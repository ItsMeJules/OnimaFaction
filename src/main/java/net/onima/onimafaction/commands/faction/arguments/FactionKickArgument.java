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
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.events.FactionPlayerLeaveEvent.LeaveReason;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.Role;
import net.onima.onimafaction.players.FPlayer;
import net.onima.onimafaction.players.OfflineFPlayer;

public class FactionKickArgument extends FactionArgument {

	public FactionKickArgument() {
		super("kick", OnimaPerm.ONIMAFACTION_KICK_ARGUMENT, new String[] {"kickmember", "kickplayer", "k"});
		usage = new JSONMessage("§7/f " + name + " <player>", "§d§oKick un joueur de votre faction.");
		
		playerOnly = true;
		role = Role.OFFICER;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(checks(sender, args, 2, true))
			return false;
		
		Player player = (Player) sender;
		FPlayer fPlayer = FPlayer.getByPlayer(player);
		PlayerFaction faction = null;

		if ((faction = fPlayer.getFaction()) == null) {
			player.spigot().sendMessage(new JSONMessage("§cVous avez besoin d'une faction pour kick des joueurs !", "§a/f create ", true, "/f create ", ClickEvent.Action.SUGGEST_COMMAND).build());
			return false;
		}
		
		if (faction.isRaidable() && OnimaFaction.getInstance().getFactionServerEvent() == null) {
			player.sendMessage("§cVous ne pouvez pas kick de joueurs quand votre faction est raidable !");
			return false;
		}
		
		if (player.getName().equalsIgnoreCase(args[1])) {
			player.sendMessage("§cVous ne pouvez pas vous kick vous même !");
			return false;
		}
		
		OfflinePlayer kicked = Bukkit.getOfflinePlayer(args[1]);
		
		if (!kicked.hasPlayedBefore()) {
			player.sendMessage("§c" + name + " ne s'est jamais connecté sur le serveur !");
			return false;
		}
		
		OfflineFPlayer offlineFPlayer = OfflineFPlayer.getByOfflinePlayer(kicked);
		
		if (!faction.getMembers().contains(kicked.getUniqueId())) {
			player.sendMessage("§c" + kicked.getName() + " n'est pas dans votre faction !");
			return false;
		}
		
		Role role = offlineFPlayer.getRole();
		Role kickerRole = fPlayer.getRole();
		
		if (role.getValue() >= kickerRole.getValue()) {
			player.sendMessage("§cVous ne pouvez pas kick des " + role.getName().toLowerCase() + " de votre faction !");
			faction.broadcast("§d§o" + kickerRole.getRole() + player.getName() + " §7a essayé de kick §d§o" + role.getRole() + kicked.getName() + " §7de la faction !");
			return false;
		}
		
		if (faction.removeMember(offlineFPlayer, LeaveReason.KICK, player)) {
			if (kicked.isOnline()) 
				kicked.getPlayer().sendMessage("§d§o" + kickerRole.getRole() + player.getName() + " §7vous a kick de la faction !");
			faction.broadcast(new JSONMessage("§d§o" + kickerRole.getRole() + player.getName() + " §7a kick §d§o" + kicked.getName() + " §7de la faction !", "§a/f invite " + kicked.getName(), true, "/f invite " + kicked.getName()));
			return true;
		}
		
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) || args.length != 2) 
			return Collections.emptyList();
		
		Player player = (Player) sender;
		FPlayer fPlayer = FPlayer.getByPlayer(player);
		PlayerFaction faction = null;
		
		if ((faction = fPlayer.getFaction()) != null) {
			Role role = fPlayer.getRole();
			
			if (role.isAtLeast(Role.OFFICER)) {
				List<String> toKick = new ArrayList<>();
				
				for (UUID uuid : faction.getMembers()) {
					OfflineFPlayer offline = OfflineFPlayer.getByUuid(uuid);
					
					if (role.getValue() <= offline.getRole().getValue()) continue;
					
					if (StringUtil.startsWithIgnoreCase(offline.getOfflineApiPlayer().getName(), args[1]))
						toKick.add(offline.getOfflineApiPlayer().getName());
				}
				return toKick;
			}
		}
		return Collections.emptyList();
	}

}
