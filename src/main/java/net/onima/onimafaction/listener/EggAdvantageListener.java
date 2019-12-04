package net.onima.onimafaction.listener;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import net.onima.onimafaction.events.EggAdvantageRemovedEvent;
import net.onima.onimafaction.events.FactionClaimChangeEvent;
import net.onima.onimafaction.events.FactionDTRChangeEvent;
import net.onima.onimafaction.faction.EggAdvantage;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.faction.struct.EggAdvantageType;
import net.onima.onimafaction.faction.struct.Role;
import net.onima.onimafaction.gui.EggAdvantageMenu;
import net.onima.onimafaction.players.FPlayer;

public class EggAdvantageListener implements Listener { //TODO EggAdvantageRemovedEvent et faire le 
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();
		
		if (block.getType() != Material.DRAGON_EGG)
			return;
		
		Player player = event.getPlayer();
		FPlayer fPlayer = FPlayer.getPlayer(player);
		
		if (!fPlayer.hasFaction() && !fPlayer.hasFactionBypass()) {
			player.sendMessage("§cVous devez avoir une faction pour poser cet oeuf !");
			event.setCancelled(true);
			return;
		}
		
		Location location = block.getLocation();
		
		if (!fPlayer.getFaction().getName().equalsIgnoreCase(Claim.getClaimAt(location).getFaction().getName()) && !fPlayer.hasFactionBypass()) {
			player.sendMessage("§cVous pouvez seulement poser cet oeuf dans vos claims !");
			event.setCancelled(true);
			return;
		}
		
