package net.onima.onimafaction.commands.lives.arguments;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.onima.onimaapi.caching.UUIDCache;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.commands.BasicCommandArgument;
import net.onima.onimafaction.players.FPlayer;
import net.onima.onimafaction.players.OfflineFPlayer;

public class LivesSendArgument extends BasicCommandArgument {

	public LivesSendArgument() {
		super("send", OnimaPerm.ONIMAFACTION_LIVES_SEND_ARGUMENT);
		
		usage = new JSONMessage("§7/lives " + name + " <player> <amount>", "§d§oPermet d'envoyer des vies.");
		playerOnly = true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 3, true))
			return false;
		
		FPlayer fPlayer = FPlayer.getPlayer((Player) sender);
		UUID uuid = UUIDCache.getUUID(args[1]);
		
		if (uuid == null) {
			sender.sendMessage("§c" + args[1] + " ne s'est jamais connecté sur le serveur !");
			return false;	
		}
		
		OfflineFPlayer.getPlayer(uuid, offlineFPlayer -> {
			Integer amount = Methods.toInteger(args[2]);
			
			if (amount == null) {
				sender.sendMessage("§c" + args[2] + " n'est pas un entier !");
				return;
			}
			
			amount = Math.abs(amount);
			
			if (amount == 0) {
				sender.sendMessage("§cVous ne pouvez pas envoyer 0 vies !");
				return;
			}
			
			if (fPlayer.getLives() - amount < 0) {
				sender.sendMessage("§cVous n'avez pas assez de vie à envoyer. §7Vous en avez " + fPlayer.getLives() + ".");
				return;
			} else {
				String life = " §7vie" + (amount > 1 ? "s" : "");
				OfflineAPIPlayer offline = offlineFPlayer.getOfflineApiPlayer();
				
				sender.sendMessage("§dVous §7avez envoyé §d" + amount + life + " à §d" + Methods.getNameFromArg(offline, args[1]) + "§7.");
				
				if (offlineFPlayer.getOfflineApiPlayer().isOnline())
					((APIPlayer) offlineFPlayer.getOfflineApiPlayer()).sendMessage("§d" + fPlayer.getApiPlayer().getName() + " §7vous a envoyé §d" + amount + life + "§7.");
				
				fPlayer.setLives(fPlayer.getLives() - amount);
				offlineFPlayer.setLives(offlineFPlayer.getLives() + amount);
			}
		});
		
		return true;
	}

}
