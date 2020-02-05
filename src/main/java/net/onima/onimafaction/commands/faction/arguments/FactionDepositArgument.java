package net.onima.onimafaction.commands.faction.arguments;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.Balance;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.players.FPlayer;

public class FactionDepositArgument extends FactionArgument {

	public FactionDepositArgument() {
		super("deposit", OnimaPerm.ONIMAFACTION_DEPOSIT_ARGUMENT, new String[] {"d"});
		usage = new JSONMessage("§7/f " + name + " <amount | all>", "§d§oPermet de déposer de l'argent dans la banque de la faction.");
		
		playerOnly = true;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 2, true))
			return false;
		
		Player player = (Player) sender;
		FPlayer fPlayer = FPlayer.getPlayer(player);
		PlayerFaction faction = null;
		
		if ((faction = fPlayer.getFaction()) == null) {
			player.spigot().sendMessage(new JSONMessage("§cVous avez besoin d'une faction pour déposer de l'argent !", "§a/f create ", true, "/f create ", ClickEvent.Action.SUGGEST_COMMAND).build());
			return false;
		}
		
		Balance balance = fPlayer.getApiPlayer().getBalance();
		double money = balance.getAmount();
		Double amount = 0D;
		
		if (args[1].equalsIgnoreCase("all"))
			amount = money;
		else {
			amount = Methods.toDouble(args[1]);
			
			if (amount == null) {
				player.sendMessage("§c" + args[1] + " n'est pas un nombre !");
				return true;
			}
			
			if (amount > money)
				amount = money;
		}
		
		if (amount > 0) {
			balance.removeAmount(amount);
			faction.addMoney(amount);
			faction.broadcast("§d§o" + fPlayer.getRole().getRole() + fPlayer.getApiPlayer().getName() + " §7a déposé §d§o" + amount + ConfigurationService.MONEY_SYMBOL + " §7dans la banque de la faction.");
		} else {
			player.sendMessage("§cQu'est-ce tu veux faire ? Niquer la matrice ? Non dsl frérot tu peux pas déposer un montant négatif ou égal à 0.");
			return false;
		}
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		return args.length == 2 ? Arrays.asList("all") : Collections.emptyList();
	}

}
