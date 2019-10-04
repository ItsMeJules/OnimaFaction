package net.onima.onimafaction.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerSetupListener implements Listener {
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onLogin(PlayerLoginEvent event) {
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event) {
	}
	
	@EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
	}

}
