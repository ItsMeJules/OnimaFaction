package net.onima.onimafaction.cooldowns;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import net.onima.onimaapi.cooldown.utils.Cooldown;
import net.onima.onimaapi.event.region.PlayerRegionChangeEvent;
import net.onima.onimaapi.fakeblock.FakeType;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.time.Time;
import net.onima.onimaapi.utils.time.Time.LongTime;
import net.onima.onimaapi.zone.Cuboid;
import net.onima.onimaapi.zone.struct.Flag;
import net.onima.onimaapi.zone.type.Region;
import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.events.FactionClaimChangeEvent;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.faction.claim.WildernessClaim;
import net.onima.onimafaction.players.FPlayer;

public class PvPTimerCooldown extends Cooldown implements Listener {
	
	public static final long ITEMS_PICKUP_DELAY;
	
	private static final String ITEM_PICKUP_MESSAGE_META_KEY;
	private static final String PORTAL_MESSAGE_META_KEY;
	
	static {
		ITEMS_PICKUP_DELAY = 20 * Time.SECOND;
		ITEM_PICKUP_MESSAGE_META_KEY = "pickupMessageDelay";
		PORTAL_MESSAGE_META_KEY = "portalMessageDelay";
	}
	
	private Map<UUID, Long> items;
	
	{
		items = new HashMap<>();
	}

	public PvPTimerCooldown() {
		super("pvp_timer", (byte) 5, 30 * Time.MINUTE);
	}

	@Override
	public String scoreboardDisplay(long timeLeft) {
		return "§cInvincibilité §6: §c" + LongTime.setHMSFormat(timeLeft);
	}

	@Override
	public boolean action(OfflineAPIPlayer offline) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void onStart(OfflineAPIPlayer offline, long time) {
		if (OnimaFaction.getInstance().getEOTW().isRunning())
			return;
		
		super.onStart(offline, time);
		
		if (offline.isOnline()) {
			APIPlayer apiPlayer = (APIPlayer) offline;
			Player player = apiPlayer.toPlayer();
			Region region = Claim.getClaimAndRegionAt(player.getLocation());
			
			apiPlayer.sendMessage("§aVous avez maintenant votre pvp timer pour " + LongTime.setYMDWHMSFormat(duration) + '.');
			
			if (!(region instanceof WildernessClaim) && region.hasFlag(Flag.PVP_TIMER_PAUSE)) {
				apiPlayer.sendMessage("§7Tant que vous restez dans " + region.getDisplayName(player) + "§7, votre pvp timer sera en §apause§7.");
				super.startPause(apiPlayer);
			}
		}
	}
	
	@Override
	public void onExpire(OfflineAPIPlayer offline) {
		if (offline.isOnline()) {
			APIPlayer apiPlayer = (APIPlayer) offline;
			
			apiPlayer.sendMessage("§cVous n'avez plus votre pvp timer, bonne chance !");
			apiPlayer.removeFakeBlockByType(FakeType.PVP_TIMER_REGION_BORDER);
		}
		
		super.onExpire(offline);
	}
	
	@Override
	public void onCancel(OfflineAPIPlayer offline) {
		if (offline.isOnline())
			((APIPlayer) offline).removeFakeBlockByType(FakeType.PVP_TIMER_REGION_BORDER);
		
		super.onCancel(offline);
	}
	
	@Override
	public void startPause(OfflineAPIPlayer offline) {
		if (offline.isOnline())
			((APIPlayer) offline).sendMessage("§7Votre §epvp timer §7est maintenant en pause.");
		
		super.startPause(offline);
	}
	
