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

public class FactionDemoteArgument extends FactionArgument {

	public FactionDemoteArgument() {
		super("demote", OnimaPerm.ONIMAFACTION_DEMOTE_ARGUMENT, new String[] {"derank", "unrank"});
		usage = new JSONMessage("§7/f " + name + " <player>", "§d§oPermet de dégrader un joueur.");
		
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
			player.spigot().sendMessage(new JSONMessage("§cVous avez besoin d'une faction pour dégrader un joueur !", "§a/f create ", true, "/f create ", ClickEvent.Action.SUGGEST_COMMAND).build());
			return false;
		}
		
		OfflinePlayer offline = Bukkit.getOfflinePlayer(args[1]);
		OfflineFPlayer offlineFPlayer = OfflineFPlayer.getByOfflinePlayer(offline);
		
		if (!offline.hasPlayedBefore()) {
			player.sendMessage("§c" + args[1] + " ne s'est jamais connecté sur le serveur !");
			return false;
		}
		
		if (!faction.getMembers().contains(offline.getUniqueId())) {
			player.sendMessage("§c" + offline.getName() + " n'est pas dans votre faction !");
			return false;
		}
		
		if (player.getUniqueId().equals(offlineFPlayer.getOfflineApiPlayer().getUUID())) {
			player.sendMessage("§cVous ne pouvez pas vous dégrader vous même !");
			return false;
		}
		
		Role playerRole = fPlayer.getRole();
		Role offlineRole = offlineFPlayer.getRole();
		
		if (offlineRole.getValue() < playerRole.getValue()) {
			player.sendMessage("§cVous ne pouvez pas dégrader un joueur ayant un rôle supérieur ou égal au votre !");
			return false;
		}
		
		if (offlineRole == Role.MEMBER) {
			player.sendMessage("§cVous ne pouvez pas dégrader en-dessous de " + Role.MEMBER.getName().toLowerCase() + " !");
			return false;
		}
		
		offlineFPlayer.setRole(offlineRole = Role.fromValue(offlineRole.getValue() - 1));
		faction.broadcast(new JSONMessage("§d§o" + playerRole.getRole() + player.getName() + " §7a dégradé §d§o" + offline.getName() + " §7au rôle de §d§o" + offlineRole.getName().toLowerCase(), "§a/f promote" + offline.getName(), true, "/f promote" + offline.getName()));
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!checks(sender, args, 2, false) || args.length == 2) {
		
			Player player = (Player) sender;
			FPlayer fPlayer = FPlayer.getByPlayer(player);
			PlayerFaction faction = null;
		
			if ((faction = fPlayer.getFaction()) != null) {
				Role playerRole = fPlayer.getRole();
				List<String> completions = new ArrayList<>();
				
				for (UUID uuid : faction.getMembers()) {
					OfflineFPlayer offlineFPlayer = OfflineFPlayer.getByUuid(uuid);
					Role memberRole = offlineFPlayer.getRole();
					
					if (memberRole.getValue() < playerRole.getValue() && memberRole != Role.MEMBER)
						completions.add(offlineFPlayer.getOfflineApiPlayer().getName());
				}
				
				return completions;
			}
		}
		
		return Collections.emptyList();
	}

}
