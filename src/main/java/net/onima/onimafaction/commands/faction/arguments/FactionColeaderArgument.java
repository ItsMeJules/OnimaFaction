package net.onima.onimafaction.commands.faction.arguments;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.Role;
import net.onima.onimafaction.players.FPlayer;

public class FactionColeaderArgument extends FactionArgument {
	
	public FactionColeaderArgument() {
		super("coleader", OnimaPerm.ONIMAFACTION_COLEADER_ARGUMENT);
		
		usage = new JSONMessage("§7/f " + name + " <player>", "§d§oDéfini le co-leader de votre faction.");

		playerOnly = true;
		role = Role.LEADER;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 2, true))
			return false;
		
		Player player = (Player) sender;
		FPlayer fPlayer = FPlayer.getByPlayer(player);
		PlayerFaction faction = fPlayer.getFaction();
		OfflinePlayer offline = Bukkit.getOfflinePlayer(args[1]);
		
		if (!offline.hasPlayedBefore()) {
			player.sendMessage("§c" + args[2] + " ne s'est jamais connecté sur le serveur !");
			return false;
		}
		
		if (player.getUniqueId().equals(offline.getUniqueId())) {
			player.sendMessage("§cVous ne pouvez pas vous définir co-leader !");
			return false;
		}
		
		if (fPlayer.getRole() == Role.COLEADER) {
			player.sendMessage("§c" + offline.getName() + " est déjà co-leader de la faction !");
			return false;
		}
		
		fPlayer.setRole(Role.COLEADER);
		faction.broadcast("§d§o" + fPlayer.getRole().getRole() + player.getName() + " §7a promu §d§o" + offline.getName() + " §7au rôle de co-leader.");
		return false;
	}

}
