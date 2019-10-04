package net.onima.onimafaction.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import net.onima.onimaapi.cooldown.utils.Cooldown;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.time.Time.LongTime;
import net.onima.onimafaction.cooldowns.PvPTimerCooldown;

public class PvPCommand implements CommandExecutor, TabCompleter {
	
	private JSONMessage timeUsage = new JSONMessage("§7/pvp time (player)", "§d§oAffiche le pvp timer d'un joueur.");
	private JSONMessage enableUsage = new JSONMessage("§7/pvp enable", "§d§oDésactive votre pvp timer.");
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!OnimaPerm.ONIMAAPI_PVP_COMMAND.has(sender)) {
			sender.sendMessage(OnimaPerm.ONIMAAPI_PVP_COMMAND.getMissingMessage());
			return false;
		}
		
		if (args.length < 1) {
			help(sender);
			return false;
		}
		
		if (args[0].equalsIgnoreCase("enable")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("§cSeulement les joueurs peuvent utiliser cet argument !");
				return false;
			}
			
			APIPlayer apiPlayer = APIPlayer.getByPlayer((Player) sender);
			Cooldown cooldown = Cooldown.getCooldown(PvPTimerCooldown.class);
			
			if (cooldown.getTimeLeft(apiPlayer.getUUID()) > 0L) {
				sender.sendMessage("§dVous §7avez désactivé votre §cpvp timer§7.");
				apiPlayer.removeCooldown(cooldown);
				return true;
			} else {
				sender.sendMessage("§cVotre pvp timer est déjà désactivé.");
				return false;
			}
			
		} else if (args[0].equalsIgnoreCase("time")) {
			Cooldown cooldown = Cooldown.getCooldown(PvPTimerCooldown.class);
			String msg;
			
			if (args.length > 1) {
				APIPlayer apiPlayer = APIPlayer.getByName(args[1]);
				
				if (apiPlayer == null) {
					sender.sendMessage("§c" + args[0] + " n'est pas connecté !");
					return false;
				}
			
				long timeLeft;
				
				if ((timeLeft = cooldown.getTimeLeft(apiPlayer.getUUID())) > 0L)
					msg = "§d" + apiPlayer.getName() + " §7a son §cpvp timer §7pour encore §d" + LongTime.setYMDWHMSFormat(timeLeft) + "§7.";
				else
					msg = "§d" + apiPlayer.getName() + " §7n'a pas son §cpvp timer §7d'activé.";
					
			} else if (sender instanceof Player) {
				Player player = (Player) sender;
				long timeLeft;
				
				if ((timeLeft = cooldown.getTimeLeft(player.getUniqueId())) > 0L)
					msg = "§dVous §7avez votre §cpvp timer §7pour encore §d" + LongTime.setYMDWHMSFormat(timeLeft) + "§7.";
				else
					msg = "§dVous §7n'avez pas votre §cpvp timer §7d'activé.";
					
			} else {
				sender.sendMessage("§cSeulement les joueurs peuvent utiliser cet argument !");
				return false;
			}
			
			sender.sendMessage(msg);
			return true;
		}
		
		help(sender);
		return false;
	}

	private void help(CommandSender sender) {
		sender.sendMessage("§e" + ConfigurationService.STAIGHT_LINE);
		timeUsage.send(sender);
		enableUsage.send(sender);
		sender.sendMessage("§e" + ConfigurationService.STAIGHT_LINE);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> completions = new ArrayList<>();
		
		if (args.length == 1) {
			if (StringUtil.startsWithIgnoreCase("enable", args[0]))
				completions.add("enable");
			else if (StringUtil.startsWithIgnoreCase("time", args[0]))
				completions.add("time");
		} else if (args.length == 2) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if(StringUtils.startsWithIgnoreCase(player.getName(), args[1]))
					completions.add(player.getName());
			}
		}
		
		return completions;
	}
	
}
