package net.onima.onimafaction.commands.faction.arguments.staff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import com.google.common.collect.Sets;

import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.zone.struct.Flag;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.Faction;

public class FactionFlagArgument extends FactionArgument {

	private static final Set<String> ARGS = Sets.newHashSet("list", "add", "remove");
	
	public FactionFlagArgument() {
		super("flag", OnimaPerm.ONIMAFACTION_FLAG_ARGUMENT);
		
		usage = new JSONMessage("§c/f " + name + " <faction> <add | list | remove> <flag>", "§d§oPermet de gérer les flags d'une faction.");
		
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
		
		if (args.length < 4) {
			if (args[2].equalsIgnoreCase("list")) {
				sender.sendMessage("§7Voici tous les flags de la faction §d§o" + faction.getName() + " §7:");
				for (Flag flag : faction.getFlags()) {
					String command = "f flag " + faction.getName() + " remove " + flag.getName();
					
					new JSONMessage(" §7- " + flag.getName(), "§a/" + command, true, command).send(sender);
				}
				return true;
			} else 
				usage.send(sender, "§7Utilisation : ");
		} else {
			Flag flag = Flag.fromName(args[3]);
			
			if (flag == null) {
				sender.sendMessage("§cLe flag " + args[3] + " n'existe pas !");
				return false;
			}
			
			if (args[2].equalsIgnoreCase("add")) {
				if (faction.hasFlag(flag)) {
					sender.sendMessage("§cLa faction " + faction.getName() + " a déjà le flag " + flag.getName() + '.');
					return false;
				}
				
				faction.addFlag(flag);
				sender.sendMessage("§d§oVous §7avez §aajouté §7le flag §d§o" + flag.getName() + " §7a la faction §d§o" + faction.getName() + "§7.");
				return true;
			} else if (args[2].equalsIgnoreCase("remove")) {
				if (!faction.hasFlag(flag)) {
					sender.sendMessage("§cLa faction " + faction.getName() + " n'a pas le flag " + flag.getName() + '.');
					return false;
				}
				
				faction.removeFlag(flag);
				sender.sendMessage("§d§oVous §7avez §csupprimé §7le flag §d§o" + flag.getName() + " §7a la faction §d§o" + faction.getName() + "§7.");
				return true;
			} else
				usage.send(sender, "§7Utilisation : ");
		}
		
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length < 2 || !sender.hasPermission(OnimaPerm.ONIMAFACTION_FLAG_ARGUMENT.getPermission()))
			return Collections.emptyList();
		
		List<String> completions = new ArrayList<>();
		
		if (args.length == 2) {
			for (Faction faction : Faction.getFactions()) {
				if (faction.isWilderness())
					continue;
				
				String factionName = faction.getName();
				
				if (StringUtil.startsWithIgnoreCase(factionName, args[1]))
					completions.add(ChatColor.stripColor(factionName));
			}
		} else if (args.length == 3) {
			for (String arg : ARGS) {
				if (StringUtil.startsWithIgnoreCase(arg, args[2]))
					completions.add(arg);
			}
		} else if (args.length == 4) {
			Faction faction = Faction.getFaction(args[1]);
			
			if (faction != null) {
				for (Flag flag : Flag.values()) {//TODO Optimize
					if (faction.hasFlag(flag) && args[2].equalsIgnoreCase("add")) continue;
					if (!faction.hasFlag(flag) && args[2].equalsIgnoreCase("remove")) continue;
					
					String flagName = flag.getName();
					
					if (StringUtil.startsWithIgnoreCase(flagName, args[3]))
						completions.add(flagName);
				}
			}
		}
			
		return completions;
	}

}
