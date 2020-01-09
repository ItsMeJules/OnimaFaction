package net.onima.onimafaction.commands.faction.arguments.staff;

import java.util.UUID;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.onima.onimaapi.caching.UUIDCache;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.players.OfflineFPlayer;

public class FactionBypassArgument extends FactionArgument {
	
	public FactionBypassArgument() {
		super("bypass", OnimaPerm.ONIMAFACTION_BYPASS_ARGUMENT);
		
		usage = new JSONMessage("§c/f " + name + " (player)", "§d§oPermet d'intéragir avec l'environnement dans un claim.");
		
		needFaction = false;
		playerOnly = true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 1, true))
			return false;
		
		Player player = (Player) sender;
		UUID uuid = args.length > 1 ? UUIDCache.getUUID(args[1]) : player.getUniqueId();
		
		if (uuid == null) {
			sender.sendMessage("§c" + args[1] + " ne s'est jamais connecté sur le serveur !");
			return false;
		}
		
		OfflineFPlayer.getPlayer(uuid, fPlayer -> {
			fPlayer.setFactionBypass(!fPlayer.hasFactionBypass());
			
			if (fPlayer.getOfflineApiPlayer().isOnline())
				((APIPlayer) fPlayer.getOfflineApiPlayer()).sendMessage("§dFaction bypass " + (fPlayer.hasFactionBypass() ? "§aON" : "§cOFF") + "§d.");
		
			if (!uuid.equals(player.getUniqueId()))
				player.sendMessage("§dFaction bypass pour §l" + Methods.getRealName(fPlayer.getOfflineApiPlayer().getOfflinePlayer()) + (fPlayer.hasFactionBypass() ? "§aON" : "§cOFF") + "§d.");

		});
		
		return true;
	}

}
