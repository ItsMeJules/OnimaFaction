package net.onima.onimafaction.commands.faction.arguments.staff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.players.FPlayer;

public class FactionChatSpyArgument extends FactionArgument {
	
	private static List<String> consoleSpies;
	
	static {
		consoleSpies = new ArrayList<>();
	}
	
	public FactionChatSpyArgument() {
		super("chatspy", OnimaPerm.ONIMAFACTION_CHATSPY_ARGUMENT, new String[] {"cs", "spychat", "sc"});
		
		usage = new JSONMessage("§c/f " + name + " <list | all | faction>", "§d§oEspionne des chats de faction.");
	
		needFaction = false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 2, true))
			return false;
		
		List<String> spies = (sender instanceof ConsoleCommandSender) ? consoleSpies : FPlayer.getPlayer((Player) sender).getFactionSpying();
				
		if (args[1].equalsIgnoreCase("list")) {
			if (spies.isEmpty()) {
				sender.sendMessage("§cVous n'espionnez aucun chat de faction !");
				return false;
			} else if(spies.contains("all")) {
				sender.sendMessage("§d§oVous §7espionnez tous les chats de faction !");
				return true;
			}
			
			sender.sendMessage("§d§oVous §7espionnez §d§o" + spies.size() + " §7chat" + (spies.size() > 1 ? "s" : "") + " de faction :");
			ComponentBuilder builder = new ComponentBuilder("");
			
			for (String name : spies) {
				builder.append(" §7- §d" + name + "\n");
				builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f sc " + name));
				builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§c/f sc " + name).create()));
			}
			
			Methods.sendJSON(sender, builder.create());
			return true;
		} else if (args[1].equalsIgnoreCase("all")) {
			if (spies.contains("all")) {
				spies.remove("all");
				sender.sendMessage("§d§oVous §7avez arrêté d'espionner le chat de §d§otoutes §7les factions.");
			} else {
				spies.clear();
				spies.add("all");
				sender.sendMessage("§d§oVous §7espionnez maintenant le chat de §d§otoutes §7les factions.");
			}
			
			return true;
		}
		
		
		Faction faction = null;
		
		if ((faction = Faction.getFaction(args[1])) == null) {
			sender.sendMessage("§cImpossible de trouver la faction ou le joueur " + args[1]);
			return false;
		}
		
		if (!(faction instanceof PlayerFaction)) {
			sender.sendMessage("§cVous pouvez seulement espionner les factions de joueurs !");
			return false;
		}
		
		if (spies.contains(faction.getName())) {
			sender.sendMessage("§d§oVous §7avez arrêté d'espionner §d§o" + faction.getName() + "§7.");
			spies.remove(faction.getName());
			return true;
		} else if (spies.contains("all"))
			sender.sendMessage("§cVous êtes déjà entrain d'espionner le chat de toutes les factions.");
		else {
			sender.sendMessage("§d§oVous §7espionnez maintenant le chat de §d§o" + faction.getName() + "§7.");
			spies.add(faction.getName());
			return true;
		}
		
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length != 2)
			return Collections.emptyList();
		
		List<String> completions = new ArrayList<>();
		List<String> spies = (sender instanceof ConsoleCommandSender) ? consoleSpies : FPlayer.getPlayer((Player) sender).getFactionSpying();
		
		if (StringUtil.startsWithIgnoreCase("all", args[1]))
			completions.add("all");
		
		if (StringUtil.startsWithIgnoreCase("list", args[1]))
			completions.add("list");
		
		if (spies.contains("all"))
			return completions;
		else {
			for (PlayerFaction faction : PlayerFaction.getPlayersFaction().values()) {
				if (StringUtil.startsWithIgnoreCase(faction.getName(), args[1])) 
					completions.add(faction.getName());
			}
			
			completions.addAll(super.onTabComplete(sender, cmd, label, args));
		}
		
		return completions;
	}
	
	public static List<String> getConsoleSpies() {
		return consoleSpies;
	}
	
}
