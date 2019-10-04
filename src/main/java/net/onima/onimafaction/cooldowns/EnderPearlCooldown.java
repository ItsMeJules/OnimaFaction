package net.onima.onimafaction.cooldowns;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Sets;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.PacketPlayOutSetSlot;
import net.minecraft.server.v1_7_R4.PlayerInventory;
import net.onima.onimaapi.OnimaAPI;
import net.onima.onimaapi.cooldown.utils.Cooldown;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.WorldBorder;
import net.onima.onimaapi.utils.time.Time;
import net.onima.onimaapi.utils.time.Time.LongTime;
import net.onima.onimaapi.zone.struct.Flag;
import net.onima.onimaapi.zone.type.Region;
import net.onima.onimafaction.faction.claim.Claim;

public class EnderPearlCooldown extends Cooldown implements Listener {
	
	private final Map<UUID, PearlNameFaker> itemNameFakes;
	private Set<Material> blockedPearlTypes;
	
	{
		itemNameFakes = new HashMap<>();
		blockedPearlTypes = Sets.newHashSet(Material.THIN_GLASS, Material.IRON_FENCE, Material.FENCE, Material.NETHER_FENCE, Material.FENCE_GATE, Material.ACACIA_STAIRS, Material.BIRCH_WOOD_STAIRS, Material.BRICK_STAIRS, Material.COBBLESTONE_STAIRS, Material.DARK_OAK_STAIRS, Material.JUNGLE_WOOD_STAIRS, Material.NETHER_BRICK_STAIRS, Material.QUARTZ_STAIRS, Material.SANDSTONE_STAIRS, Material.SMOOTH_STAIRS, Material.SPRUCE_WOOD_STAIRS, Material.WOOD_STAIRS);
	}

	public EnderPearlCooldown() {
		super("enderpearl", (byte) 0, 16 * Time.SECOND);
	}

	@Override
	public String scoreboardDisplay(long timeLeft) {
		return "§eEnderpearl §6: §c" + LongTime.setHMSFormat(timeLeft);
	}
	
	@Override
	public void onStart(OfflineAPIPlayer offline) {
		super.onStart(offline);
		
		if (offline.isOnline())
			((APIPlayer) offline).sendMessage("§7Vous avez maintenant un cooldown d'§eenderpearl §7pour §c" + LongTime.setHMSFormat(duration) + "§7.");
	}
	
	@Override
	public void onExpire(OfflineAPIPlayer offline) {
		super.onExpire(offline);
		
		if (offline.isOnline()) {
			APIPlayer apiPlayer = (APIPlayer) offline;
			
			apiPlayer.sendMessage("§aVous pouvez de nouveau lancer des §eenderpearls§a.");
			stopDisplaying(apiPlayer.toPlayer());
		}
	}
	
