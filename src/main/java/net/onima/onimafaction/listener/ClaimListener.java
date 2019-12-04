package net.onima.onimafaction.listener;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.onima.onimaapi.fakeblock.FakeBlock;
import net.onima.onimaapi.fakeblock.FakeType;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.zone.Cuboid;
import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.faction.claim.ClaimSelection;
import net.onima.onimafaction.players.FPlayer;

public class ClaimListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onClaimWandInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		ItemStack wand = event.getItem();
		
		if (action == Action.PHYSICAL || wand == null || !Claim.CLAIMING_WAND.equals(wand))
			return;
		
		Player player = event.getPlayer();
		FPlayer fPlayer = FPlayer.getPlayer(player);
		APIPlayer apiPlayer = fPlayer.getApiPlayer();
		
		if (action.toString().contains("BLOCK")) {
			Block block = event.getClickedBlock();
			ClaimSelection claimSelection = fPlayer.getClaimSelection() == null ? new ClaimSelection() : fPlayer.getClaimSelection();
			Location location = block.getLocation();
			Location selectionLocation = claimSelection.getLocation(action);
			
			fPlayer.setClaimSelection(claimSelection);
			
			if (action == Action.RIGHT_CLICK_BLOCK)
				event.setCancelled(true);
			
			if (Claim.canSelectHere(player, location, true)) {
				
				if (selectionLocation != null) {
					if (Methods.locationEquals(selectionLocation, location) || Methods.locationEquals(selectionLocation.clone().add(0, 1, 0), location)) return;
					if ((System.currentTimeMillis() - claimSelection.getLastUpdate()) <= 2000L) {
						player.sendMessage("§cVeuillez patienter une seconde avant de repositionner votre location...");
						return;
					}
				} 
				
				claimSelection.setLocation(action, location);
			} else return;
			
			Location second = Methods.locationEquals(claimSelection.getLocation1(), location) ? claimSelection.getLocation2() : claimSelection.getLocation1();
			
			if (second != null) {
				if ((Math.abs(second.getBlockX() - location.getBlockX()) + 1) < ConfigurationService.CLAIM_MIN_LENGTH || (Math.abs(second.getBlockZ() - location.getBlockZ()) + 1) < ConfigurationService.CLAIM_MIN_LENGTH) {
					player.sendMessage("§cLa longueur d'un claim ne doit pas être en-dessous de " + ConfigurationService.CLAIM_MIN_LENGTH);
					return;
				} 
			}
			
			player.sendMessage("§d§oVous §7avez selectionné la position §d§o#" + claimSelection.getPosition(action) + " §7en : §e(" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")§7.");

			Bukkit.getScheduler().runTaskAsynchronously(OnimaFaction.getInstance(), () -> {
				if (selectionLocation != null && !claimSelection.getPillar(action).isEmpty())
					claimSelection.getPillar(action).parallelStream().forEach(fb -> apiPlayer.removeFakeBlock(fb));
				
				Location cloned = location.clone();
				List<Location> pillar = new ArrayList<>();
				
				while (cloned.add(0, 1, 0).getBlockY() <= ConfigurationService.CLAIM_MAX_HEIGHT)
					pillar.add(cloned.clone());
				
				FakeBlock.generate(apiPlayer, claimSelection.setPillar(action, FakeType.CREATE_CLAIM.toBlocks(pillar, 0)));
			});
			
			if (claimSelection.hasBothLocationSet()) {
				Cuboid cuboid = claimSelection.toCuboid();
				PlayerFaction faction = fPlayer.getFaction();
				double price = ClaimSelection.calculatePrice(cuboid, faction.getClaims().size());
				
				player.sendMessage((faction.getMoney() >= price ? "§a" : "§c") + "Prix du claim : " + price + ConfigurationService.MONEY_SYMBOL + " §7taille : §d§o" + cuboid.getXLength() + ", " + cuboid.getZLength() + ".");
			}
		}
		
		if (action == Action.RIGHT_CLICK_AIR && fPlayer.getClaimSelection() != null) {
			fPlayer.getClaimSelection().remove(apiPlayer);
			player.sendMessage("§d§oVous §7avez supprimé la sélection de votre claim.");
			return;
		}
		
		if (player.isSneaking() && (action.toString().contains("LEFT"))) {
			ClaimSelection claimSelection = fPlayer.getClaimSelection();
			
			if (claimSelection == null || !claimSelection.hasBothLocationSet()) {
				player.sendMessage("§cVous n'avez pas défini les deux locations pour le claim.");
				return;
			}
			
			if (Claim.tryToBuyClaim(player, claimSelection)) {
				fPlayer.getClaimSelection().remove(apiPlayer);
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event) {
		if (Claim.CLAIMING_WAND.equals(event.getPlayer().getItemInHand()))
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			if (Claim.CLAIMING_WAND.equals(player.getItemInHand()))
				removeClaimSelection(player);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerKick(PlayerKickEvent event) {
		event.getPlayer().getInventory().remove(Claim.CLAIMING_WAND);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.getPlayer().getInventory().remove(Claim.CLAIMING_WAND);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDrop(PlayerDropItemEvent event) {
		Item item = event.getItemDrop();
		
		if (Claim.CLAIMING_WAND.equals(item.getItemStack())) {
			item.remove();
			removeClaimSelection(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerPickup(PlayerPickupItemEvent event) {
		Item item = event.getItem();
		
		if (Claim.CLAIMING_WAND.equals(item.getItemStack())) {
			item.remove();
			removeClaimSelection(event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (event.getDrops().remove(Claim.CLAIMING_WAND))
			removeClaimSelection(event.getEntity());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryOpen(InventoryOpenEvent event) {
		HumanEntity humanEntity = event.getPlayer();
		if (humanEntity instanceof Player) {
			PlayerInventory inventory = humanEntity.getInventory();
			
			if (inventory.contains(Claim.CLAIMING_WAND)) {
				inventory.remove(Claim.CLAIMING_WAND);
				removeClaimSelection((Player) humanEntity);
			}
		}
	}
	
	private void removeClaimSelection(Player player) {
		FPlayer fPlayer = FPlayer.getPlayer(player);
		ClaimSelection claimSelection = fPlayer.getClaimSelection();
		
		if (claimSelection != null) claimSelection.remove(fPlayer.getApiPlayer());
	}

}
