package net.onima.onimafaction.commands.faction.arguments.staff;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;

public class FactionDeathBanMultiplierArgument extends FactionArgument {
	
	public FactionDeathBanMultiplierArgument() {
		super("deathbanmultiplier", OnimaPerm.ONIMAFACTION_DEATHBANMULTIPLIER_ARGUMENT);
		
		usage = new JSONMessage("§c/f " + name + " <faction> <multiplier>", "§d§oPermet de modifier le deathban multiplier d'une faction.");
		
		needFaction = false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 3, true))
			return false;
			
		Faction faction = null;
		
		if ((faction = Faction.getFaction(args[1])) == null) {
			sender.sendMessage("§cImpossible de trouver la faction ou le joueur " + args[1]);
			return false;
		}
		
		Double multiplier = Methods.toDouble(args[2]);
		
		if (multiplier == null) {
			sender.sendMessage("§c" + args[2] + " n'est pas un nombre !");
			return false;
		}
		
		sender.sendMessage("§d§oVous §7avez défini le deathban multiplier de §d§o" + faction.getName() + " §7sur §d§o" + multiplier + "§7.");
		if (faction instanceof PlayerFaction)
			((PlayerFaction) faction).broadcast("§d§o" + Methods.getRealName(sender) + " §7a défini le deathban multiplier de §d§ovotre §7faction sur §d§o" + multiplier + "§7.");
		
		faction.getClaims().parallelStream().forEach(claim -> claim.setDeathbanMultiplier(multiplier));
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length != 2 || sender.hasPermission(OnimaPerm.ONIMAFACTION_DEATHBANMULTIPLIER_ARGUMENT.getPermission()))
			return Collections.emptyList();
		
		return Faction.getFactions().parallelStream().filter(faction -> StringUtil.startsWithIgnoreCase(faction.getName(), args[1])).map(Faction::getName).collect(Collectors.toList());
	}

}
