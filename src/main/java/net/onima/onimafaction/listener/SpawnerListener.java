package net.onima.onimafaction.listener;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.onima.onimaapi.items.Crowbar;
import net.onima.onimaapi.items.Crowbar.CrowbarBlock;
import net.onima.onimaapi.utils.BetterItem;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.OSound;
import net.onima.onimaapi.zone.struct.Flag;
import net.onima.onimafaction.faction.claim.Claim;

public class SpawnerListener implements Listener { //TODO Ajouter un message quand un portail de l'end est d�truit dans un claim
	
	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.hasItem() && Crowbar.isCrowbar(event.getItem())) {
			
			ItemStack item = event.getItem();
			Player player = event.getPlayer();
			World world = player.getWorld();
			
			if (world.getEnvironment() != World.Environment.NORMAL) {
				player.sendMessage("§cVous pouvez seulement utiliser la crowbar dans l'overworld !");
				return;
			}

			Block block = event.getClickedBlock();
			
			if (EnvironementListener.tryToBuild(player, block.getLocation()) < 1 || RegionListener.tryToBuild(player, block.getLocation(), Flag.BREAK_BLOCK) == -1) {
				player.sendMessage("§cVous ne pouvez pas utiliser votre crowbar dans le territoire de " + Claim.getClaimAndRegionAt(block.getLocation()).getDisplayName(player));
				return;
			}
			
			if (block.getType() == Material.ENDER_PORTAL_FRAME) {
				int portals = Crowbar.getCount(item, CrowbarBlock.ENDER_PORTALS);
				
				if (portals > 0) {
					Location loc = block.getLocation();
					
					block.setType(Material.AIR);
					world.playEffect(loc, Effect.STEP_SOUND, Material.ENDER_PORTAL_FRAME.getId());
					
					for (int x = -4; x < 4; x++) {
						for (int z = -4; z < 4; z++) {
							Location loc2 = loc.clone();
							loc2.add(x, 0.0D, z);
							
							if (loc2.getBlock().getType() == Material.ENDER_PORTAL) {
								world.playEffect(loc2, Effect.STEP_SOUND, Material.ENDER_PORTAL.getId());
								loc2.getBlock().setType(Material.AIR);
							}
						}
					}
					
					world.dropItemNaturally(loc, new ItemStack(Material.ENDER_PORTAL_FRAME));
					portals--;
					if (portals <= 0) {
						new OSound(Sound.ANVIL_BREAK, 1.5F, 10F).play(player);
						player.setItemInHand(null);
					} else {
						short maxDurability = item.getType().getMaxDurability();
						item.setDurability((short) (maxDurability - maxDurability / Crowbar.PORTAL_COUNT * portals));
						Crowbar.updateCount(item, 0, portals);
					}
				} else {
					player.sendMessage("§cVous ne pouvez pas prendre de portail de l'end car vous avez déjà pris un spawner.");
					return;
				}
			} else if (block.getType() == Material.MOB_SPAWNER) {
				int spawners = Crowbar.getCount(item, CrowbarBlock.SPAWNERS);
				
				if (spawners > 0) {
					Location loc = block.getLocation();
					CreatureSpawner spawner = (CreatureSpawner) block.getState();
					String creatureName = spawner.getCreatureTypeName();
					
					block.setType(Material.AIR);
					world.playEffect(loc, Effect.STEP_SOUND, Material.MOB_SPAWNER.getId());
					world.dropItemNaturally(block.getLocation(), new BetterItem(Material.MOB_SPAWNER, 1, 0, ConfigurationService.SPAWNER_NAME.replace("%spawner%", creatureName), creatureName).toItemStack());
					spawners--;
				} else {
					player.sendMessage("§cVous ne pouvez pas prendre de spawners car vous avez déjà pris un portail de l'end.");
					return;
				}
				
				if (spawners <= 0) {
					new OSound(Sound.ANVIL_BREAK, 1.5F, 10F).play(player);
					player.setItemInHand(null);
				} else {
					short maxDurability = item.getType().getMaxDurability();
					item.setDurability((short) (maxDurability - maxDurability / Crowbar.SPAWNER_COUNT * spawners));
					Crowbar.updateCount(item, spawners, 0);
				}
			} else {
				player.sendMessage("§cLa crowbar peut-être seulement utilisé sur des spawners et des portails de l'end.");
				return;
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onSpawnerPlace(BlockPlaceEvent event) { //TODO Si les joueurs ont beaucoup d'erreurs faire un meta.getDisplayName().endsWith(�c�f�d).
		Block block = event.getBlock();
		ItemStack item = event.getItemInHand();
		
		if (block.getType() != Material.MOB_SPAWNER || !item.hasItemMeta()) return;
		
		ItemMeta meta = item.getItemMeta();
		
		if (meta.hasDisplayName() && meta.hasLore()) {
			List<String> lore = meta.getLore();
			CreatureSpawner spawner = (CreatureSpawner) block.getState();
			
			if (!lore.isEmpty()) {
				String spawnerType = ChatColor.stripColor(lore.get(0).toUpperCase());
				EntityType entityType = EntityType.fromName(spawnerType);
				
				if (entityType != null) {
					spawner.setSpawnedType(entityType);
					spawner.update(true);
				} else
					event.getPlayer().sendMessage("Une erreur a été détectée avec votre spawner, la créature " + spawnerType + " n'existe pas ! Vous devriez contacter un membre du staff.");
			}
		}
	}

}
