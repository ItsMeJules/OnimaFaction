package net.onima.onimafaction.listener;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import net.onima.onimaapi.event.region.PlayerRegionChangeEvent;
import net.onima.onimaapi.event.region.PlayerRegionChangeEvent.PlayerRegionChangementCause;
import net.onima.onimaapi.event.region.RegionChangeEvent;
import net.onima.onimaapi.event.region.RegionChangeEvent.RegionChangeCause;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.time.Time;
import net.onima.onimaapi.zone.struct.Flag;
import net.onima.onimaapi.zone.type.Region;
import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.events.FactionClaimChangeEvent;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.players.FPlayer;

public class RegionListener implements Listener {
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onMove(PlayerMoveEvent event) {
		handleMove(event);
	}	
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onTeleport(PlayerTeleportEvent event) {
		handleMove(event);
	}	
	
	@EventHandler(ignoreCancelled = true)
	public void onClaimChange(FactionClaimChangeEvent event) {
		Claim claim = event.getClaim();
		FPlayer fPlayer = event.getFPlayer();
		APIPlayer apiPlayer = fPlayer.getApiPlayer();
		String claimOrUnclaim = event.getClaimChangeCause() == FactionClaimChangeEvent.ClaimChangeCause.CLAIM ? "§cclaim" : "§aunclaim";
		
		Bukkit.getScheduler().runTaskAsynchronously(OnimaFaction.getInstance(), () -> {
			for (Player insidePlayer : claim.toCuboid().getPlayers()) {
				FPlayer fInside = FPlayer.getPlayer(insidePlayer);
				
				fInside.setRegionOn(Claim.getClaimAndRegionAt(insidePlayer.getLocation()));
				insidePlayer.sendMessage("§d§o" + apiPlayer.getDisplayName() + " §7a " + claimOrUnclaim + " §7le territoire où vous vous trouvez. Vous êtes maintenant dans " + fInside.getRegionOn().getDisplayName(insidePlayer) + "§7.");
			}
		});
	}
	
