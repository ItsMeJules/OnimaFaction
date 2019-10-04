package net.onima.onimafaction.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.timed.TimedEvent.EventAction;

public class SOTWCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!OnimaPerm.SOTW_COMMAND.has(sender)) {
			sender.sendMessage(OnimaPerm.SOTW_COMMAND.getMissingMessage());
			return false;
		}
		
		if (OnimaFaction.getInstance().getSOTW().isRunning())
			OnimaFaction.getInstance().getSOTW().action(EventAction.CANCELLED);
		else
			OnimaFaction.getInstance().getSOTW().start();
		
		return true;
	}

}
