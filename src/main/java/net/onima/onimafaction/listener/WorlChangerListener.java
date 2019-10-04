package net.onima.onimafaction.listener;

import org.bukkit.World;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerPortalEvent;

import net.onima.onimaapi.utils.WorldChanger;

public class WorlChangerListener implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onEntityPortal(EntityPortalEvent event) {
		if (event.getEntity() instanceof EnderDragon)
			event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerPortal(PlayerPortalEvent event) {
		World toWorld = event.getTo().getWorld();
		
		if (toWorld == null)
			return;
		
		WorldChanger changer = WorldChanger.getChanger(event.getFrom().getWorld().getName(), toWorld.getName());
		
		if (changer == null || changer.shouldUseTravelAgent() || changer.getSpawnLocation() == null)
			return;
		
		event.useTravelAgent(false);
		event.setTo(changer.getSpawnLocation());
	}
	
}
