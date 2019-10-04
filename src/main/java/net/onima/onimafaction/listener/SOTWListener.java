package net.onima.onimafaction.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.timed.event.SOTW;

public class SOTWListener implements Listener {
	
	private SOTW sotw;
	
	{
		sotw = OnimaFaction.getInstance().getSOTW();
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player && (event.getCause() != DamageCause.VOID || event.getCause() != DamageCause.SUICIDE) && sotw.isRunning())
			event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player && sotw.isRunning())
			event.setFoodLevel(20);
	}
	
}
