package net.onima.onimafaction.cooldowns;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.common.base.Predicate;

import net.onima.onimaapi.cooldown.utils.Cooldown;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.utils.OEffect;
import net.onima.onimaapi.utils.time.Time;
import net.onima.onimaapi.utils.time.Time.LongTime;
import net.onima.onimafaction.players.FPlayer;
import net.onima.onimafaction.players.OfflineFPlayer;

public class FactionHomeCooldown extends Cooldown implements Listener {
	
	public static final Predicate<OfflineFPlayer> HAS_HOME;
	public static final OEffect TELEPORT_EFFECT;
	
	static {
		HAS_HOME = (offline) -> offline != null && offline.hasFaction() && offline.getFaction().hasHome();
		TELEPORT_EFFECT = new OEffect(Effect.ENDER_SIGNAL, 0);
	}
	
	public FactionHomeCooldown() {
		super("f_home", (byte) 2, 10 * Time.SECOND);
	}

	@Override
	public String scoreboardDisplay(long timeLeft) {
		return "§2F home §6: §c" + LongTime.setHMSFormat(timeLeft);
	}
	
	@Override
	public boolean action(OfflineAPIPlayer offline) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void onStart(OfflineAPIPlayer offline) {
		if (offline.isOnline())
			((APIPlayer) offline).sendMessage("§eTéléportation dans votre home dans " + LongTime.setHMSFormat(duration) + ", ne bougez pas ou ne prenez pas de dégâts pendant ce délai.");
	
		super.onStart(offline);
	}
	
	@Override
	public void onExpire(OfflineAPIPlayer offline) {
		FPlayer onlineF = null;
		
		if (offline.isOnline() && HAS_HOME.apply(onlineF = FPlayer.getPlayer(offline.getUUID()))) {
			APIPlayer apiPlayer = (APIPlayer) offline;
			Location location = onlineF.getFaction().getHome();
			
			location.getChunk();
			apiPlayer.toPlayer().teleport(location);
			apiPlayer.sendMessage("§aVous avez été téléport avec succès au home de votre faction.");
			TELEPORT_EFFECT.play(location, 2);
		}
		
		super.onExpire(offline);
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if (getTimeLeft(event.getPlayer().getUniqueId()) <= 0L)
			return;
		
		APIPlayer apiPlayer = APIPlayer.getPlayer(event.getPlayer());
		
		if (apiPlayer.hasMovedOneBlockTo(event.getTo())) {
			onCancel(apiPlayer);
			apiPlayer.sendMessage("§cVous avez bougé... Téléportation annulée.");
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		APIPlayer apiPlayer = APIPlayer.getPlayer(event.getPlayer());
		
		if (getTimeLeft(apiPlayer.getUUID()) <= 0L)
			return;
		
		onCancel(apiPlayer);
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();

		if (entity instanceof Player && getTimeLeft(entity.getUniqueId()) > 0L) {
			((Player) entity).sendMessage("§cVous avez reçu des dégâts... Téléportation annulée.");
			onCancel(APIPlayer.getPlayer(entity.getUniqueId()));
		}
	}
	
}