		if (location.clone().add(0, -1, 0).getBlock().getType() != Material.EMERALD_BLOCK) {
			player.sendMessage("§cL'oeuf peut seulement être posé sur un block d'émeraude !");
			event.setCancelled(true);
			return;
		} else {
			fPlayer.getApiPlayer().openMenu(new EggAdvantageMenu(fPlayer, location));
		}
	}

	@EventHandler
	public void onClaimRemoved(FactionClaimChangeEvent event) {
		if (event.getClaimChangeCause() == FactionClaimChangeEvent.ClaimChangeCause.UNCLAIM && !event.getClaim().getEggAdvantages().isEmpty()) {
			Claim claim = event.getClaim();
			
			if (!(claim.getFaction() instanceof PlayerFaction))
				return;
			
			PlayerFaction faction = (PlayerFaction) claim.getFaction();
			Iterator<EggAdvantage> iterator = claim.getEggAdvantages().iterator();
			
			while (iterator.hasNext()) {
				EggAdvantage advantage = iterator.next();
				
				advantage.remove();
				iterator.remove();
				faction.broadcast("§6Vous avez perdu l'oeuf " + advantage.getType().getName() + "§6, allez le récupérer avant qu'il ne soit perdu !");
				Bukkit.getPluginManager().callEvent(new EggAdvantageRemovedEvent(advantage, claim));
			}
		}
	}
	
	@EventHandler
	public void onBlockFromTo(BlockFromToEvent event) {
		Block block = event.getBlock();
		
		if (block != null && block.getType() == Material.DRAGON_EGG) {
			Claim claim = Claim.getClaimAt(block.getLocation());
			
			if (!(claim.getFaction() instanceof PlayerFaction))
				return;
			
			if (!claim.getEggAdvantages().isEmpty()) {
				PlayerFaction faction = (PlayerFaction) claim.getFaction();
				Iterator<EggAdvantage> iterator = claim.getEggAdvantages().iterator();
				
				while (iterator.hasNext()) {
					EggAdvantage advantage = iterator.next();
					
					if (advantage.getLocations().containsValue(block.getLocation())) {
						advantage.remove(block.getLocation().add(0, -1, 0));
						iterator.remove();
						faction.broadcast("§6Un oeuf a été téléporté, vous avez perdu les pouvoirs " + advantage.getType().getName() + "§6, allez le récupérer avant qu'il ne soit perdu !");
						Bukkit.getPluginManager().callEvent(new EggAdvantageRemovedEvent(advantage, claim));
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onGravity(BlockPhysicsEvent event) {
		Block block = event.getBlock();
		
		if (block != null && block.getType() == Material.DRAGON_EGG) {
			Claim claim = Claim.getClaimAt(block.getLocation());
			
			if (!(claim.getFaction() instanceof PlayerFaction))
				return;
			
			if (!claim.getEggAdvantages().isEmpty()) {
				PlayerFaction faction = (PlayerFaction) claim.getFaction();
				Iterator<EggAdvantage> iterator = claim.getEggAdvantages().iterator();
				
				while (iterator.hasNext()) {
					EggAdvantage advantage = iterator.next();
					
					if (advantage.getLocations().containsValue(block.getLocation()) && block.getLocation().add(0, -1, 0).getBlock().getType() != Material.EMERALD_BLOCK) {
						advantage.remove(block.getLocation().add(0, -1, 0));
						iterator.remove();
						faction.broadcast("§6Un oeuf a été déplacé de son socle, vous avez perdu les pouvoirs " + advantage.getType().getName() + "§6, allez le récupérer avant qu'il ne soit perdu !");
						Bukkit.getPluginManager().callEvent(new EggAdvantageRemovedEvent(advantage, claim));
					}
				}
			}
				
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		
		if (block != null && block.getType() == Material.DRAGON_EGG) {
			Claim claim = Claim.getClaimAt(block.getLocation());
			
			if (!(claim.getFaction() instanceof PlayerFaction))
				return;
			
			if (!claim.getEggAdvantages().isEmpty()) {
				PlayerFaction faction = (PlayerFaction) claim.getFaction();
				Iterator<EggAdvantage> iterator = claim.getEggAdvantages().iterator();
				
				while (iterator.hasNext()) {
					EggAdvantage advantage = iterator.next();
					
					if (advantage.getLocations().containsValue(block.getLocation())) {
						advantage.remove(block.getLocation().add(0, -1, 0));
						iterator.remove();
						faction.broadcast("§6Un oeuf a été cassé, vous avez perdu les pouvoirs " + advantage.getType().getName() + "§6, allez le récupérer avant qu'il ne soit perdu !");
						Bukkit.getPluginManager().callEvent(new EggAdvantageRemovedEvent(advantage, claim));
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		Block clicked = event.getClickedBlock();
		
		if (action.toString().contains("BLOCK") && clicked != null && clicked.getType() == Material.DRAGON_EGG) {
			Faction fac = Claim.getClaimAt(clicked.getLocation()).getFaction();
			
			if (!(fac instanceof PlayerFaction))
				return;

			Player player = event.getPlayer();
			FPlayer fPlayer = FPlayer.getPlayer(player);
			
			if (!fPlayer.getRole().isAtLeast(Role.COLEADER)) {
				player.sendMessage("§cVous devez être au moins " + Role.COLEADER.getName() + " pour pouvoir intéragir avec cet oeuf.");
				event.setCancelled(true);
			}
			
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onStickyPistonExtend(BlockPistonExtendEvent event) {
		Block targetBlock = event.getBlock().getRelative(event.getDirection(), event.getLength() + 1);
		
		checkPiston(event, targetBlock);
		
		for (Block block : event.getBlocks())
			checkPiston(event, block);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockGrowth(BlockGrowEvent event) {
		Block block = event.getBlock();
		Claim claim = Claim.getClaimAt(block.getLocation());
		
		if (!(claim.getFaction() instanceof PlayerFaction))
			return;
		
		EggAdvantage egg = claim.getEggAdvantage(EggAdvantageType.CROPS);
		
		if (egg != null) {
			byte nextData = (byte) (block.getData() + (egg.getAmount() - 1 + egg.getType().getChanger()));
			
			if (nextData > 9)
				nextData = 9;
			
			block.setData((byte) nextData);
		}
	}
	
	@EventHandler
	public void onEggRemove(EggAdvantageRemovedEvent event) {
		EggAdvantage egg = event.getEggAdvantage();
		
		if (egg.getType() == EggAdvantageType.F_DTR) {
			PlayerFaction faction = (PlayerFaction) event.getClaim().getFaction();
			
			if (faction.getDTR() > faction.getMaxDTR())
				faction.setDTR(faction.getMaxDTR(), FactionDTRChangeEvent.DTRChangeCause.PLUGIN);
		}
	}
	
	private void checkPiston(Cancellable event, Block block) {
		Claim claim = Claim.getClaimAt(block.getLocation());
		
		if (!(claim.getFaction() instanceof PlayerFaction))
			return;
		
		if (!claim.getEggAdvantages().isEmpty()) {
			Iterator<EggAdvantage> iterator = claim.getEggAdvantages().iterator();
			
			while (iterator.hasNext()) {
				if (iterator.next().getLocations().containsKey(block.getLocation()))
					event.setCancelled(true);
			}
		}
	}
	
}
