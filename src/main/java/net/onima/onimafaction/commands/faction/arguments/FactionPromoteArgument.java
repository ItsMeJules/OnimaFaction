package net.onima.onimafaction.commands.faction.arguments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.Role;
import net.onima.onimafaction.players.FPlayer;
import net.onima.onimafaction.players.OfflineFPlayer;

public class FactionPromoteArgument extends FactionArgument {

	public FactionPromoteArgument() {
		super("promote", OnimaPerm.ONIMAFACTION_PROMOTE_ARGUMENT);
		usage = new JSONMessage("§7/f " + name + " <player>", "§d§oPromeut un joueur au rang suivant.");
		
		playerOnly = true;
		role = Role.COLEADER;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 2, true))
			return false;
		
		Player player = (Player) sender;
		FPlayer fPlayer = FPlayer.getByPlayer(player);
		PlayerFaction faction = null;
		
		if ((faction = fPlayer.getFaction()) == null) {
			player.spigot().sendMessage(new JSONMessage("§cVous avez besoin d'une faction pour promouvoir un joueur !", "§a/f create ", true, "/f create ", ClickEvent.Action.SUGGEST_COMMAND).build());
			return false;
		}
		
		OfflinePlayer offline = Bukkit.getOfflinePlayer(args[1]);
		
		if (!offline.hasPlayedBefore()) {
			player.sendMessage("§c" + args[1] + " ne s'est jamais connecté sur le serveur !");
			return false;
		}
		
		OfflineFPlayer offlineFPlayer = OfflineFPlayer.getByOfflinePlayer(offline);
		
		if (!faction.getMembers().contains(offline.getUniqueId())) {
			player.sendMessage("§c" + offline.getName() + " n'est pas dans votre faction !");
			return false;
		}
		
		if (player.getUniqueId().equals(offlineFPlayer.getOfflineApiPlayer().getUUID())) {
			player.sendMessage("§cVous ne pouvez pas vous grader vous même !");
			return false;
		}
		
		Role playerRole = fPlayer.getRole();
		Role offlineRole = offlineFPlayer.getRole();
		
		if (offlineRole.getValue() < playerRole.getValue()) {
			player.sendMessage("§cVous ne pouvez pas promouvoir des joueurs qui ont un rôle supérieur ou égal au votre !");
			return false;
		}
		
		if (offlineRole == Role.COLEADER) {
			player.sendMessage("§cVous ne pouvez pas promouvoir au-dessus de " + Role.LEADER.getName().toLowerCase() + " !");
			return false;
		}
		
		offlineFPlayer.setRole(Role.fromValue(offlineRole.getValue() + 1));
		faction.broadcast(new JSONMessage("§d§o" + fPlayer.getRole().getRole() + player.getName() + " §7a promu §d§o" + offline.getName() + " §7au rôle de §d§o" + offlineRole.getName().toLowerCase(), "§a/f demote " + offline.getName(), true, "/f demote " + offline.getName()));
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!checks(sender, args, 2, false) || args.length == 2) {
		
			Player player = (Player) sender;
			FPlayer fPlayer = FPlayer.getByPlayer(player);
			PlayerFaction faction = null;
		
			if ((faction = fPlayer.getFaction()) != null) {
				int roleValue = fPlayer.getRole().getValue();
				List<String> completions = new ArrayList<>();
				
				for (UUID uuid : faction.getMembers()) {
					OfflineFPlayer offlineFPlayer = OfflineFPlayer.getByUuid(uuid);
					
					if (offlineFPlayer.getRole().getValue() > roleValue && offlineFPlayer.getRole() != Role.COLEADER)
						continue;
					
					completions.add(offlineFPlayer.getOfflineApiPlayer().getName());
				}
				
				return completions;
			}
		}
		
		return Collections.emptyList();
	}
	
}
