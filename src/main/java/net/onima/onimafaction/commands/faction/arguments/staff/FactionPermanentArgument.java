package net.onima.onimafaction.commands.faction.arguments.staff;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;

public class FactionPermanentArgument extends FactionArgument {
	
	public FactionPermanentArgument() {
		super("permanent", OnimaPerm.ONIMAFACTION_PERMANENT_ARGUMENT);
		
		usage = new JSONMessage("§c/f " + name + " <faction>", "§d§oPermet de rendre une faction permanente.");
		
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
		
		faction.setPermanent(!faction.isPermanent());
		String permanent = faction.isPermanent() ? "§apermanente" : "§cnon permanente";
		
		if (faction instanceof PlayerFaction)
			((PlayerFaction) faction).broadcast("§7Votre faction a été rendue " + permanent + " §7par §d§o" + sender.getName() + "§7.");
	
		sender.sendMessage("§d§oVous §7avez rendu la faction §d§o" + faction.getName() + ' ' + permanent + "§7.");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length != 2 || !sender.hasPermission(OnimaPerm.ONIMAFACTION_PERMANENT_ARGUMENT.getPermission()))
			return Collections.emptyList();
		
		return Faction.getFactions().parallelStream().filter(faction -> StringUtil.startsWithIgnoreCase(faction.getName(), args[1])).map(Faction::getName).collect(Collectors.toList());
	}
	
}
