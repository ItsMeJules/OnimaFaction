package net.onima.onimafaction.commands.faction.arguments.staff;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.onima.onimaapi.caching.UUIDCache;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.events.FactionPlayerLeaveEvent.LeaveReason;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.Role;
import net.onima.onimafaction.players.OfflineFPlayer;

public class FactionForceKickArgument extends FactionArgument {
	
	public FactionForceKickArgument() {
		super("forcekick", OnimaPerm.ONIMAFACTION_FORCEKICK_ARGUMENT);
		
		usage = new JSONMessage("§c/f " + name + " <player>", "§d§oPermet de kick de force un joueur d'une faction.");
	
		needFaction = false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 2, true))
			return false;
		
		OfflinePlayer offline = Bukkit.getOfflinePlayer(UUIDCache.getUUID(args[1]));
		
		if (!offline.hasPlayedBefore()) {
			sender.sendMessage("§c" + args[1] + " ne s'est jamais connecté sur le serveur !");
			return false;
		}
		
		OfflineFPlayer.getPlayer(offline, fPlayer -> {
			PlayerFaction faction = fPlayer.getFaction();
			
			if (faction == null) {
				sender.sendMessage("§c" + Methods.getName(fPlayer.getOfflineApiPlayer(), true) + " n'a pas de faction !");
				return;
			}
			
			if (fPlayer.getRole() == Role.LEADER) {
				sender.sendMessage("§cVous ne pouvez pas kick de force le leader de la faction, vous devez d'abord le dégrader !");
				return;
			}
			
			if (faction.removeMember(fPlayer, LeaveReason.KICK, sender)) {
				faction.broadcast("§d§o" + Methods.getRealName(sender) + " §7a kick de force §d§o" + Methods.getRealName(offline) + " §7de la faction !");
				sender.sendMessage("§d§oVous §7avez bien kick §d§o" + Methods.getName(fPlayer.getOfflineApiPlayer(), true) + " §7de sa faction !");
			}
		});
		
		return true;
	}

}
