package net.onima.onimafaction.commands.faction.arguments.staff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.events.FactionDTRChangeEvent.DTRChangeCause;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;

public class FactionDtrArgument extends FactionArgument {
	
	public FactionDtrArgument() {
		super("dtr", OnimaPerm.ONIMAFACTION_DTR_ARGUMENT, new String[] {"stdtr"});

		usage = new JSONMessage("§c/f " + name + " <faction> <dtr>", "§d§oPermet de modifier le dtr d'une faction.");
		
		needFaction = false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 3, true))
			return false;
		
		Float dtr = Methods.toFloat(args[2]);
		
		if (dtr == null) {
			sender.sendMessage("§c" + args[2] + " n'est pas un nombre !");
			return false;
		}
		
		if (args[1].equalsIgnoreCase("all")) { 
			for (PlayerFaction faction : PlayerFaction.getPlayersFaction().values())
				faction.setDTR(dtr, DTRChangeCause.ADMIN);
			
			Bukkit.broadcastMessage("§d§o" + sender.getName() + " §7a défini le DTR de toutes les factions sur : §d§o" + dtr);
			return true;
		}
		
		Faction maybeFaction = null;
		
		if ((maybeFaction = Faction.getFaction(args[1])) == null) {
			sender.sendMessage("§cImpossible de trouver la faction ou le joueur " + args[1]);
			return false;
		}
		
		if (!(maybeFaction instanceof PlayerFaction)) {
			sender.sendMessage("§cVous pouvez seulement changer le dtr des factions de joueurs !");
			return false;
		}
		
		PlayerFaction faction = (PlayerFaction) maybeFaction;
		Float oldDtr = faction.getDTR();
		
		faction.setDTR(dtr, DTRChangeCause.ADMIN);
		Bukkit.broadcastMessage("§d§o" + sender.getName() + " §7a changé le DTR de §d§o" + faction.getName() + " §7qui était de §d§o" + oldDtr + " §7en §7§o" +faction.getDTR() + "§7.");
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length != 2)
			return Collections.emptyList();
		
		List<String> completions = new ArrayList<>();
		
		if (StringUtil.startsWithIgnoreCase("all", args[1])) {
			completions.add("all");
			return completions;
		}
		
		for (PlayerFaction faction : PlayerFaction.getPlayersFaction().values()) {
			if (StringUtil.startsWithIgnoreCase(faction.getName(), args[1]))
				completions.add(faction.getName());
		}
		
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (StringUtil.startsWithIgnoreCase(online.getName(), args[1]))
				completions.add(online.getName());
		}
		
		return completions;
	}

}
