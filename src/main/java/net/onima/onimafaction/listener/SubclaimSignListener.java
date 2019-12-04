package net.onima.onimafaction.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.InventoryHolder;

import net.onima.onimaapi.caching.UUIDCache;
import net.onima.onimaapi.signs.HCFSign;
import net.onima.onimaapi.signs.SubclaimSign;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.faction.struct.Role;
import net.onima.onimafaction.players.FPlayer;

public class SubclaimSignListener implements Listener { //TODO pour un role de la faction
	
	@EventHandler(ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event) {
		if (OnimaFaction.getInstance().getEOTW().isRunning()) return;
		
		Block block = event.getClickedBlock();
		
		if(block.getState() instanceof Chest) {
			Player player = event.getPlayer();
			FPlayer fPlayer = FPlayer.getPlayer(player);
			SubclaimSign subSign = SubclaimSign.fromLocation(block.getLocation());
			
			if (subSign != null && (!subSign.getOwners().contains(player.getUniqueId()) || fPlayer.getRole().isAtMost(Role.COLEADER))) {
				player.sendMessage("§cVous ne pouvez pas ouvrir ce coffre privé. Seulement les " + Role.COLEADER.getName() + " et ses propriétaires peuvent.");
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onSignChange(SignChangeEvent event) {
		if (OnimaFaction.getInstance().getEOTW().isRunning()) return;
		
		String[] lines = event.getLines();
		
		if (!lines[0].equalsIgnoreCase(ChatColor.stripColor(ConfigurationService.SUBCLAIM_SIGN_LINE)))
			return;
		
		Player player = event.getPlayer();
		Block block = event.getBlock();
		Block attached = HCFSign.getAttachedBlock(block);
		
		if (!(attached.getState() instanceof Chest))
			return;
		
		Chest chest = (Chest) attached.getState();
		FPlayer fPlayer = FPlayer.getPlayer(player);
		
		if(!fPlayer.hasFaction()) {
			player.sendMessage("§cVous avez besoin d'une faction pour créer un coffre privé.");
			return;
		}
		
		if (!Claim.getClaimAt(attached.getLocation()).getFaction().getName().equalsIgnoreCase(fPlayer.getFaction().getName())) {
			player.sendMessage("§cVous devez être dans vos claims pour créer un coffre privé.");
			return;	
		}
		
		if (SubclaimSign.fromLocation(block.getLocation()) != null) {
			player.sendMessage("§cCe coffre est déjà privé.");
			return;
		}
		
		List<String> ownersList = new ArrayList<>(3);

		for (int i = 1; i < lines.length; i++) {
			String line = lines[i];
			if (StringUtils.isNotBlank(line)) 
				ownersList.add(line);
		}
		
		if (ownersList.isEmpty()) {
			player.sendMessage("§cVous devez noter au moins un joueur sur le panneau.");
			return;
		}
		
		SubclaimSign sign = new SubclaimSign((Sign) block.getState(), chest);
		sign.setOwners(ownersList.stream().map(str -> {return UUIDCache.getUUID(str);}).filter(uuid -> uuid != null).collect(Collectors.toCollection(() -> new ArrayList<>(3))));
		
		InventoryHolder holder = chest.getInventory().getHolder();
		
		if (holder.getInventory() instanceof DoubleChestInventory) {
			DoubleChest doubleChest = ((DoubleChestInventory) holder.getInventory()).getHolder();
			
			sign.setLocation(((Chest) doubleChest.getLeftSide()).getLocation(), 0);
			sign.setLocation(((Chest) doubleChest.getRightSide()).getLocation(), 1);
		}
		
		event.setLine(0, ConfigurationService.SUBCLAIM_SIGN_LINE);
		event.setLine(1, "§c" + lines[1]);
		event.setLine(2, "§c" + lines[2]);
		event.setLine(3, "§c" + lines[3]);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		if (OnimaFaction.getInstance().getEOTW().isRunning()) return;
		
		Block block = event.getBlock();
		Player player = event.getPlayer();
		
		if (!block.getType().toString().contains("SIGN"))
			return;
		
		SubclaimSign sign = SubclaimSign.fromLocation(block.getLocation());
		FPlayer fPlayer = FPlayer.getPlayer(player);
		
		if (sign != null) {
			if (fPlayer.getRole().isAtMost(Role.COLEADER) && !sign.getOwners().contains(player.getUniqueId()) && !fPlayer.hasFactionBypass()) {
				player.sendMessage("§cVous ne pouvez pas casser de coffres privés ou de panneaux privés.");
				event.setCancelled(true);
			} else
				HCFSign.getHCFSigns().remove(sign);
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (OnimaFaction.getInstance().getEOTW().isRunning()) return;
		
		Block block = event.getBlock();
		
		if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
			InventoryHolder holder = ((Chest) block.getState()).getInventory().getHolder();
			
			if (holder.getInventory() instanceof DoubleChestInventory) {
				DoubleChest doubleChest = ((DoubleChestInventory) holder.getInventory()).getHolder();
				Location right = ((Chest) doubleChest.getRightSide()).getLocation();
				Location left = ((Chest) doubleChest.getLeftSide()).getLocation();
				
				for (SubclaimSign subSign : HCFSign.getHCFSigns().parallelStream().filter(sign -> sign instanceof SubclaimSign).map(sign -> {return (SubclaimSign) sign;}).collect(Collectors.toList())) {
					if (locEquals(right, subSign))
						subSign.setLocation(right, 1);
					else if (locEquals(left, subSign))
						subSign.setLocation(left, 2);
				}
			}
		} else if (block.getType() == Material.HOPPER && SubclaimSign.fromLocation(block.getRelative(BlockFace.UP).getLocation()) != null) {
			event.getPlayer().sendMessage("§cVous ne pouvez pas placer d'hopper en-dessous d'un coffre privé.");
			event.setCancelled(true);
		}
	}
	
	private boolean locEquals(Location location, SubclaimSign subSign) {
		return Methods.locationEquals(location, subSign.getLocations()[0]) || Methods.locationEquals(location, subSign.getLocations()[1]) || Methods.locationEquals(location, subSign.getSign().getLocation());
	}
	
}
