package net.onima.onimafaction.faction;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.WorldBorder;
import net.onima.onimafaction.cooldowns.FactionStuckCooldown;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.players.FPlayer;

public class StuckRequest {

	private static final Material[] BLACKLIST = {Material.LEAVES, Material.LEAVES_2, Material.FENCE_GATE, Material.WATER, Material.LAVA, Material.STATIONARY_LAVA, Material.STATIONARY_WATER};
	
	private PlayerFaction faction;
	private Location location;
	
	public StuckRequest(Location location, PlayerFaction faction) {
		this.location = location;
		this.faction = faction;
	}
	
	public Location find(int searchRadius) {
		int max = ((Number) WorldBorder.getBorders().get(location.getWorld().getName())).intValue();
		int originalX = Math.max(Math.min(location.getBlockX(), max), -max);
        int originalZ = Math.max(Math.min(location.getBlockZ(), max), -max);
        int minX = Math.max(originalX - searchRadius, -max) - originalX;
        int maxX = Math.min(originalX + searchRadius, max) - originalX;
        int minZ = Math.max(originalZ - searchRadius, -max) - originalZ;
        int maxZ = Math.min(originalZ + searchRadius, max) - originalZ;
        
        for (int x = 0; x < searchRadius; x++) {
            if (x > maxX || -x < minX)
                continue;
            
            for (int z = 0; z < searchRadius; z++) {
                if (z > maxZ || -z < minZ)
                    continue;

                Location atPos = location.clone().add(x, 0.0D, z);
                Faction factionAtPos = Claim.getClaimAt(atPos).getFaction();
                
                if (factionAtPos == null || (faction != null && faction.getUUID().equals(factionAtPos.getUUID())) || !factionAtPos.isNormal()) {
                    Location safe = getSafeLocation(location.getWorld(), atPos.getBlockX(), atPos.getBlockZ());

                    if (safe != null)
                        return safe.add(0.5, 0.5, 0.5);
                    
                }
                
                Location atNeg = location.clone().add(x, 0.0D, z);
                Faction factionAtNeg = Claim.getClaimAt(atNeg).getFaction();
                
                if (factionAtNeg == null || (faction != null && faction.getUUID().equals(factionAtPos.getUUID())) || !factionAtNeg.isNormal()) {
                    Location safe = getSafeLocation(location.getWorld(), atNeg.getBlockX(), atNeg.getBlockZ());

                    if (safe != null)
                        return safe.add(0.5, 0.5, 0.5);
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
	
    private static Location getSafeLocation(World world, int x, int z) {
        Block highest = world.getHighestBlockAt(x, z);
        Material type = highest.getType();
        
        if (Arrays.asList(BLACKLIST).contains(type))
            return null;
        
        while (!type.isSolid()) {
            if (highest.getY() <= 1 || Arrays.asList(BLACKLIST).contains(type))
                return null;

            highest = highest.getRelative(BlockFace.DOWN);
            type = highest.getType();
        }
        
        return highest.getRelative(BlockFace.UP).getLocation();
    }

}
