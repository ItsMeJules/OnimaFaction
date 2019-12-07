package net.onima.onimafaction.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.projectiles.ProjectileSource;

import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.zone.struct.Flag;
import net.onima.onimaapi.zone.type.Region;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.players.FPlayer;

public class ProtectionListener implements Listener {
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		
		if (Claim.getClaimAndRegionAt(event.getEntity().getLocation()).hasFlag(Flag.COMBAT_SAFE)) {
			
			if (event instanceof EntityDamageByEntityEvent) {
				Player attacker = Methods.getLastAttacker((EntityDamageByEntityEvent) event);
				
				if (attacker != null && !attacker.equals(entity) && FPlayer.getPlayer(attacker).hasFactionBypass())
					return;
			}
			
			switch (event.getCause()) {
			case SUICIDE:
			case VOID:
				break;
			default:
				event.setCancelled(true);
				break;
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true)
	public void onPotionSplash(PotionSplashEvent event) {
		ThrownPotion potion = event.getEntity();
		
		if (!Methods.isDebuff(potion)) return;
		
		Region region = Claim.getClaimAndRegionAt(potion.getLocation());
		
		if (region.hasFlag(Flag.COMBAT_SAFE)) {
			event.setCancelled(true);
			return;
		}
		
		ProjectileSource shooter = potion.getShooter();
		
		if (shooter instanceof Player) {
			Player player = (Player) shooter;
			PlayerFaction faction = FPlayer.getPlayer(player).getFaction();
			
			for (LivingEntity affected : event.getAffectedEntities()) {
				if (!player.equals(affected) && affected instanceof Player) {
					if (Claim.getClaimAndRegionAt(affected.getLocation()).hasFlag(Flag.COMBAT_SAFE) || (faction != null && faction.getMembers().containsKey(affected.getUniqueId())))
						event.setCancelled(true);
				}
			}
		}
	}
	
}
