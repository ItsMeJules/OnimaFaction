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
import org.bukkit.util.StringUtil;

import net.md_5.bungee.api.chat.ClickEvent;
import net.onima.onimaapi.caching.UUIDCache;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.Role;
import net.onima.onimafaction.players.FPlayer;
import net.onima.onimafaction.players.OfflineFPlayer;

public class FactionLeaderArgument extends FactionArgument {

	public FactionLeaderArgument() {
		super("leader", OnimaPerm.ONIMAFACTION_LEADER_ARGUMENT, new String[] {"admin"});
		usage = new JSONMessage("§7/f " + name + " <player>", "§d§oDéfini le leader de votre faction.");
		
		playerOnly = true;
		role = Role.LEADER;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 2, true))
			return false;
		
		Player player = (Player) sender;
		FPlayer fPlayer = FPlayer.getPlayer(player);
		PlayerFaction faction = fPlayer.getFaction();
		
		if (faction == null) {
			player.spigot().sendMessage(new JSONMessage("§cVous avez besoin d'une faction pour pouvoir définir un leader !", "§a/f create ", true, "/f create ", ClickEvent.Action.SUGGEST_COMMAND).build());
			return false;
		}
		
		OfflinePlayer target = Bukkit.getOfflinePlayer(UUIDCache.getUUID(args[1]));
		
		if (!target.hasPlayedBefore() && !target.isOnline()) {
			player.sendMessage("§c" + args[1] + " ne s'est jamais connecté sur le serveur !");
			return false;
		}
		
		OfflineFPlayer.getPlayer(target, offline -> {
			if (target.getName().equalsIgnoreCase(player.getName())) {
				player.sendMessage("§cVous êtes déjà leader de votre faction !");
				return;
			}
			
			offline.setRole(Role.LEADER);
			fPlayer.setRole(Role.COLEADER);
			faction.broadcast("§d§o" + fPlayer.getApiPlayer().getName() + " a donné son rôle de leader à §d§o" + Methods.getRealName(target) + "§7.");
		});
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player && args.length == 2) {
			Player player = (Player) sender;
			FPlayer fPlayer = FPlayer.getPlayer(player);
			
			if (fPlayer.getRole() == Role.LEADER) {
				List<String> completions = new ArrayList<>(19);
				
				for (UUID uuid : fPlayer.getFaction().getMembersUUID()) {
					OfflineFPlayer offline = OfflineFPlayer.getOfflineFPlayers().get(uuid);
					
					if (offline == null || offline.getRole() == Role.LEADER || !StringUtil.startsWithIgnoreCase(offline.getOfflineApiPlayer().getName(), args[1]))
						continue;
					
					completions.add(offline.getOfflineApiPlayer().getName());
				}
				
				return completions;
			}
		}
		return Collections.emptyList();
	}
	
}
