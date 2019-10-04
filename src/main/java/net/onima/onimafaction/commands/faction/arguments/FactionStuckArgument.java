package net.onima.onimafaction.commands.faction.arguments;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.cooldowns.FactionStuckCooldown;
import net.onima.onimafaction.faction.StuckRequest;
import net.onima.onimafaction.players.FPlayer;

public class FactionStuckArgument extends FactionArgument {
	
	public FactionStuckArgument() {
		super("stuck", OnimaPerm.ONIMAFACTION_STUCK_ARGUMENT);
		
		usage = new JSONMessage("§7/f " + name, "§d§oVous téléporte à une location sauve si vous êtes bloqué.");
		
		needFaction = false;
		playerOnly = true;
	}

	@SuppressWarnings("unused")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 1, true))
			return false;
		
		Player player = (Player) sender;
		FPlayer fPlayer = FPlayer.getByPlayer(player);

		if (fPlayer.getStuckRequest() == null) {
			fPlayer.setStuckRequest(new StuckRequest(player.getLocation()));
			fPlayer.getApiPlayer().startCooldown(FactionStuckCooldown.class);
			player.sendMessage("§d§oVotre §7demande de stuck a commencé. Ne sortez pas du radius de §d§o" + ConfigurationService.STUCK_RADIUS + " §7block" + (ConfigurationService.STUCK_RADIUS > 1 ? "s" : "") + '.');
			return true;
		}
		
		player.sendMessage("§cVous êtes déjà entrain de f stuck !");
		return false;
	}

}
