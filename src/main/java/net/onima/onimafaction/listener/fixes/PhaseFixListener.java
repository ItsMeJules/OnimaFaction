package net.onima.onimafaction.listener.fixes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.listener.EnvironementListener;
import net.onima.onimafaction.listener.RegionListener;

public class PhaseFixListener implements Listener {

    @EventHandler
    public void onMove(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
    	Location location = player.getLocation();
    	
        if (location.getBlock() != null
        		&& location.getBlock().getType() == Material.TRAP_DOOR
        		&& EnvironementListener.tryToBuild(player, location) < 1
        		&& RegionListener.tryToBuild(player, location) < 1) {
        	player.teleport(player.getLocation().add(0.0D, 1.0D, 0.0D));
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerShovel(BlockBreakEvent event){
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Location location = player.getLocation();
        double yDiff;
        
        if (event.isCancelled() && block.getType().isSolid() && (yDiff = location.getY() - block.getY()) >= 0 && yDiff < 0.5) {
            int blockX = block.getX();
            int blockZ = block.getZ();
            
            if (blockX != location.getBlockX() || blockZ != location.getBlockZ()) {
            	
                Bukkit.getScheduler().runTask(OnimaFaction.getInstance(), () -> {
                	
                    new BukkitRunnable() {
                    	
                        public void run() {
                            if (player.isOnline() && blockX == player.getLocation().getBlockX() && blockZ == player.getLocation().getBlockZ())
                                player.teleport(location.clone());
                            else if (!player.isOnline() || player.getLocation().getBlockX() != location.getBlockX() || player.getLocation().getBlockZ() != location.getBlockZ() || player.getLocation().getBlockY() != location.getBlockY())
                                cancel();
                        }
                        
                    }.runTaskTimer(OnimaFaction.getInstance(), 1, 1);
                    
                    if (blockX == player.getLocation().getBlockX() && blockZ == player.getLocation().getBlockZ())
                        player.teleport(location.clone());
                });
            }
        }
    }
	
}
