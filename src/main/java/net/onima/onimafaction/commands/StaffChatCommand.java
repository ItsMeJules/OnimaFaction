package net.onima.onimafaction.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimafaction.faction.struct.Chat;
import net.onima.onimafaction.players.FPlayer;

public class StaffChatCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!OnimaPerm.ONIMAFACTION_STAFFCHAT_COMMAND.has(sender)) {
			sender.sendMessage(OnimaPerm.ONIMAFACTION_STAFFCHAT_COMMAND.getMissingMessage());
			return false;
		}
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cSeulement les joueurs peuvent se mettre en staff chat !");
			return false;
		}
		
		FPlayer fPlayer = FPlayer.getByPlayer((Player) sender);
		
		if (fPlayer.getChat() != Chat.STAFF) {
			fPlayer.setChat(Chat.STAFF);
			sender.sendMessage("§eVous §7êtes §amaintenant en §9staff §7chat.");
		} else {
			fPlayer.setChat(Chat.GLOBAL);
			sender.sendMessage("§eVous §7n'êtes §cplus en §9staff §7chat.");
		}
		
		return true;
	}

}
