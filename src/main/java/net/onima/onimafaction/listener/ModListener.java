package net.onima.onimafaction.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.players.PlayerOption;
import net.onima.onimafaction.events.FactionChatEvent;

public class ModListener implements Listener {
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(FactionChatEvent event) {
		APIPlayer player = APIPlayer.getByPlayer((Player) event.getSender());
		
		if (player.isInModMode() && player.getOptions().getBoolean(PlayerOption.ModOptions.CHAT_MESSAGE)) {
			event.setCancelled(true);
			player.sendMessage("§cVous avez désactivé l'option de parler dans le chat en mod mode.");
		}
	}

}
