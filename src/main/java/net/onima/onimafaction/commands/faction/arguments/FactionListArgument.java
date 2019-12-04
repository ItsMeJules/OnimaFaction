package net.onima.onimafaction.commands.faction.arguments;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.PlayerFaction;

public class FactionListArgument extends FactionArgument {
	
	public static final int MAX_FACTION_PER_PAGE = 10;

	public FactionListArgument() {
		super("list", OnimaPerm.ONIMAFACTION_LIST_ARGUMENT);
		usage = new JSONMessage("§7/f " + name + " (page)", "§d§oAffiche la liste des factions.");
	
		needFaction = false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Integer page = 0;
		
		if (args.length < 2)
			page = 1;
		else {
			page = Methods.toInteger(args[1]);
			
			if (page == null) {
				sender.sendMessage("§c" + args[1] + " n'est pas un nombre !");
				return true;
			}
		}

		for (BaseComponent[] component : factionList(sender instanceof Player ? (Player) sender : null, page))
			Methods.sendJSON(sender, component);
		
		return true;
	}

	public List<BaseComponent[]> factionList(Player viewer, Integer page) {
		if (page < 1) {
			List<BaseComponent[]> errorList = new ArrayList<>(1);

			errorList.add(new ComponentBuilder("§cLa page " + page + " n'existe pas !").create());
			
			return errorList;
		}
		
		int maxPage = (int) Math.ceil(PlayerFaction.getPlayersFaction().size() / MAX_FACTION_PER_PAGE);
		
		if (page > maxPage)
			page = maxPage;
		
		double toSkip = page * MAX_FACTION_PER_PAGE;
		int index = 0;
		
		List<BaseComponent[]> list = new ArrayList<>();
		
		list.add(new ComponentBuilder("§6" + ConfigurationService.STAIGHT_LINE).create());
		for (PlayerFaction faction : PlayerFaction.getByMostPlayersOnline().keySet()) {
			if (toSkip > 0 && toSkip != MAX_FACTION_PER_PAGE) {
				toSkip--;
				continue;
			}
			
			if (index <= MAX_FACTION_PER_PAGE) {
				list.add(faction.jsonHoverTooltip(viewer, index).build());
				index++;
			}
		}
		list.add(new ComponentBuilder("§6" + ConfigurationService.STAIGHT_LINE).create());
		
		return list;
	}

}
