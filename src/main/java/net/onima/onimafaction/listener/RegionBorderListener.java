package net.onima.onimafaction.listener;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import net.onima.onimaapi.event.region.PlayerRegionChangeEvent;
import net.onima.onimaapi.fakeblock.FakeBlock;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.zone.type.Region;
import net.onima.onimafaction.players.FPlayer;

public class RegionBorderListener implements Listener {
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Location to = event.getTo();
		Location from = event.getFrom();
		
		int toX = to.getBlockX();
		int toY = to.getBlockY();
		int toZ = to.getBlockZ();

		if (from.getBlockX() != toX || from.getBlockY() != toY || from.getBlockZ() != toZ) {
			FPlayer fPlayer = FPlayer.getPlayer(event.getPlayer()); 
			FakeBlock.generate(fPlayer.getApiPlayer(), fPlayer.playerBarriers(to));
		}
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		onPlayerMove(event);
	}
	
	@EventHandler
	public void onMove(PlayerRegionChangeEvent event) {
		APIPlayer apiPlayer = event.getAPIPlayer();
		Region region = event.getNewRegion();
			
		if (region.getAccessRank().getValue() > apiPlayer.getRank().getRankType().getValue()) {
			apiPlayer.sendMessage("§cVous avez besoin du rank " + region.getAccessRank().getName() + " §cpour pouvoir entrer dans cette région !");
			event.setCancelled(true);
		}
	}
	
}
