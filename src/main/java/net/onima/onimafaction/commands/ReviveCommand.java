package net.onima.onimafaction.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import net.onima.onimaapi.OnimaAPI;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.players.Deathban;
import net.onima.onimafaction.players.OfflineFPlayer;

public class ReviveCommand implements CommandExecutor, TabCompleter {
	
	private JSONMessage usage = new JSONMessage("§7/revive <player>", "§d§oRessucite un joueur deathban.");

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!OnimaPerm.REVIVE_COMMAND.has(sender)) {
			sender.sendMessage(OnimaAPI.UNKNOWN_COMMAND);
			return false;
		}
		
		if (args.length < 1) {
			usage.send(sender, "§7Utilisation : ");
			return false;
		}
		
		OfflineFPlayer.getPlayer(args[0], offline -> {
			if (offline == null) {
				sender.sendMessage("§c" + args[0] + " ne s'est jamais connecté sur le serveur !");
				return;
			}
			
			Deathban deathban = offline.getDeathban();
			
			if ((deathban == null || !deathban.isActive()) && !deathban.isEotwDeathban()) {
				sender.sendMessage("§c" + Methods.getName(offline.getOfflineApiPlayer(), true) + " n'est pas deathban !");
				return;
			}
			
			sender.sendMessage("§dVous §7avez revive §d" + offline.getOfflineApiPlayer().getName());
			offline.setDeathban(null);
		});
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (OnimaPerm.REVIVE_COMMAND.has(sender) || args.length != 1) 
			return Collections.emptyList();
		
		List<String> completions = new ArrayList<>();
		
		for (OfflineFPlayer offline : OfflineFPlayer.getDisconnectedOfflineFPlayers()) {
			
			if (offline.getDeathban() != null && offline.getDeathban().isActive()) {
				if (StringUtil.startsWithIgnoreCase(offline.getOfflineApiPlayer().getOfflinePlayer().getName(), args[0]))
					completions.add(offline.getOfflineApiPlayer().getOfflinePlayer().getName());
			}
		}
		
		return completions;
	}

}
