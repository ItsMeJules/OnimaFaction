package net.onima.onimafaction.commands.faction.arguments.staff;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
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

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 2, true))
			return false;
		
		OfflinePlayer offline = null;
		
		if ((offline = Bukkit.getOfflinePlayer(args[1])) == null) {
			sender.sendMessage("§c" + args[1] + " ne s'est jamais connecté sur le serveur !");
			return false;
		}
		
		OfflineFPlayer fPlayer = OfflineFPlayer.getByOfflinePlayer(offline);
		PlayerFaction faction = fPlayer.getFaction();
		
		if (faction == null) {
			sender.sendMessage("§c" + offline.getName() + " n'a pas de faction !");
			return false;
		}
		
		if (fPlayer.getRole() == Role.LEADER) {
			sender.sendMessage("§cVous ne pouvez pas kick de force le leader de la faction, vous devez d'abord le dégrader !");
			return false;
		}
		
		if (faction.removeMember(fPlayer, LeaveReason.KICK, sender)) {
			faction.broadcast("§d§o" + sender.getName() + " §7a kick de force §d§o" + offline.getName() + " §7de la faction !");
			sender.sendMessage("§d§oVous §7avez bien kick §d§o" + offline.getName() + " §7de sa faction !");
			return true;
		}
		
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length != 2)
			return Collections.emptyList();

		return Arrays.stream(Bukkit.getOfflinePlayers()).filter(offline -> OfflineFPlayer.getByOfflinePlayer(offline).getRole() != Role.LEADER).map(OfflinePlayer::getName).collect(Collectors.toList());
	}
	
}
