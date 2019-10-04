package net.onima.onimafaction.commands.faction.arguments.staff;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;

public class FactionForceDisbandArgument extends FactionArgument {
	
	public FactionForceDisbandArgument() {
		super("forcedisband", OnimaPerm.ONIMAFACTION_FORCEDISBAND_ARGUMENT);
		
		usage = new JSONMessage("§c/f " + name + " <faction>", "§d§oDissout de force une faction.");
		
		needFaction = false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 2, true))
			return false;
		
		Faction faction = null;
		
		if ((faction = Faction.getFaction(args[1])) == null) {
			sender.sendMessage("§cImpossible de trouver la faction ou le joueur " + args[1]);
			return false;
		}
		
		Bukkit.broadcastMessage("§d§o" + sender.getName() + " §7a dissout de force la faction §d§o" + faction.getName());
		
		if (faction instanceof PlayerFaction)
			((PlayerFaction) faction).disband(null);
		else
			faction.remove();
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length != 2)
			return Collections.emptyList();
		
		return Faction.getFactions().parallelStream().map(Faction::getName).filter(name -> StringUtil.startsWithIgnoreCase(name, args[0])).collect(Collectors.toList());
	}

}
