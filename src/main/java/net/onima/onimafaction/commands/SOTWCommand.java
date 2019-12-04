package net.onima.onimafaction.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.onima.onimaapi.OnimaAPI;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.timed.TimedEvent.EventAction;

public class SOTWCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!OnimaPerm.SOTW_COMMAND.has(sender)) {
			sender.sendMessage(OnimaAPI.UNKNOWN_COMMAND);
			return false;
		}
		
		if (OnimaFaction.getInstance().getSOTW().isRunning()) {
			OnimaFaction.getInstance().getSOTW().action(EventAction.CANCELLED);
			Bukkit.broadcastMessage(OnimaFaction.getInstance().getSOTW().getMessage(EventAction.CANCELLED).replace("%player%", Methods.getRealName(sender)));
		} else
			OnimaFaction.getInstance().getSOTW().start(Methods.getRealName(sender));
		
		return true;
	}

}