	@EventHandler
	public void onRegionChange(RegionChangeEvent event) {
		RegionChangeCause cause = event.getCause();
		Region region = event.getRegion();
		
		if (!(event instanceof PlayerRegionChangeEvent)) {
			Bukkit.getScheduler().runTaskAsynchronously(OnimaFaction.getInstance(), () -> {
				String change = "";
				
				switch ((RegionChangeEvent.RegionChangementCause) cause) {
				case CREATED:
					change = "§acréée";
					break;
				case RESIZE:
					change = "§6modifiée";
					break;
				case DELETED:
					change = "§csupprimée";
					break;
				default:
					break;
				}
				
				for (Player player : event.getRegion().toCuboid().getPlayers()) {
					FPlayer fInside = FPlayer.getPlayer(player);
					
					fInside.setRegionOn(Claim.getClaimAndRegionAt(player.getLocation()));
					player.sendMessage("§7Une région a été " + change + " §7à l'endroit où vous vous trouvez. §dVous §7êtes maintenant dans " + region.getDisplayName(player) + "§7.");
				}
			});
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Location location = event.getBlock().getLocation();
		
		System.out.println(tryToBuild(player, location, Flag.BREAK_BLOCK));
		
		if (tryToBuild(player, location, Flag.BREAK_BLOCK) == -1) {
			player.sendMessage("§cVous ne pouvez pas casser de blocks dans le territoire de " + Claim.getClaimAndRegionAt(location).getDisplayName(player) + "§c.");
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Location location = event.getBlock().getLocation();
		
		if (tryToBuild(player, location, Flag.PLACE_BLOCK) == -2) {
			player.sendMessage("§cVous ne pouvez pas poser de blocks dans le territoire de " + Claim.getClaimAndRegionAt(location).getDisplayName(player) + "§c.");
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		Location location = event.getBlockClicked().getLocation();
		
		if (tryToBuild(player, location, Flag.PLACE_BLOCK) == -2) {
			player.sendMessage("§cVous ne pouvez pas vider de seau dans le territoire de " + Claim.getClaimAndRegionAt(location).getDisplayName(player) + "§c.");
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBucketFill(PlayerBucketFillEvent event) {
		Player player = event.getPlayer();
		Location location = event.getBlockClicked().getLocation();
		
		if (tryToBuild(player, location, Flag.BREAK_BLOCK) == -1) {
			player.sendMessage("§cVous ne pouvez pas remplir de seau dans le territoire de " + Claim.getClaimAndRegionAt(location).getDisplayName(player) + "§c.");
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
		Entity remover = event.getRemover();
		
		if (remover instanceof Player) {
			Player player = (Player) remover;
			Location location = event.getEntity().getLocation();
			
			if (tryToBuild(player, location, Flag.BREAK_BLOCK) == -1) {
				player.sendMessage("§cVous ne pouvez pas casser ceci dans le territoire de " + Claim.getClaimAndRegionAt(location).getDisplayName(player) + "§c.");
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onHangingPlace(HangingPlaceEvent event) {
		Player player = event.getPlayer();
		Location location = event.getEntity().getLocation();
		
		if (tryToBuild(player, location, Flag.PLACE_BLOCK) == -2) {
			player.sendMessage("§cVous ne pouvez pas placer ceci dans le territoire de " + Claim.getClaimAndRegionAt(location).getDisplayName(player) + "§c.");
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onHangingDamageByEntity(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		
		if (entity instanceof Hanging) {
			Player player = Methods.getLastAttacker(event);
			
			if (player == null) return;
			
			Location location = event.getEntity().getLocation();
			
			if (tryToBuild(player, location, Flag.BREAK_BLOCK) == -1) {
				player.sendMessage("§cVous ne pouvez pas endommager ceci dans le territoire de " + Claim.getClaimAndRegionAt(location).getDisplayName(player) + "§c.");
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onHangingInteract(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		
		if (entity instanceof Hanging) {
			if (tryToBuild(event.getPlayer(), entity.getLocation(), Flag.NO_INTERACT) == -4) 
				event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		
		if (action == Action.LEFT_CLICK_BLOCK)
			return;
		
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		
		if (block != null && tryToBuild(player, block.getLocation(), Flag.NO_INTERACT) == -4) {
			event.setCancelled(true);
			
			List<MetadataValue> value = player.getMetadata("interact-region");
			long now = System.currentTimeMillis();
			
			if (value.isEmpty() || value.get(0).asLong() - now <= 0L) {
				if (action != Action.PHYSICAL)
					player.setMetadata("interact-region", new FixedMetadataValue(OnimaFaction.getInstance(), now + 20 * Time.SECOND));
				
				player.sendMessage("§cVous ne pouvez pas intéragir avec ceci dans le territoire de " + Claim.getClaimAndRegionAt(event.getClickedBlock().getLocation()).getDisplayName(player) + "§c.");
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockBurn(BlockBurnEvent event) {
		if (Claim.getClaimAndRegionAt(event.getBlock().getLocation()).hasFlag(Flag.NATURAL_PROTECT))
			event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockForm(BlockFormEvent event) {
		if (Claim.getClaimAndRegionAt(event.getBlock().getLocation()).hasFlag(Flag.NATURAL_PROTECT))
			event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		if (event.isSticky()) {
			Location location = event.getRetractLocation();
			Block block = location.getBlock();
			
			if (!block.isLiquid() && !block.isEmpty()) {
				
				if (Claim.getClaimAndRegionAt(location).hasFlag(Flag.NATURAL_PROTECT))
					event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onStickyPistonExtend(BlockPistonExtendEvent event) {
		Block block = event.getBlock();
		Block targetBlock = block.getRelative(event.getDirection(), event.getLength() + 1);
		
		if (!targetBlock.isLiquid() && !targetBlock.isEmpty()) {
			
			if (Claim.getClaimAndRegionAt(targetBlock.getLocation()).hasFlag(Flag.NATURAL_PROTECT))
				event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		Region region = Claim.getClaimAndRegionAt(player.getLocation());
		
		if (region.hasFlag(Flag.PICKUP_ITEM)) {
			event.setCancelled(true);
			
			List<MetadataValue> value = player.getMetadata("pickup-region");
			long now = System.currentTimeMillis();
			
			if (value.isEmpty() || value.get(0).asLong() - now <= 0L) {
				player.setMetadata("pickup-region", new FixedMetadataValue(OnimaFaction.getInstance(), now + 20 * Time.SECOND));
				player.sendMessage("§cVous ne pouvez pas ramasser d'items dans le territoire de " + region.getDisplayName(player) + "§c.");
			}
		}
	}
	
	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		Region region = Claim.getClaimAndRegionAt(player.getLocation());
		
		if (region.hasFlag(Flag.ITEM_DROP)) {
			player.sendMessage("§cVous ne pouvez pas jeter d'items dans le territoire de " + region.getDisplayName(player) + "§c.");
			event.setCancelled(true);
		}
	}
	
	public static int tryToBuild(Entity entity, Location location, Flag flag) {
		Player player = null;
		
		if (entity instanceof Player)
			player = (Player) entity;
		else return 0;
	
		if (FPlayer.getPlayer(player).hasFactionBypass()) return 1;
		
		Region region = Claim.getClaimAndRegionAt(location);
		
		if (flag == Flag.BREAK_BLOCK && region.hasFlag(Flag.BREAK_BLOCK))
			return -1;
		else if (flag == Flag.PLACE_BLOCK && region.hasFlag(Flag.PLACE_BLOCK))
			return -2;
		else if (flag == Flag.NO_INTERACT && region.hasFlag(Flag.NO_INTERACT))
			return -4;
		
		return 1;
	}
	
	public void handleMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		Location from = event.getFrom(), to = event.getTo();
		
		if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) return;
		
		Region fromRegion = Claim.getClaimAndRegionAt(from);
		Region toRegion = Claim.getClaimAndRegionAt(to);
		
		if (!fromRegion.getName().equalsIgnoreCase(toRegion.getName())) {
			boolean isTeleport = event instanceof PlayerTeleportEvent;
			
			PlayerRegionChangeEvent regionChange = new PlayerRegionChangeEvent(APIPlayer.getPlayer(player.getUniqueId()), from, to, fromRegion, toRegion, isTeleport ? PlayerRegionChangementCause.TELEPORT : PlayerRegionChangementCause.MOVE);
			Bukkit.getPluginManager().callEvent(regionChange);
			
			if (regionChange.isCancelled()) {
				if (isTeleport)
					event.setCancelled(true);
				else {
					from.setX(from.getBlockX() + 0.5);
					from.setZ(from.getBlockZ() + 0.5);
					event.setTo(from);
				}
				return;
			}
			
			FPlayer.getPlayer(player.getUniqueId()).setRegionOn(toRegion);
			
			String fromDispName = fromRegion.getDisplayName(player), toDispName = toRegion.getDisplayName(player);
			
			if (fromDispName.equalsIgnoreCase(toDispName)) return;
			
			boolean isLeaveDeathban = fromRegion.isDeathbannable(), isEnterDeathban = toRegion.isDeathbannable();
			boolean shouldLeaveMessage = !fromRegion.hasFlag(Flag.NO_LEAVING_MESSAGE), shouldEnterMessage = !toRegion.hasFlag(Flag.NO_LEAVING_MESSAGE);
		
			String leaveMsg = shouldLeaveMessage ? "§eQuitté: " + fromDispName + " §e(" + (isLeaveDeathban ? "§cDeathban" : "§bNon-Deathban") + "§e)" + (isLeaveDeathban ? " §cx" + fromRegion.getDeathbanMultiplier() : "") : "";
			String enterMsg = shouldEnterMessage ? "§eEntré: " + toDispName + " §e(" + (isEnterDeathban ? "§cDeathban" : "§bNon-Deathban") + "§e)" + (isEnterDeathban ? " §cx" + toRegion.getDeathbanMultiplier() : "") : "";
			
			player.sendMessage((shouldLeaveMessage ? leaveMsg + (shouldEnterMessage ? "§7, " + enterMsg : "") : (shouldEnterMessage ? enterMsg : "")));
		}
	}
	
}
