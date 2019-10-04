package net.onima.onimafaction.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.onima.onimaapi.cooldown.utils.Cooldown;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimafaction.cooldowns.LogoutCooldown;

public class LogoutCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!OnimaPerm.LOGOUT_COMMAND.has(sender)) {
			sender.sendMessage(OnimaPerm.LOGOUT_COMMAND.getMissingMessage());
			return false;
		}
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cSeulement les joueurs peuvent utiliser cette commande !");
			return false;
		}
		
		APIPlayer apiPlayer = APIPlayer.getByPlayer((Player) sender);
		LogoutCooldown cooldown = (LogoutCooldown) Cooldown.getCooldown(LogoutCooldown.class);
		
		if (cooldown.getTimeLeft(apiPlayer.getUUID()) > 0L) {
			sender.sendMessage("§cVous êtes déjà entrain de vous déconnecter !");
			return false;
		} else
			apiPlayer.startCooldown(cooldown);
		
		return true;
	}

}
