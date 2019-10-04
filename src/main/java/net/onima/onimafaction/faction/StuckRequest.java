package net.onima.onimafaction.faction;

import org.bukkit.Location;

import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimafaction.cooldowns.FactionStuckCooldown;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.faction.claim.WildernessClaim;
import net.onima.onimafaction.players.FPlayer;

public class StuckRequest {
	
	private Location location;
	
	public StuckRequest(Location location) {
		this.location = location;
	}
	
	public Location find(int searchRadius) {
		for (int x = location.getBlockX() - searchRadius; x < location.getBlockX() + searchRadius; x++) {
			for (int z = location.getBlockZ() - searchRadius; z < location.getBlockZ() + searchRadius; z++) {
				Location next = location.clone().add(x, 0, z);
				
				if (Claim.getClaimAt(next) instanceof WildernessClaim) {
					next.setY(next.getWorld().getHighestBlockYAt(next));
					return next;
				}
			}
		}
		return null;
	}
	
	public boolean isNotInRadius(Location to) {
		return (Math.abs(location.getBlockX() - to.getBlockX()) > ConfigurationService.STUCK_RADIUS || Math.abs(location.getBlockY() - to.getBlockY()) > ConfigurationService.STUCK_RADIUS || Math.abs(location.getBlockZ() - to.getBlockZ()) > ConfigurationService.STUCK_RADIUS);
	}

	public void cancel(FPlayer fPlayer) {
		fPlayer.getApiPlayer().removeCooldown(FactionStuckCooldown.class);
		fPlayer.setStuckRequest(null);
	}

}
