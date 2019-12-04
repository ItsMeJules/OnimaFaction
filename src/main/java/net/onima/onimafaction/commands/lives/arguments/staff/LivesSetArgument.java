package net.onima.onimafaction.commands.lives.arguments.staff;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.onima.onimaapi.caching.UUIDCache;
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
		
		UUID uuid = UUIDCache.getUUID(args[1]);
		
		if (uuid == null) {
			sender.sendMessage("§c" + args[1] + " ne s'est jamais connecté sur le serveur !");
			return false;
		}
		
		OfflineFPlayer.getPlayer(uuid, offlineFPlayer -> {
			if (offlineFPlayer == null) {
				sender.sendMessage("§c" + args[1] + " ne s'est jamais connecté sur le serveur !");
				return;
			}

			Integer amount = Methods.toInteger(args[2]);
			
			if (amount == null) {
				sender.sendMessage("§c" + args[2] + " n'est pas un entier !");
				return;
			}
			
			amount = Math.abs(amount);
			String life = " §7vie" + (amount > 1 ? "s" : "");
				
			sender.sendMessage("§dVous §7avez défini §d" + amount + life + " à §d" + Methods.getName(offlineFPlayer.getOfflineApiPlayer(), true) + "§7.");
				
			if (offlineFPlayer.getOfflineApiPlayer().isOnline())
				((APIPlayer) offlineFPlayer.getOfflineApiPlayer()).sendMessage("§d" + Methods.getRealName(sender) + " §7vous a défini §d" + amount + life + "§7.");
			
			offlineFPlayer.setLives(amount);
		});
		
		return true;
	}

}
