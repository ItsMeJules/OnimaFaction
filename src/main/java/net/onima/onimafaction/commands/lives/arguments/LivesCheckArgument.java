package net.onima.onimafaction.commands.lives.arguments;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.callbacks.VoidCallback;
import net.onima.onimaapi.utils.commands.BasicCommandArgument;
import net.onima.onimafaction.players.OfflineFPlayer;

public class LivesCheckArgument extends BasicCommandArgument {

	public LivesCheckArgument() {
		super("check", OnimaPerm.ONIMAFACTION_LIVES_CHECK_ARGUMENT, new String[] {"info"});
		
		usage = new JSONMessage("§7/lives " + name + " <player>", "§d§oPermet de voir le nombre de vies.");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 1, true))
			return false;

		VoidCallback<OfflineFPlayer> callback = offline -> {
			if (offline == null) {
				sender.sendMessage("§c" + args[1] + " ne s'est jamais connecté sur le serveur !");
				return;
			}
			
			String msg = offline.getOfflineApiPlayer().getName().equalsIgnoreCase(Methods.getRealName(sender)) ? "§dVous §7avez" : "§d" + Methods.getNameFromArg(offline.getOfflineApiPlayer(), args[1]) + " §7a";
			
			sender.sendMessage(msg + " §c" + offline.getLives() + " §7vie" + (offline.getLives() > 1 ? "s" : "") + '.');
		};
		
		if (args.length <= 1) {
			if (sender instanceof Player)
				OfflineFPlayer.getPlayer((Player) sender, callback);
			else {
				sender.sendMessage("§cLa console n'a pas besoin de vie !");
				return false;
			}
		} else
			OfflineFPlayer.getPlayer(args[1], callback);

		return true;
	}

}
