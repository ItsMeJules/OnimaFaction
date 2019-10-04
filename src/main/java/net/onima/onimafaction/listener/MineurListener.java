package net.onima.onimafaction.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.armorclass.Mineur;
import net.onima.onimafaction.players.FPlayer;

public class MineurListener implements Listener {

	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		
		if (entity instanceof Player && Methods.hasGotLastDamageByPlayer((Player) entity)) {
			Player player = (Player) entity;
			Mineur mineur = (Mineur) FPlayer.getByPlayer(player).getArmorClass(Mineur.class);
			
			if (mineur.isActivated())
				mineur.removeInvisibility(player, true);
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		((Mineur) FPlayer.getByPlayer(event.getPlayer()).getArmorClass(Mineur.class)).handleInvisibility(event);
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		onMove(event);
	}
	
}
