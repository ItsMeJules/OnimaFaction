package net.onima.onimafaction.listener.fixes;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import net.onima.onimaapi.zone.struct.Flag;
import net.onima.onimafaction.players.FPlayer;

/**
 * Listener that prevents players from being trapped in portals.
 */
public class PortalTrapFixListener implements Listener {

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if (event.getClickedBlock() == null || FPlayer.getByPlayer(event.getPlayer()).getRegionOn().hasFlag(Flag.NO_INTERACT))
			return;
		
		if (event.getClickedBlock().getType() == Material.PORTAL) {
			event.getClickedBlock().setType(Material.AIR);
			event.getPlayer().sendMessage("§eVous avez §cdésactivé §ece portail.");
		}
	}
	
}