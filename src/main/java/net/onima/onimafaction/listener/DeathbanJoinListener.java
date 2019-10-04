package net.onima.onimafaction.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.time.Time.LongTime;
import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.players.Deathban;
import net.onima.onimafaction.players.OfflineFPlayer;

public class DeathbanJoinListener implements Listener {
	
	 @EventHandler
	 public void onDeathbanJoin(AsyncPlayerPreLoginEvent event) {
		 OfflineFPlayer offline = OfflineFPlayer.getByUuid(event.getUniqueId());
		 
		 if (offline == null)
			 return;
		 
		 Deathban deathban = offline.getDeathban();
		 
		 if (deathban != null) {
			 if (deathban.isActive()) {
				 int lives = offline.getLives();
				 
				 if (lives > 0) {
					 if (offline.isAskingToUseALife()) {
						 
						 Bukkit.getScheduler().runTaskLater(OnimaFaction.getInstance(), () -> {
							 offline.setAskingToUseALife(false);
							 offline.setLives(lives - 1);
							 offline.setDeathban(null); 
							 
							 if (offline.getOfflineApiPlayer().isOnline())
								 ((APIPlayer) offline.getOfflineApiPlayer()).sendMessage("§7Tu as utilisé une vie, tu en as §e" + (lives - 1) + " §7restante" + (lives > 2 ? "s" : "") + '.');
						 }, 20L);
						 
					 } else {
						 event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ConfigurationService.DEATHBAN_KICK_CONNECT_MESSAGE.replace("%death-message%", deathban.getDeathMessage()).replace("%death-time%", Methods.toFormatDate(deathban.getDeathTime(), ConfigurationService.DATE_FORMAT_HOURS).replace("%time-left", LongTime.setYMDWHMSFormat(deathban.getExpireTime() - System.currentTimeMillis()))));
						 offline.setAskingToUseALife(true);
						 Bukkit.getScheduler().runTaskLater(OnimaFaction.getInstance(), () -> offline.setAskingToUseALife(false), 15 * 20L);
					 }
				 } else
					 event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ConfigurationService.DEATHBAN_KICK_MESSAGE.replace("%death-message%", deathban.getDeathMessage()).replace("%death-time%", Methods.toFormatDate(deathban.getDeathTime(), ConfigurationService.DATE_FORMAT_HOURS)).replace("%time-left%", LongTime.setYMDWHMSFormat(deathban.getExpireTime() - System.currentTimeMillis())));
			 } else
				 offline.setDeathban(null);
		 }
	 }

}
