package net.onima.onimafaction.commands.faction.arguments.staff;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.onima.onimaapi.caching.UUIDCache;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.Role;
import net.onima.onimafaction.players.OfflineFPlayer;

public class FactionForceColeaderArgument extends FactionArgument {

	public FactionForceColeaderArgument() {
		super("forcecoleader", OnimaPerm.ONIMAFACTION_FORCECOLEADER_ARGUMENT);
		
		usage = new JSONMessage("§c/f " + name + " <player>", "§d§oPermet de définir de force un co-leader dans une faction.");
		
		needFaction = false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 2, true))
			return false;
		
		OfflinePlayer offline = Bukkit.getOfflinePlayer(UUIDCache.getUUID(args[1]));
		
		if (!offline.hasPlayedBefore()) {
			sender.sendMessage("§c" + args[1] + " ne s'est jamais connecté sur le serveur !");
			return false;
		}
		
		OfflineFPlayer.getPlayer(offline, fPlayer -> {
			PlayerFaction faction = null;
			
			if ((faction = fPlayer.getFaction()) == null) {
				sender.sendMessage("§c" + Methods.getName(fPlayer.getOfflineApiPlayer(), true) + " n'a pas de faction !");
				return;
			}
			
			if (fPlayer.getRole() == Role.COLEADER) {
				sender.sendMessage("§c" + Methods.getName(fPlayer.getOfflineApiPlayer(), true) + " est déjà co-leader de sa faction !");
				return;
			}
			
			fPlayer.setRole(Role.COLEADER);
			
			sender.sendMessage("§d§oVous §7avez défini le rôle de §d§o" + Methods.getName(fPlayer.getOfflineApiPlayer(), true) + " §7sur co-leader.");
			faction.broadcast("§d§o" + Methods.getRealName(sender) + " §7a défini §d§o" + fPlayer.getOfflineApiPlayer().getName() + " §7en tant que co-leader de la faction.");
		});
		
		return true;
	}
	
}
