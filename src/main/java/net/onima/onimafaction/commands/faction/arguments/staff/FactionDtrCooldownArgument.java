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
import net.onima.onimaapi.rank.RankType;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.time.Time.LongTime;
import net.onima.onimaapi.utils.time.TimeUtils;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;

public class FactionDtrCooldownArgument extends FactionArgument {

	public FactionDtrCooldownArgument() {
		super("dtrcooldown", OnimaPerm.ONIMAFACTION_DTR_COOLDOWN_ARGUMENT);
		
		needFaction = false;
		usage = new JSONMessage("§7/f " + name + " <faction> <time>", "§d§oEnlève le dtr freeze d'une faction.");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 3, true))
			return false;
		
		long dtrCooldown = TimeUtils.timeToMillis(args[2]);
		
		if (dtrCooldown == -1) {
			sender.sendMessage("§c" + args[2] + " n'est pas un nombre !");
			return false;
		} else if(dtrCooldown == -2) {
			sender.sendMessage("§cMauvais format, vous devez écrire comme ceci : /f dtrcooldown " + args[1] + " 1ho ou 15mi etc.");
			return false; 
		}
		
		if (args[1].equalsIgnoreCase("all")) {
			for (PlayerFaction faction : PlayerFaction.getPlayersFaction().values())
				faction.setRegenCooldown(dtrCooldown);
			
			Bukkit.broadcastMessage(RankType.getRank(sender).getNameColor() + Methods.getRealName(sender) + " §7a changé le DTR cooldown de §etoutes les factions §7pour §e" + LongTime.setYMDWHMSFormat(dtrCooldown) + "§7.");
			return true;
		}
		
		Faction faction = Faction.getFaction(args[1]);
		
		if (faction == null) {
			sender.sendMessage("§cLa faction ou le joueur " + args[1] + " n'existe pas !");
			return false;
		}
		
		if (!(faction instanceof PlayerFaction)) {
			sender.sendMessage("§cSeulement les factions de joueurs peuvent avoir un DTR freeze.");
			return false;
		}
		
		PlayerFaction playerFaction = (PlayerFaction) faction;
		String format = LongTime.setYMDWHMSFormat(dtrCooldown);
		
		playerFaction.setRegenCooldown(dtrCooldown);
		playerFaction.broadcast(RankType.getRank(sender).getNameColor() + Methods.getRealName(sender) + " §7a changé le DTR cooldown de §evotre faction §7pour §e" + format + "§7.");
		sender.sendMessage("§dVous §7avez changé le DTR cooldown de la faction §e" + playerFaction.getName() + " §7pour §e" + format + "§7.");
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!OnimaPerm.ONIMAFACTION_DTR_COOLDOWN_ARGUMENT.has(sender) || args.length != 2)
			return Collections.emptyList();
		
		List<String> completions = new ArrayList<>();
		
		if (StringUtil.startsWithIgnoreCase("all", args[1]))
			completions.add("all");
		
		for (PlayerFaction faction : PlayerFaction.getPlayersFaction().values()) {
			if (StringUtil.startsWithIgnoreCase(faction.getName(), args[1]))
				completions.add(faction.getName());
		}
		
		for (Player online : Methods.getOnlinePlayers(null)) {
			if (StringUtil.startsWithIgnoreCase(online.getName(), args[1]))
				completions.add(online.getName());
		}
		
		return completions;
	}

}
