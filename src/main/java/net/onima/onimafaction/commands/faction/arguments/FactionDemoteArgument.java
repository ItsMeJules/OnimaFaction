package net.onima.onimafaction.commands.faction.arguments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.onima.onimaapi.caching.UUIDCache;
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
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 2, true))
			return false;
		
		Player player = (Player) sender;
		FPlayer fPlayer = FPlayer.getPlayer(player);
		PlayerFaction faction = fPlayer.getFaction();
		
		if (faction == null) {
			player.spigot().sendMessage(new JSONMessage("§cVous avez besoin d'une faction pour dégrader un joueur !", "§a/f create ", true, "/f create ", ClickEvent.Action.SUGGEST_COMMAND).build());
			return false;
		}
		
		UUID uuid = UUIDCache.getUUID(args[1]);
		
		if (uuid == null) {
			player.sendMessage("§c" + args[2] + " ne s'est jamais connecté sur le serveur !");
			return false;
		}
		
		if (!faction.getMembers().containsKey(uuid)) {
			player.sendMessage("§c" + args[1] + " n'est pas dans votre faction !");
			return false;
		}
		
		if (player.getUniqueId().equals(uuid)) {
			player.sendMessage("§cVous ne pouvez pas vous dégrader vous même !");
			return false;
		}
		
		OfflineFPlayer.getPlayer(uuid, offlineFPlayer -> {
			Role playerRole = fPlayer.getRole();
			Role offlineRole = offlineFPlayer.getRole();
			
			if (offlineRole.getValue() < playerRole.getValue()) {
				player.sendMessage("§cVous ne pouvez pas dégrader un joueur ayant un rôle supérieur ou égal au votre !");
				return;
			}
			
			if (offlineRole == Role.MEMBER) {
				player.sendMessage("§cVous ne pouvez pas dégrader en-dessous de " + Role.MEMBER.getName().toLowerCase() + " !");
				return;
			}
			
			String promoteCmd = "/f promote" + offlineFPlayer.getOfflineApiPlayer().getName();
			
			offlineFPlayer.setRole(offlineRole = Role.fromValue(offlineRole.getValue() - 1));
			faction.broadcast(new JSONMessage("§d§o" + playerRole.getRole() + fPlayer.getApiPlayer().getName() + " §7a dégradé §d§o" + offlineFPlayer.getOfflineApiPlayer().getName() + " §7au rôle de §d§o" + offlineRole.getName().toLowerCase(), "§a" + promoteCmd, true, promoteCmd));
		});
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!checks(sender, args, 2, false) || args.length == 2) {
		
			Player player = (Player) sender;
			FPlayer fPlayer = FPlayer.getPlayer(player);
			PlayerFaction faction = null;
		
			if ((faction = fPlayer.getFaction()) != null) {
				Role playerRole = fPlayer.getRole();
				List<String> completions = new ArrayList<>();
				
				for (UUID uuid : faction.getMembersUUID()) {
					OfflineFPlayer offlineFPlayer = OfflineFPlayer.getOfflineFPlayers().get(uuid);
					
					if (offlineFPlayer == null)
						continue;
					
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
