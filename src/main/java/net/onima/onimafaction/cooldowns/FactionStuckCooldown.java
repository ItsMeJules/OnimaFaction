package net.onima.onimafaction.cooldowns;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.google.common.base.Predicate;

import net.onima.onimaapi.cooldown.utils.Cooldown;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.time.Time;
import net.onima.onimaapi.utils.time.Time.LongTime;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.StuckRequest;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.players.FPlayer;

@SuppressWarnings("unused")
public class FactionStuckCooldown extends Cooldown implements Listener {
	
	public static final Predicate<FPlayer> CAN_F_STUCK;

	static {
		CAN_F_STUCK = (fPlayer) -> {
			Faction factionAt = Claim.getClaimAt(fPlayer.getApiPlayer().toPlayer().getLocation()).getFaction();
			
			if (!(factionAt.isWilderness() || factionAt.isRoad() || factionAt.isSafeZone())) {
				if (!fPlayer.hasFaction())
					return true;
				else return !fPlayer.getFaction().getName().equalsIgnoreCase(factionAt.getName());
			}
			
			return false;
		};
	}
	
	public FactionStuckCooldown() {
		super("f_stuck", (byte) 3, Time.MINUTE);
	}

	@Override
	public String scoreboardDisplay(long timeLeft) {
		return "§dF stuck §6: §c" + LongTime.setHMSFormat(timeLeft);
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
			FPlayer fPlayer = FPlayer.getByUuid(offline.getUUID());
			
			if (!isApplicable(fPlayer, CAN_F_STUCK)) {
				apiPlayer.sendMessage("§cVous pouvez seulement f stuck dans un territoire ennemie ou en WarZone !");
				fPlayer.setStuckRequest(null);
				return;
			}
			
			apiPlayer.sendMessage("§d§oVotre §7demande de stuck a commencé. Ne sortez pas du rayon de §d§o" + ConfigurationService.STUCK_RADIUS + " §7block" + (ConfigurationService.STUCK_RADIUS > 1 ? "s" : "") + '.');
			fPlayer.setStuckRequest(new StuckRequest(apiPlayer.toPlayer().getLocation()));
		}
		
		super.onStart(offline);
	}
	
	@Override
	public void onExpire(OfflineAPIPlayer offline) {
		if (offline.isOnline()) {
			APIPlayer apiPlayer = (APIPlayer) offline;
			FPlayer fPlayer = FPlayer.getByUuid(offline.getUUID());
			StuckRequest request = fPlayer.getStuckRequest();
			
			if (request != null && !request.isNotInRadius(apiPlayer.toPlayer().getLocation())) {
				apiPlayer.toPlayer().teleport(request.find(ConfigurationService.STUCK_SEARCH_RADIUS));
				apiPlayer.sendMessage("§aVous avez été téléporté à la location sauf la plus proche !");
				fPlayer.setStuckRequest(null);
			} else {
				onCancel(apiPlayer);
				apiPlayer.sendMessage("§cVous n'êtes plus dans le rayon de " + ConfigurationService.STUCK_RADIUS + " block" + (ConfigurationService.STUCK_RADIUS > 1 ? "s" : "") + " téléportation annulée !");
				return;
			}
		}
		
		super.onExpire(offline);
	}
	
	@Override
	public void onCancel(OfflineAPIPlayer offline) {
		if (offline.isOnline())
			FPlayer.getByUuid(offline.getUUID()).setStuckRequest(null);
		
		super.onCancel(offline);
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		if (checkFailed(player.getUniqueId(), event.getTo())) {
			player.sendMessage("§cVous n'êtes plus dans le rayon de " + ConfigurationService.STUCK_RADIUS + " block" + (ConfigurationService.STUCK_RADIUS > 1 ? "s" : "") + " téléportation annulée !");
			onCancel(APIPlayer.getByPlayer(player));
		}
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		onMove(event);
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();

		if (entity instanceof Player && getTimeLeft(entity.getUniqueId()) > 0L) {
			((Player) entity).sendMessage("§cVous avez reçu des dégâts... Stuck annulée.");
			onCancel(APIPlayer.getByUuid(entity.getUniqueId()));
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		APIPlayer apiPlayer = APIPlayer.getByPlayer(event.getPlayer());
		
		if (getTimeLeft(apiPlayer.getUUID()) <= 0L)
			return;
		
		onCancel(apiPlayer);
	}
	
	private boolean checkFailed(UUID uuid, Location to) {
		if (getTimeLeft(uuid) <= 0L)
			return false;

		StuckRequest request = FPlayer.getByUuid(uuid).getStuckRequest();
		
		return request != null && request.isNotInRadius(to);
	}
	
}
