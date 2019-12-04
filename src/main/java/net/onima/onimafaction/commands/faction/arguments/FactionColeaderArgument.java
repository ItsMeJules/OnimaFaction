package net.onima.onimafaction.commands.faction.arguments;

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
import net.onima.onimafaction.players.OfflineFPlayer;

public class FactionColeaderArgument extends FactionArgument {
	
	public FactionColeaderArgument() {
		super("coleader", OnimaPerm.ONIMAFACTION_COLEADER_ARGUMENT);
		
		usage = new JSONMessage("§7/f " + name + " <player>", "§d§oDéfini le co-leader de votre faction.");

		playerOnly = true;
		role = Role.LEADER;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 2, true))
			return false;
		
		Player player = (Player) sender;
		FPlayer fPlayer = FPlayer.getPlayer(player);
		PlayerFaction faction = fPlayer.getFaction();
		OfflinePlayer offline = faction.getMember(args[1]);
		
		if (offline == null) {
			player.sendMessage("§c" + args[1] + " n'est pas dans votre faction !");
			return false;
		}
		
		if (player.getUniqueId().equals(offline.getUniqueId())) {
			player.sendMessage("§cVous ne pouvez pas vous définir co-leader !");
			return false;
		}
		
		OfflineFPlayer.getPlayer(offline, offlineFPlayer -> {
			if (offlineFPlayer.getRole() == Role.COLEADER) {
				player.sendMessage("§c" + offlineFPlayer.getOfflineApiPlayer().getName() + " est déjà co-leader de la faction !");
				return;
			}
			
			offlineFPlayer.setRole(Role.COLEADER);
			faction.broadcast("§d§o" + fPlayer.getRole().getRole() + fPlayer.getApiPlayer().getName() + " §7a promu §d§o" + offlineFPlayer.getOfflineApiPlayer().getName() + " §7au rôle de co-leader.");
		});
		
		return true;
	}

}
