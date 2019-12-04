package net.onima.onimafaction.commands.faction.arguments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import net.md_5.bungee.api.chat.ClickEvent;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.struct.Chat;
import net.onima.onimafaction.players.FPlayer;

public class FactionChatArgument extends FactionArgument {

	public FactionChatArgument() {
		super("chat", OnimaPerm.ONIMAFACTION_CHAT_ARGUMENT, new String[] {"c"});
		
		usage = new JSONMessage("§7/f " + name + " (public | faction | ally)", "§d§oActive le chat public/ally/faction.");
		
		playerOnly = true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 1, true))
			return false;
		
		Player player = (Player) sender;
		FPlayer fPlayer = FPlayer.getPlayer(player);
		
		if (!fPlayer.hasFaction()) {
			player.spigot().sendMessage(new JSONMessage("§cVous avez besoin d'une faction pour changer de chat !", "§a/f create ", true, "/f create ", ClickEvent.Action.SUGGEST_COMMAND).build());
			return false;
		}
		
		Chat nextChat = args.length >= 2 ? Chat.fromString(args[1]) : fPlayer.getChat().nextChat();
		
		fPlayer.setChat(nextChat);
		player.sendMessage("§d§oVous §7parlez maintenant dans le chat §d§o" + nextChat.getChat().toLowerCase() + "§7.");
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player) || args.length != 2)
			return Collections.emptyList();
		
		FPlayer fPlayer = FPlayer.getPlayer((Player) sender);
		List<String> chats = new ArrayList<>();
		
		for (Chat chat : Chat.values()) {
			if (chat == Chat.STAFF) continue;
			if (chat == fPlayer.getChat()) continue;
			
			if (!chats.contains(chat.getChat().toLowerCase()) && StringUtil.startsWithIgnoreCase(chat.getChat(), args[1]))
				chats.add(chat.getChat().toLowerCase());
		}
		
		return chats;
	}

}
