package net.onima.onimafaction.listener;

import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionEffectExpireEvent;
import org.bukkit.potion.PotionEffect;

import net.onima.onimafaction.armorclass.Bard;

public class BardListener implements Listener {
	
	@EventHandler
	public void onPotionEffectExpire(PotionEffectExpireEvent event) {
		LivingEntity entity = event.getEntity();
		
		if (entity instanceof Player) {
			Player player = (Player) entity;
			List<String> bardingEffects = Bard.getEffectsNotToSave(player);
			List<PotionEffect> effects = Bard.getEffectsTookFromBarding(player);
			
			if (effects == null) return;
			
			Iterator<PotionEffect> iterator = effects.iterator();
			
			while (iterator.hasNext()) {
				PotionEffect effect = iterator.next();
				if (event.getPotionEffect().getType().equals(effect.getType())) {
					event.setCancelled(true);
					entity.addPotionEffect(effect, true);
					bardingEffects.remove(effect.getType().getName());
					iterator.remove();
				}
			}
		}
	}

}