	@Override
	public boolean action(OfflineAPIPlayer offline) {
		return false;
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (event.getAction().toString().contains("RIGHT")) {
			if (player.getItemInHand().getType() == Material.ENDER_PEARL) {
				long remaining = getTimeLeft(player.getUniqueId());

				if (remaining > 0L)
					event.setCancelled(true);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		Projectile projectile = event.getEntity();
		
		if (projectile instanceof EnderPearl) {
			ProjectileSource source = ((EnderPearl) projectile).getShooter();
			
			if (source instanceof Player) {
				Player shooter = (Player) source;
				long remaining = getTimeLeft(shooter.getUniqueId());
				
				if (remaining > 0L) {
					shooter.sendMessage("§cVous devez attendre " + LongTime.setHMSFormat(remaining) + " avant de pouvoir lancer une autre enderpearl !");
					shooter.setItemInHand(shooter.getItemInHand());
					event.setCancelled(true);
					return;
				} else {
					onStart(OfflineAPIPlayer.getByOfflinePlayer(shooter));
					startDisplaying(shooter);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerItemHeld(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		PearlNameFaker pearlNameFaker = itemNameFakes.get(player.getUniqueId());
		
		if (pearlNameFaker != null) {
			int previousSlot = event.getPreviousSlot();
			ItemStack item = player.getInventory().getItem(previousSlot);
			
			if (item == null)
				return;

			pearlNameFaker.setFakeItem(CraftItemStack.asNMSCopy(item), previousSlot);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onInventoryDrag(InventoryDragEvent event) {
		HumanEntity humanEntity = event.getWhoClicked();
		
		if (humanEntity instanceof Player) {
			Player player = (Player) humanEntity;
			PearlNameFaker pearlNameFaker = itemNameFakes.get(player.getUniqueId());
			
			if (pearlNameFaker == null) return;
			
			for (Entry<Integer, ItemStack> entry : event.getNewItems().entrySet()) {
				if (entry.getKey() == player.getInventory().getHeldItemSlot()) {
					pearlNameFaker.setFakeItem(CraftItemStack.asNMSCopy(player.getItemInHand()), player.getInventory().getHeldItemSlot());
					break;
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onInventoryClick(InventoryClickEvent event) {
		HumanEntity humanEntity = event.getWhoClicked();
		if (humanEntity instanceof Player) {
			Player player = (Player) humanEntity;
			PearlNameFaker pearlNameFaker = itemNameFakes.get(player.getUniqueId());
			
			if (pearlNameFaker == null) return;

			// Required to prevent ghost items.
			int heldSlot = player.getInventory().getHeldItemSlot();
			
			if (event.getSlot() == heldSlot)
				pearlNameFaker.setFakeItem(CraftItemStack.asNMSCopy(player.getItemInHand()), heldSlot);
			else if (event.getHotbarButton() == heldSlot) {
				pearlNameFaker.setFakeItem(CraftItemStack.asNMSCopy(event.getCurrentItem()), event.getSlot());
				Bukkit.getScheduler().runTask(OnimaAPI.getInstance(), () -> player.updateInventory());
			}
		}
	}
	
	@EventHandler
	public void onPearlClip(PlayerTeleportEvent event) {
		if (event.getCause() == TeleportCause.ENDER_PEARL && blockedPearlTypes.contains(event.getTo().getBlock().getType())) {
			Player player = event.getPlayer();
			APIPlayer apiPlayer = APIPlayer.getByPlayer(player);
			
			player.sendMessage("§cVotre enderpearl a atteri sur un block interdit.");
			apiPlayer.removeCooldown(EnderPearlCooldown.class);
			player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
			event.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onEnderpearlLand(PlayerTeleportEvent event) {
		APIPlayer apiPlayer = APIPlayer.getByPlayer(event.getPlayer());
		
		if (event.getCause() == TeleportCause.ENDER_PEARL) {
			Region region = Claim.getClaimAndRegionAt(event.getTo());
			
			if (region.hasFlag(Flag.DENY_ENDERPEARL)) {
				onCancel(apiPlayer);
				apiPlayer.toPlayer().getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
				apiPlayer.sendMessage("§cVous ne pouvez pas lancer d'§eenderpearl §cdans " + region.getDisplayName(apiPlayer.toPlayer()));
				event.setCancelled(true);
			}
		} else if (event.getCause() == TeleportCause.ENDER_PEARL && WorldBorder.border(event.getTo()) && !apiPlayer.getRank().getRankType().hasPermission(OnimaPerm.WORLD_BORDER_BYPASS)) {
			apiPlayer.sendMessage("§cVous ne pouvez pas lancer d'§eenderpearls §cau-delà de la bordure !");		
			onCancel(apiPlayer);
			apiPlayer.toPlayer().getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
			event.setCancelled(true);
		}
	}
	
		
	/**
	 * Starts displaying the remaining Enderpearl cooldown on the hotbar.
	 *
	 * @param player the {@link Player} to display for
	 */
	public void startDisplaying(Player player) {
		PearlNameFaker pearlNameFaker;
		
		if (getTimeLeft(player.getUniqueId()) > 0L && itemNameFakes.putIfAbsent(player.getUniqueId(), pearlNameFaker = new PearlNameFaker(this, player)) == null) {
			long ticks = ((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion() >= 47 ? 20L : 2L;
			pearlNameFaker.runTaskTimerAsynchronously(OnimaAPI.getInstance(), ticks, ticks);
		}
	}

	/**
	 * Stop displaying the remaining Enderpearl cooldown on the hotbar.
	 *
	 * @param player the {@link Player} to stop for
	 */
	public void stopDisplaying(Player player) {
		PearlNameFaker pearlNameFaker = itemNameFakes.remove(player.getUniqueId());
		if (pearlNameFaker != null) {
			pearlNameFaker.cancel();
		}
	}
	
	/**
	 * Runnable to show remaining Enderpearl cooldown on held item.
	 */
	public static class PearlNameFaker extends BukkitRunnable {

		private final Cooldown cooldown;
		private final Player player;

		public PearlNameFaker(Cooldown cooldown, Player player) {
			this.cooldown = cooldown;
			this.player = player;
		}

		@Override
		public void run() {
			ItemStack stack = player.getItemInHand();
			
			if (stack != null && stack.getType() == Material.ENDER_PEARL) {
				long remaining = cooldown.getTimeLeft(player.getUniqueId());
				net.minecraft.server.v1_7_R4.ItemStack item = CraftItemStack.asNMSCopy(stack);
				
				if (remaining > 0L) {
					item = item.cloneItemStack();
					item.c(ConfigurationService.ENDERPEARL_COOLDOWN_ITEM_NAME + LongTime.setHMSFormat(remaining));
					setFakeItem(item, player.getInventory().getHeldItemSlot());
				} else
					cancel();
			}
		}

		@Override
		public synchronized void cancel() throws IllegalStateException {
			super.cancel();
			setFakeItem(CraftItemStack.asNMSCopy(player.getItemInHand()), player.getInventory().getHeldItemSlot()); // show the original item here.
		}

		/**
		 * Sends a fake SetSlot packet to a {@link Player}.
		 *
		 * @param nms   the {@link net.minecraft.server.v1_7_R4.ItemStack} to set at
		 * @param index the inventory index position to set at
		 */
		public void setFakeItem(net.minecraft.server.v1_7_R4.ItemStack nms, int index) {
			EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

			// Taken from CraftInventoryPlayer
			if (index < PlayerInventory.getHotbarSize()) index = index + 36;
			else if (index > 35) index = 8 - (index - 36);

			entityPlayer.playerConnection.sendPacket(new PacketPlayOutSetSlot(entityPlayer.activeContainer.windowId, index, nms));
		}
	}

}
