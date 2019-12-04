package net.onima.onimafaction.commands.faction.arguments.staff;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.players.OfflineFPlayer;

public class FactionBypassArgument extends FactionArgument {
	
	public FactionBypassArgument() {
		super("bypass", OnimaPerm.ONIMAFACTION_BYPASS_ARGUMENT);
		
		usage = new JSONMessage("§c/f " + name, "§d§oPermet d'intéragir avec l'environnement dans un claim.");
		
		needFaction = false;
		playerOnly = true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 1, true))
			return false;
		
		OfflinePlayer offline = args.length > 1 ? Bukkit.getOfflinePlayer(args[1]) : (OfflinePlayer) sender;
		
		if (!offline.hasPlayedBefore()) {
			sender.sendMessage("§c" + args[1] + " ne s'est jamais connecté sur le serveur !");
			return false;
		}
		
		OfflineFPlayer.getPlayer(offline, fPlayer -> {
			fPlayer.setFactionBypass(!fPlayer.hasFactionBypass());
			
			if (offline.isOnline())
				((Player) offline).sendMessage("§dFaction bypass " + (fPlayer.hasFactionBypass() ? "§aON" : "§cOFF") + "§d.");
		});
		
		return true;
	}

}
