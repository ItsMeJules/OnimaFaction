package net.onima.onimafaction.events;

import java.util.Collection;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.onima.onimaapi.event.chat.ChatEvent;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.Chat;

public class FactionChatEvent extends ChatEvent {

	private PlayerFaction faction;
	private Chat chat;
	
	public FactionChatEvent(Player player, PlayerFaction faction, Collection<CommandSender> readers, Chat chat, String message) {
		super(player, readers, message, APIPlayer.getPlayer(player).getRank().getRankType());
		this.faction = faction;
		this.chat = chat;
	}

	public PlayerFaction getFaction() {
		return faction;
	}
	
	public Chat getChat() {
		return chat;
	}

}
