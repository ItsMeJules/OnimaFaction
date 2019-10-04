package net.onima.onimafaction.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.armorclass.Reaper;
import net.onima.onimafaction.armorclass.Reaper.ReaperStage;
import net.onima.onimafaction.players.FPlayer;

public class ReaperListener implements Listener {
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Reaper reaper = (Reaper) FPlayer.getByPlayer(player).getArmorClass(Reaper.class);
			
			if (reaper.isActivated() && reaper.getReaperStage() == ReaperStage.STEALTH_MODE) {
				for (Entity entity : player.getNearbyEntities(Reaper.STEALTH_DAMAGE_ALERT_RADIUS, Reaper.STEALTH_DAMAGE_ALERT_RADIUS, Reaper.STEALTH_DAMAGE_ALERT_RADIUS)) {
					if (entity instanceof Player)
						((Player) entity).sendMessage("§7Un §ereaper §7a subit des dégâts en mode §5furtif §7dans un rayon de §e§l" + Reaper.STEALTH_DAMAGE_ALERT_RADIUS + " §7blocks.");
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		
		if (entity instanceof Player) {
			Player attacker = Methods.getLastAttacker(event);
			
			if (attacker == null || attacker.equals(entity)) return;
			
			Reaper reaper = (Reaper) FPlayer.getByPlayer(attacker).getArmorClass(Reaper.class);
			
			if (reaper.isActivated() && reaper.getReaperStage() == ReaperStage.STEALTH_MODE)
				reaper.start(ReaperStage.POWER_MODE, true);
		}
	}
	
}
