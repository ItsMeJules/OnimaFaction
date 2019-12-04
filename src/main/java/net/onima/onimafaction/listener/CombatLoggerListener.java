package net.onima.onimafaction.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.CombatLogger;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.zone.struct.Flag;
import net.onima.onimaapi.zone.type.Region;
import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.cooldowns.PvPTimerCooldown;
import net.onima.onimafaction.players.FPlayer;

public class CombatLoggerListener implements Listener {
	
	@EventHandler
	public void onLoggerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		CombatLogger logger = CombatLogger.getCombatLogger(player.getUniqueId());
		
		if (logger != null) {
			LivingEntity entity = logger.getEntity();
	
			double hp = Math.min(((Damageable) player).getHealth(), ((Damageable) entity).getHealth());
			
			if (hp <= 0) {
				Methods.clearInventory(player);
				player.setExp(0);
			}
			
			player.teleport(entity);
			player.setHealth(hp);
			player.setFallDistance(entity.getFallDistance());
			player.setRemainingAir(entity.getRemainingAir());
			logger.kill();
		}
	}
	
	@EventHandler
	public void onLoggerBurn(EntityCombustEvent event) {
		Entity entity = event.getEntity();
		
		if (entity instanceof LivingEntity) {
			if (CombatLogger.getCombatLogger(((LivingEntity) entity).getCustomName()) != null)
				event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onLoggerDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		
		if (entity instanceof LivingEntity) {
			LivingEntity living = (LivingEntity) entity;
			CombatLogger logger = CombatLogger.getCombatLogger(living.getCustomName());
			
			if (logger != null) {
				Location location = living.getLocation();
				World world = living.getWorld();
				Player killer = living.getKiller();
				
				if (killer != null && killer.isOnline()) {
					APIPlayer.getPlayer(killer).addKill();
					killer.setStatistic(Statistic.PLAYER_KILLS, killer.getStatistic(Statistic.PLAYER_KILLS) + 1);
				}
				
				event.getDrops().clear();
				
				for (ItemStack item : logger.getItems()) {
					if (item == null || item.getType() == Material.AIR)
						continue;
					
					event.getDrops().add(item);
				}
				
				((ExperienceOrb) world.spawn(location, ExperienceOrb.class)).setExperience(logger.getExperience());
				OfflineAPIPlayer.getPlayer(logger.getUUID(), offline -> offline.addDeath());
				living.getWorld().strikeLightningEffect(living.getLocation());
			}
		}
	}
	
	@EventHandler
	public void LoggerInteract(EntityInteractEvent event) {
		Entity entity = event.getEntity();
		
		if (entity instanceof LivingEntity) {
			if (CombatLogger.getCombatLogger(((LivingEntity) entity).getCustomName()) != null)
				event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onLoggerTeleport(EntityTeleportEvent event) {
		Entity entity = event.getEntity();
		
		if (entity instanceof LivingEntity && CombatLogger.getCombatLogger(((LivingEntity) entity).getCustomName()) != null)
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onLoggerAttacked(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		Entity eAttacker = event.getDamager();
		
		if (entity instanceof LivingEntity && eAttacker instanceof Player) {
			Player attacker = (Player) eAttacker;
			CombatLogger logger = CombatLogger.getCombatLogger(((LivingEntity) entity).getCustomName());
			
			if (logger != null) {
				entity.setVelocity(new Vector(0, 0, 0));
				if (logger.getTeamMates().contains(attacker.getUniqueId()))
					event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onUnLoad(ChunkUnloadEvent event) {
		for (Entity entity : event.getChunk().getEntities()) {
			if (entity instanceof LivingEntity && CombatLogger.getCombatLogger(((LivingEntity) entity).getCustomName()) != null && !entity.isDead())
				event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onQuit(PlayerQuitEvent event) {
		if (OnimaFaction.getInstance().getSOTW().isRunning())
			return;
		
		Player player = event.getPlayer();
		FPlayer fPlayer = FPlayer.getPlayer(player);
		
		if (fPlayer.getApiPlayer().getHealth() == 0.0D) return;
		if (fPlayer.getApiPlayer().getTimeLeft(PvPTimerCooldown.class) > 0) return;
		
		Region region = fPlayer.getRegionOn();
		
		if (OnimaFaction.hasNearbyEnemies(fPlayer, ConfigurationService.COMBAT_LOGGER_ENEMIES_DISTANCE))
			return;
		
		if (!player.isDead() && !region.hasFlag(Flag.SAFE_DISCONNECT) && !OnimaPerm.COMBAT_LOGGER_BYPASS.has(player))
			fPlayer.spawnCombatLogger();
	}

}