	@Override
	public void stopPause(OfflineAPIPlayer offline) {
		if (offline.isOnline())
			((APIPlayer) offline).sendMessage("§7Votre §epvp timer §7n'est plus en pause.");
		
		super.stopPause(offline);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onClaimChange(FactionClaimChangeEvent event) {
		if (event.getClaimChangeCause() == FactionClaimChangeEvent.ClaimChangeCause.UNCLAIM) return;
		
		Cuboid cuboid = event.getClaim().toCuboid();
		List<Player> players = cuboid.getPlayers();
		
		if (!players.isEmpty()) {
			Location location = cuboid.getWorld().getHighestBlockAt(cuboid.getMinimumLocation().add(-1, 0, -1)).getLocation();
			Faction faction = event.getFaction();
			
			for (Player player : players) {
				if (getTimeLeft(player.getUniqueId()) > 0L && player.teleport(location))
					player.sendMessage("§7La faction §d§o" + faction.getName() + " §7vient de claim cet endroit. Vu que vous avez votre §epvp timer §7d'actif, vous avez été téléporté en-dehors.");
			}
		}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		
		onStart(APIPlayer.getPlayer(player));
		onPlayerSpawnLocation(new PlayerSpawnLocationEvent(player, event.getRespawnLocation()));
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Location location = event.getEntity().getLocation();
		World world = location.getWorld();
		long time = System.currentTimeMillis() + ITEMS_PICKUP_DELAY;
		
		for (ItemStack item : event.getDrops())
			items.put(world.dropItemNaturally(location, item).getUniqueId(), time);

		event.getDrops().clear();
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		long remaining = getTimeLeft(player.getUniqueId());
		
		if (remaining > 0L) {
			event.setCancelled(true);
			player.sendMessage("§cVous ne pouvez pas vider votre seau tant que votre §epvp timer §cest actif (§e" + LongTime.setHMSFormat(remaining) + "§c).");
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onBlockIgnite(BlockIgniteEvent event) {
		Player player = event.getPlayer();
		
		if (player == null)
			return;
		
		long remaining = getTimeLeft(player.getUniqueId());
		
		if (remaining > 0L) {
			event.setCancelled(true);
			player.sendMessage("§cVous ne pouvez pas brûler de blocks tant que votre §epvp timer §cest actif (§e" + LongTime.setHMSFormat(remaining) + "§c).");
		}
	}
	
	@EventHandler
	public void onPlayerPickupItems(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		long timeLeft = getTimeLeft(player.getUniqueId());
		
		if (timeLeft > 0L) {
			UUID uuid = event.getItem().getUniqueId();
			Long delay = items.get(uuid);
			
			if (delay == null)
				return;
			
			long remaining = delay - System.currentTimeMillis();
			
			if (remaining > 0) {
				event.setCancelled(true);
				
				List<MetadataValue> value = player.getMetadata(ITEM_PICKUP_MESSAGE_META_KEY);
				long now = System.currentTimeMillis();
				
				if (value != null && !value.isEmpty() && value.get(0).asLong() - now <= 0L) {
					player.setMetadata(ITEM_PICKUP_MESSAGE_META_KEY, new FixedMetadataValue(OnimaFaction.getInstance(), now + ITEM_PICKUP_MESSAGE_META_KEY));
					player.sendMessage("§cVous ne pouvez pas ramasser cet item pendant §e" + LongTime.setHMSFormat(remaining) + "§c. Votre §epvp timer §cest actif (§e" + LongTime.setHMSFormat(timeLeft) + "§c).");
				}
			} else
				items.remove(uuid);
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (getTimeLeft(event.getPlayer().getUniqueId()) > 0L)
			startPause(APIPlayer.getPlayer(event.getPlayer()));
	}
	
	@EventHandler
	public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
		Location location = event.getSpawnLocation();
		Player player = event.getPlayer();
		FPlayer fPlayer = FPlayer.getPlayer(player);
		APIPlayer apiPlayer = fPlayer.getApiPlayer();
		Region region = Claim.getClaimAndRegionAt(location);
		
		if (!player.hasPlayedBefore() && !OnimaFaction.getInstance().getEOTW().isRunning() && !OnimaFaction.getInstance().getSOTW().isRunning())
			onStart(apiPlayer);
		 
		long timeLeft = getTimeLeft(player.getUniqueId());
		
		if (!(region instanceof WildernessClaim) && region.hasFlag(Flag.PVP_TIMER_PAUSE) && timeLeft > 0L)
			startPause(apiPlayer);
		else if (timeLeft > 0L && isPaused(player.getUniqueId()))
			stopPause(apiPlayer);
	}
	
	@EventHandler
	public void onAreaEnter(PlayerRegionChangeEvent event) {
		APIPlayer apiPlayer = event.getAPIPlayer();
		long timeLeft = getTimeLeft(apiPlayer.getUUID());
		
		if (timeLeft <= 0L) 
			return;
		
		Region to = event.getNewRegion();
		Region from = event.getRegion();

		if (event.getCause() == PlayerRegionChangeEvent.PlayerRegionChangementCause.TELEPORT && to instanceof Claim && ((Claim) to).getFaction().equals(FPlayer.getPlayer(apiPlayer.getUUID()).getFaction())) {
			apiPlayer.sendMessage("§bVous êtes entré dans votre propre claim, vous avez donc perdu votre pvp timer.");
			onCancel(apiPlayer);
		}
	
		if (to.hasFlag(Flag.PVP_TIMER_DENY_ENTRY)) {
			event.setCancelled(true);
			apiPlayer.sendMessage("§cVous ne pouvez pas entrer dans " + to.getDisplayName(apiPlayer.toPlayer()) + " §ctant que votre §epvp timer §cest actif (§e" + LongTime.setHMSFormat(timeLeft) + "§c).");
		} else if (to.hasFlag(Flag.PVP_TIMER_PAUSE)) {
			if (!from.hasFlag(Flag.PVP_TIMER_PAUSE))
				startPause(apiPlayer);
		} else if (from.hasFlag(Flag.PVP_TIMER_PAUSE))
			stopPause(apiPlayer);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onAttack(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		
		if (entity instanceof Player) {
			Player player = (Player) entity;
			Player attacker = Methods.getLastAttacker(event);
			
			if (attacker == null || attacker.equals(entity))
				return;
			
			long timeLeft;
			
			if ((timeLeft = getTimeLeft(attacker.getUniqueId())) > 0L) {
				attacker.sendMessage("§cVous ne pouvez pas attaquer §e" + Methods.getName(player) + " §ccar votre §epvp timer §cest actif (§e" + LongTime.setDHMSFormat(timeLeft) + "§c).");
				event.setCancelled(true);
			} else if ((timeLeft = getTimeLeft(entity.getUniqueId())) > 0L) {
				attacker.sendMessage("§cVous ne pouvez pas attaquer §e" + Methods.getName(player) + " §ccar son §epvp timer §cest actif (§e" + LongTime.setDHMSFormat(timeLeft) + "§c).");
				event.setCancelled(true);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPotionSplash(PotionSplashEvent event) {
		ThrownPotion potion = event.getPotion();
		
		if (potion.getShooter() instanceof Player && Methods.isDebuff(potion)) {
			Player shooter = (Player) potion.getShooter();
			List<Player> affected = event.getAffectedEntities().stream().filter(entity -> entity instanceof Player).map(entity -> (Player) entity).collect(Collectors.toList());
		
			if (getTimeLeft(shooter.getUniqueId()) > 0L && !affected.isEmpty()) {
				shooter.sendMessage("§cVous ne pouvez pas attaquer §edes joueurs §ccar votre §epvp timer §cest actif (§e" + LongTime.setDHMSFormat(getTimeLeft(shooter.getUniqueId())) + "§c).");
				for (Player player : affected)
					event.setIntensity(player, 0);
			}
			
			for (Player player : affected) {
				if (!shooter.equals(player)) {
					long timeLeft;
					
					if ((timeLeft = getTimeLeft(player.getUniqueId())) > 0L) {
						shooter.sendMessage("§cVous ne pouvez pas attaquer §e" + Methods.getName(shooter) + " §ccar son §epvp timer §cest actif (§e" + LongTime.setDHMSFormat(timeLeft) + "§c).");
						event.setIntensity(player, 0);
					}
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerPortalPVPTimer(PlayerPortalEvent event) {
		World toWorld = event.getTo().getWorld();

		if (toWorld == null)
			return;
		
		Player player = event.getPlayer();
		boolean denyTeleport = false;
		
		if (getTimeLeft(player.getUniqueId()) >= 0L && toWorld.getEnvironment() == Environment.THE_END)
			denyTeleport = true;
		
		if (denyTeleport) {
			List<MetadataValue> value = player.getMetadata(PORTAL_MESSAGE_META_KEY);
			long now = System.currentTimeMillis();
			
			if (value != null && !value.isEmpty() && value.get(0).asLong() - now <= 0L) {
				player.setMetadata(PORTAL_MESSAGE_META_KEY, new FixedMetadataValue(OnimaFaction.getInstance(), now + ITEM_PICKUP_MESSAGE_META_KEY));
				player.sendMessage("§cVous ne pouvez pas vous téléporter tant que votre pvp timer est actif.");
			}
			
			event.setCancelled(true);
		}
	}
	
}
