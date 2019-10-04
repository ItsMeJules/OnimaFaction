package net.onima.onimafaction.commands.faction.arguments.staff;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.Role;
import net.onima.onimafaction.players.OfflineFPlayer;

public class FactionForceMemberArgument extends FactionArgument {

	public FactionForceMemberArgument() {
		super("forcemember", OnimaPerm.ONIMAFACTION_FORCEMEMBER_ARGUMENT);
		
		usage = new JSONMessage("§c/f " + name + " <player>", "§d§oPermet de définir de force un membre dans une faction.");
		
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
		PlayerFaction faction = null;
		
		if ((faction = fPlayer.getFaction()) == null) {
			sender.sendMessage("§c" + offline.getName() + " n'a pas de faction !");
			return false;
		}
		
		if (fPlayer.getRole() == Role.MEMBER) {
			sender.sendMessage("§c" + offline.getName() + " est déjà leader de sa faction !");
			return false;
		}
		
		fPlayer.setRole(Role.MEMBER);
		
		sender.sendMessage("§d§oVous §7avez défini le rôle de §d§o" + offline.getName() + " §7sur membre.");
		faction.broadcast("§d§o" + sender.getName() + " §7a défini §d§o" + offline.getName() + " §7en tant que membre.");
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length != 2)
			return Collections.emptyList();

		return Arrays.stream(Bukkit.getOfflinePlayers()).filter(offline -> OfflineFPlayer.getByOfflinePlayer(offline).getRole() != Role.MEMBER).map(OfflinePlayer::getName).filter(name -> StringUtil.startsWithIgnoreCase(name, args[1])).collect(Collectors.toList());
	}
}
