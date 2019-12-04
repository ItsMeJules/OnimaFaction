package net.onima.onimafaction.listener;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.google.common.collect.ImmutableList;

import net.onima.onimaapi.crates.SupplyCrate;
import net.onima.onimaapi.crates.utils.Crate;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.faction.claim.WildernessClaim;
import net.onima.onimafaction.players.FPlayer;

public class EnvironementListener implements Listener {
	
	private static List<Material> INTERACTABLES_MAT;
	
	static {
		INTERACTABLES_MAT = ImmutableList.<Material>builder().add(Material.BED).add(Material.BED_BLOCK).add(Material.BEACON).add(Material.FENCE_GATE).add(Material.IRON_DOOR).add(Material.TRAP_DOOR).add(Material.WOOD_DOOR).add(Material.IRON_DOOR_BLOCK).add(Material.WOODEN_DOOR).add(Material.CHEST).add(Material.TRAPPED_CHEST).add(Material.FURNACE).add(Material.BURNING_FURNACE).add(Material.BREWING_STAND).add(Material.HOPPER).add(Material.DROPPER).add(Material.DISPENSER).add(Material.STONE_BUTTON).add(Material.WOOD_BUTTON).add(Material.ENCHANTMENT_TABLE).add(Material.ANVIL).add(Material.LEVER).add(Material.FIRE).build();	
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event) {
		if (!event.hasBlock()) return;
		
		Action action = event.getAction();
		Block block = event.getClickedBlock();
		Location location = block.getLocation();
		Faction factionAt = Claim.getClaimAt(location).getFaction();
		Player player = event.getPlayer();
		int canBuild = tryToBuild(player, location);
		
		if (action == Action.PHYSICAL && canBuild < 1) {
			event.setCancelled(true);
			return;
		}
		
		if (canBuild < 1 && action == Action.RIGHT_CLICK_BLOCK) {
			if (factionAt.isSafeZone() || factionAt.isRoad()) {
				if (ConfigurationService.SPAWN_INTERACTABLES_BLOCKS.contains(block.getType())) 
					canBuild = 1;
			} else if (!factionAt.isWilderness()) {
				if (ConfigurationService.NOT_CLAIM_INTERACTABLES_BLOCKS.contains(block.getType()))
					canBuild = 1;
				
				SupplyCrate crate = null;
				
				if ((crate = SupplyCrate.getDroppedByLocation(block.getLocation())) != null) {
					crate.open(APIPlayer.getPlayer(player), Crate.NO_BOOSTER);
					canBuild = 1;
				}
			}
			
			if (canBuild < 1 && INTERACTABLES_MAT.contains(block.getType())) {
				event.setCancelled(true);
				player.sendMessage("§cVous ne pouvez pas intéragir avec ceci dans le territoire de " + factionAt.getDisplayName(player) + "§c.");
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		Entity entity = event.getEntity();
		
		if (entity instanceof LivingEntity && tryToBuild(entity, event.getBlock().getLocation()) < 1)
			event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Location location = event.getBlock().getLocation();
		int canBuild = tryToBuild(player, location);
		
		if (canBuild < 1) {
			player.sendMessage("§cVous ne pouvez pas casser de blocks dans " + (canBuild == 0 ? "l'end." : "le territoire de " + Claim.getClaimAt(location).getFaction().getDisplayName(player) + "§c."));
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Location location = event.getBlock().getLocation();
		int canBuild = tryToBuild(player, location);
		
		if (canBuild < 1) {
			player.sendMessage("§cVous ne pouvez pas poser de blocks dans " + (canBuild == 0 ? "l'end." : "le territoire de " + Claim.getClaimAt(location).getFaction().getDisplayName(player) + "§c."));
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		Location location = event.getBlockClicked().getLocation();
		int canBuild = tryToBuild(player, location);
		
		if (canBuild < 1) {
			player.sendMessage("§cVous ne pouvez pas vider de seau dans " + (canBuild == 0 ? "l'end." : "le territoire de " + Claim.getClaimAt(location).getFaction().getDisplayName(player) + "§c."));
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBucketFill(PlayerBucketFillEvent event) {
		Player player = event.getPlayer();
		Location location = event.getBlockClicked().getLocation();
		int canBuild = tryToBuild(player, location);
		
		if (canBuild < 1) {
			player.sendMessage("§cVous ne pouvez pas remplir de seau dans " + (canBuild == 0 ? "l'end." : "le territoire de " + Claim.getClaimAt(location).getFaction().getDisplayName(player) + "§c."));
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
		Entity remover = event.getRemover();
		
		if (remover instanceof Player) {
			Player player = (Player) remover;
			Location location = event.getEntity().getLocation();
			int canBuild = tryToBuild(player, location);
			
			if (canBuild < 1) {
				player.sendMessage("§cVous ne pouvez pas casser ceci dans " + (canBuild == 0 ? "l'end." : "le territoire de " + Claim.getClaimAt(location).getFaction().getDisplayName(player) + "§c."));
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onHangingPlace(HangingPlaceEvent event) {
		Player player = event.getPlayer();
		Location location = event.getEntity().getLocation();
		int canBuild = tryToBuild(player, location);
		
		if (canBuild < 1) {
			player.sendMessage("§cVous ne pouvez pas placer ceci dans " + (canBuild == 0 ? "l'end." : "le territoire de " + Claim.getClaimAt(location).getFaction().getDisplayName(player) + "§c."));
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
			int canBuild = tryToBuild(player, location);
			
			if (canBuild < 1) {
				player.sendMessage("§cVous ne pouvez pas endommager ceci dans " + (canBuild == 0 ? "l'end." : "le territoire de " + Claim.getClaimAt(location).getFaction().getDisplayName(player) + "§c."));
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onHangingInteract(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		
		if (entity instanceof Hanging) {
			if (tryToBuild(event.getPlayer(), entity.getLocation()) < 1) 
				event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockBurn(BlockBurnEvent event) {
		Faction faction = Claim.getClaimAt(event.getBlock().getLocation()).getFaction();
		
		if (faction.isWilderness()) return;
		
		if (!(faction instanceof PlayerFaction) || !((PlayerFaction) faction).isRaidable())
			event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockForm(BlockFormEvent event) {
		if (!(Claim.getClaimAt(event.getBlock().getLocation()) instanceof WildernessClaim)) event.setCancelled(true);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		if (event.isSticky()) {
			Location location = event.getRetractLocation();
			Block block = location.getBlock();
			
			if (!block.isLiquid() && !block.isEmpty()) {
				Faction factionAt = Claim.getClaimAt(location).getFaction();
				
				if (factionAt.isNormal() && !((PlayerFaction) factionAt).isRaidable() && !factionAt.getName().equalsIgnoreCase(Claim.getClaimAt(event.getBlock().getLocation()).getFaction().getName()))
					event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onStickyPistonExtend(BlockPistonExtendEvent event) {
		Block block = event.getBlock();
		Block targetBlock = block.getRelative(event.getDirection(), event.getLength() + 1);
		
		if (!targetBlock.isLiquid() && !targetBlock.isEmpty()) {
			Faction factionAt = Claim.getClaimAt(targetBlock.getLocation()).getFaction();
			
			if (factionAt.isNormal() && !((PlayerFaction) factionAt).isRaidable() && !factionAt.getName().equalsIgnoreCase(Claim.getClaimAt(event.getBlock().getLocation()).getFaction().getName()))
				event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		event.blockList().clear();
	}
	
	public static int tryToBuild(Entity entity, Location location) {
		Player player = null;
		
		if (entity instanceof Player)
			player = (Player) entity;
		else return -2;
		
		FPlayer fPlayer = FPlayer.getPlayer(player);
		
		if (fPlayer.hasFactionBypass()) return 1;
		if (location.getWorld().getEnvironment() == Environment.THE_END) return 0;
		
		Faction factionAt = Claim.getClaimAt(location).getFaction();
		PlayerFaction faction = fPlayer.getFaction();
		
		if (factionAt.isWilderness() 
				|| ((factionAt instanceof PlayerFaction) && ((PlayerFaction) factionAt).isRaidable())
				|| (faction != null && factionAt.getName().equalsIgnoreCase(faction.getName())))
			return 1;
		
		return -1;
	}
	
}
