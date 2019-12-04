package net.onima.onimafaction.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import net.onima.onimaapi.OnimaAPI;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.time.TimeUtils;
import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.timed.FactionServerEvent;
import net.onima.onimafaction.timed.TimedEvent.EventAction;
import net.onima.onimafaction.timed.event.Raidable;

public class RaidableCommand implements CommandExecutor, TabCompleter {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!OnimaPerm.RAIDABLE_COMMAND.has(sender)) {
			sender.sendMessage(OnimaAPI.UNKNOWN_COMMAND);
			return false;
		}
		
		if (args.length == 1 && args[0].equalsIgnoreCase("br"))
			OnimaFaction.getInstance().getBattleRoyale().start();
		
		if (args.length == 1 && args[0].equalsIgnoreCase("stop")) {
			FactionServerEvent factionRaidable = OnimaFaction.getInstance().getFactionServerEvent();
			
			if (factionRaidable instanceof Raidable) {
				Raidable raidable = (Raidable) factionRaidable;
				
				if (raidable.isRunning()) {
					raidable.action(EventAction.CANCELLED);
					Bukkit.broadcastMessage(raidable.getMessage(EventAction.CANCELLED).replace("%player%", Methods.getRealName(sender)));
					return true;
				} else
					sender.sendMessage("§cL'event n'est pas en cours !");
			} else {
				sender.sendMessage("§cL'event en cours n'est pas un event raidable !");
				return false;
			}
			
		} else if (args.length >= 3 && args[0].equalsIgnoreCase("start")) {
			if (OnimaFaction.getInstance().getFactionServerEvent() != null) {
				sender.sendMessage("§cIl y a déjà un event en cours !");
				return false;
			} else {
				long time = TimeUtils.timeToMillis(args[1]);
				long delay = TimeUtils.timeToMillis(args[2]);
				
				if (time == -1 || delay == -1) {
					sender.sendMessage("§c" + (time == -1 ? args[1] : args[2]) + " n'est pas un nombre !");
					return false;
				} else if (time == -2 || delay == -2) {
					sender.sendMessage("§cMauvais format, essayez comme ceci : 1mi ou 1se ou 1ye etc...");
					return false;
				} else
					new Raidable(time, delay).start(Methods.getRealName(sender));
			}
			return true;
		} else {
			sender.sendMessage("§7§m-----------------------------");
			new JSONMessage("§7/raidable start <time> <delay>", "§d§oLance un event raidable.");
			new JSONMessage("§7/raidable stop", "§d§oAnnule un event raidable.");
			sender.sendMessage("§7§m-----------------------------");
		}
			
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (OnimaPerm.RAIDABLE_COMMAND.has(sender) && args.length == 1) {
			List<String> completions = new ArrayList<>();
			
			if (StringUtil.startsWithIgnoreCase("start", args[0]))
				completions.add("start");
			
			if (StringUtil.startsWithIgnoreCase("stop", args[0]))
				completions.add("stop");
			
			return completions;
		}
		
		return Collections.emptyList();
	}
	
}
