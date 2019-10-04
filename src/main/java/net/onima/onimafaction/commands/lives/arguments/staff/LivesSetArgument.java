package net.onima.onimafaction.commands.lives.arguments.staff;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.commands.BasicCommandArgument;
import net.onima.onimafaction.players.OfflineFPlayer;

public class LivesSetArgument extends BasicCommandArgument {

	public LivesSetArgument() {
		super("set", OnimaPerm.ONIMAFACTION_LIVES_SET_ARGUMENT);
		
		usage = new JSONMessage("§7/lives " + name + " <player> <amount>", "§d§oPermet de définir le nombre des vies.");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 3, true))
			return false;
		
		OfflineFPlayer offlineFPlayer = OfflineFPlayer.getByName(args[1]);
		
		if (offlineFPlayer == null) {
			sender.sendMessage("§c" + args[1] + " ne s'est jamais connecté sur le serveur !");
			return false;
		}

		Integer amount = Methods.toInteger(args[2]);
		
		if (amount == null) {
			sender.sendMessage("§c" + args[2] + " n'est pas un entier !");
			return false;
		}
		
		amount = Math.abs(amount);
		String life = " §7vie" + (amount > 1 ? "s" : "");
			
		sender.sendMessage("§dVous §7avez défini §d" + amount + life + " à §d" + offlineFPlayer.getOfflineApiPlayer().getName() + "§7.");
			
		if (offlineFPlayer.getOfflineApiPlayer().isOnline())
			((APIPlayer) offlineFPlayer.getOfflineApiPlayer()).sendMessage("§d" + sender.getName() + " §7vous a défini §d" + amount + life + "§7.");
		
		offlineFPlayer.setLives(amount);
		return true;
	}

}
