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

public class EOTWCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!OnimaPerm.EOTW_COMMAND.has(sender)) {
			sender.sendMessage(OnimaAPI.UNKNOWN_COMMAND);
			return false;
		}
		
		if (OnimaFaction.getInstance().getEOTW().isRunning()) {
			OnimaFaction.getInstance().getEOTW().action(EventAction.CANCELLED);
			Bukkit.broadcastMessage(OnimaFaction.getInstance().getEOTW().getMessage(EventAction.CANCELLED).replace("%player%", Methods.getRealName(sender)));
		} else
			OnimaFaction.getInstance().getEOTW().start(Methods.getRealName(sender));
		
		return true;
	}

}
