package net.onima.onimafaction.commands.faction.arguments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import net.md_5.bungee.api.chat.ClickEvent;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.players.FPlayer;

public class FactionShowArgument extends FactionArgument {

	public FactionShowArgument() {
		super("show", OnimaPerm.ONIMAFACTION_SHOW_ARGUMENT, new String[]{"f", "i", "info", "who"});
		usage = new JSONMessage("§7/f " + name + " (faction | player)", "§d§oAffiche les détails d'une faction.");
		
		needFaction = false;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 1, true))
			return false;
		
		Faction faction = null;
		
		if (args.length < 2) {
			if (sender instanceof Player) { 
				if ((faction = FPlayer.getPlayer((Player) sender).getFaction()) == null) {
					((Player) sender).spigot().sendMessage(new JSONMessage("§cVous avez besoin d'une faction pour pouvoir afficher ses détails !", "§a/f create ", true, "/f create ", ClickEvent.Action.SUGGEST_COMMAND).build());
					return false;
				}
			} else {
				sender.sendMessage("§cLa console n'a pas de faction ! Il faut spécifier une faction !");
				return false;
			}
			
		} else if ((faction = Faction.getFaction(args[1])) == null) {
			sender.sendMessage("§cImpossible de trouver la faction ou le joueur " + args[1]);
			return false;
		}

		faction.sendShow(sender);
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) || args.length != 2)
			return Collections.emptyList();
		
		List<String> completions = Faction.getFactions().parallelStream().map(Faction::getName).filter(name -> StringUtil.startsWithIgnoreCase(name, args[1])).collect(Collectors.toCollection(() -> new ArrayList<String>(Faction.getFactions().size())));
		Player player = (Player) sender;
		
		for (APIPlayer apiPlayer : APIPlayer.getOnlineAPIPlayers()) {
			if (!player.canSee(apiPlayer.toPlayer()) && (apiPlayer.isVanished() || !FPlayer.getPlayer(apiPlayer.getUUID()).getRegionOn().getDisplayName(null).equalsIgnoreCase(ConfigurationService.SAFEZONE_NAME)))
				continue;
			
			if (StringUtil.startsWithIgnoreCase(apiPlayer.getName(), args[1]))
				completions.add(apiPlayer.getName());
		}
		
		return completions;
	}

}
