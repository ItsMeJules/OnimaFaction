package net.onima.onimafaction.cooldowns;

import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import net.onima.onimaapi.cooldown.utils.Cooldown;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.utils.time.Time;
import net.onima.onimaapi.utils.time.Time.LongTime;
import net.onima.onimaapi.zone.struct.Flag;
import net.onima.onimafaction.players.FPlayer;

public class LogoutCooldown extends Cooldown implements Listener {

	public LogoutCooldown() {
		super("logout", (byte) 6, 30 * Time.SECOND);
	}

	@Override
	public String scoreboardDisplay(long timeLeft) {
		return "§aDéconnexion §6: " + LongTime.setHMSFormat(timeLeft);
	}

	@Override
	public boolean action(OfflineAPIPlayer offline) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void onStart(OfflineAPIPlayer offline) {
		if (offline.isOnline()) {
			APIPlayer apiPlayer = (APIPlayer) offline;
			
			if (FPlayer.getByUuid(apiPlayer.getUUID()).getRegionOn().hasFlag(Flag.SAFE_DISCONNECT))
				onExpire(offline);
			else
				apiPlayer.sendMessage("§aDéconnexion en cours, veuillez ne pas bouger et ne pas prendre de dégâts...");
		}
		
		super.onStart(offline);
	}
	
	@Override
	public void onExpire(OfflineAPIPlayer offline) {
		if (offline.isOnline()) {
			APIPlayer apiPlayer = (APIPlayer) offline;
			
			apiPlayer.setSafeDisconnect(true);
			apiPlayer.toPlayer().kickPlayer("§aVous avez été déconnecté sain et sauf !");
		}
		
		super.onExpire(offline);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event) {
		APIPlayer apiPlayer = APIPlayer.getByPlayer(event.getPlayer());

		if (apiPlayer.hasMovedOneBlockTo(event.getTo()) && getTimeLeft(apiPlayer.getUUID()) > 0L) {
			apiPlayer.sendMessage("§cVous avez bougé, déconnexion annulée !");
			onCancel(apiPlayer);
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		onPlayerMove(event);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();

		if (getTimeLeft(event.getPlayer().getUniqueId()) > 0L)
			onCancel(OfflineAPIPlayer.getByUuid(uuid));
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();

		if (entity instanceof Player && getTimeLeft(entity.getUniqueId()) > 0L) {
			((Player) entity).sendMessage("§cVous avez reçu des dégâts, déconnextion annulée !");
			onCancel(APIPlayer.getByUuid(entity.getUniqueId()));
		}
	}
	
}
