package net.onima.onimafaction.listener;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionEffectExpireEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.armorclass.ArmorClass;
import net.onima.onimafaction.players.FPlayer;

public class ArmorClassListener implements Listener {
	
	@EventHandler
	public void onExpireEffect(PotionEffectExpireEvent event) {
		LivingEntity entity = event.getEntity();
		
		if (entity instanceof Player) {
			ArmorClass equipped = FPlayer.getByUuid(entity.getUniqueId()).getEquippedClass();
			
			if (equipped == null) return;
			
			for (PotionEffect effect : equipped.getEffects()) {
				if (effect.getType().equals(event.getPotionEffect().getType())) {
					event.setCancelled(true);
					entity.addPotionEffect(effect, true);
				}
			}
		}
	}
	
	@EventHandler
	public void onMilkDrink(PlayerItemConsumeEvent event) { //Simulating the milk drink
		Player player = event.getPlayer();
		ArmorClass armorClass = FPlayer.getByPlayer(player).getEquippedClass();

		if (armorClass == null) return;
		
		if (event.getItem().getType() == Material.MILK_BUCKET) {
			if (player.getGameMode() != GameMode.CREATIVE)
				player.setItemInHand(new ItemStack(Material.BUCKET, player.getItemInHand().getAmount()));
			Methods.clearEffects(player);
			player.addPotionEffects(armorClass.getEffects());
			event.setCancelled(true);
		}
	}
	 
}
