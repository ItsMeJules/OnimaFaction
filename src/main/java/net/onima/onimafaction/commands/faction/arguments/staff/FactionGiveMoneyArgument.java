package net.onima.onimafaction.commands.faction.arguments.staff;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;

public class FactionGiveMoneyArgument extends FactionArgument {
	
	public FactionGiveMoneyArgument() {
		super("givemoney", OnimaPerm.ONIMAFACTION_GIVEMONEY_ARGUMENT);
		
		usage = new JSONMessage("§c/f " + name + " <faction> <amount>", "§d§oPermet de donner de l'argent à une faction.");
		
		needFaction = false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 3, true))
			return false;
		
		Faction maybeFaction = null;
		
		if ((maybeFaction = Faction.getFaction(args[1])) == null) {
			sender.sendMessage("§cImpossible de trouver la faction ou le joueur " + args[1]);
			return false;
		}
		
		if (!(maybeFaction instanceof PlayerFaction)) {
			sender.sendMessage("§cVous ne pouvez donner de l'argent seulement aux factions de joueurs !");
			return false;
		}
		
		PlayerFaction faction = (PlayerFaction) maybeFaction;
		Double amount = Methods.toDouble(args[2]);
		
		if (amount == null) {
			sender.sendMessage("§c" + args[2] + " n'est pas un nombre !");
			return false;
		}
		
		faction.addMoney(amount);
		sender.sendMessage("§d§oVous §7avez donné §d§o" + amount + ConfigurationService.MONEY_SYMBOL + " §7à la faction §d§o" + faction.getName() + "§7.");
		faction.broadcast("§d§o" + Methods.getRealName(sender) + " §7a donné §d§o" + amount + ConfigurationService.MONEY_SYMBOL + " §7à la faction.");
		
		return true;
	}

}
