package net.onima.onimafaction.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.timed.TimedEvent.EventAction;

public class EOTWCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!OnimaPerm.EOTW_COMMAND.has(sender)) {
			sender.sendMessage(OnimaPerm.EOTW_COMMAND.getMissingMessage());
			return false;
		}
		
		if (OnimaFaction.getInstance().getEOTW().isRunning())
			OnimaFaction.getInstance().getEOTW().action(EventAction.CANCELLED);
		else
			OnimaFaction.getInstance().getEOTW().start();
		
		return true;
	}

}
