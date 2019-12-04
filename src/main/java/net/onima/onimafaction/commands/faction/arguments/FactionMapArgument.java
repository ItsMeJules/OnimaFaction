package net.onima.onimafaction.commands.faction.arguments;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.players.FPlayer;

public class FactionMapArgument extends FactionArgument {

	public FactionMapArgument() {
		super("map", OnimaPerm.ONIMAFACTION_MAP_ARGUMENT);
		
		usage = new JSONMessage("§7/f " + name, "§d§oAffiche les claims autour de vous.");
		
		playerOnly = true;
		needFaction = false;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 1, true))
			return false;
		
		Player player = (Player) sender;
		FPlayer fPlayer = FPlayer.getPlayer(player);
		
		fPlayer.setfMap(!fPlayer.hasfMap(), true);
		return true;
	}

}
