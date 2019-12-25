package net.onima.onimafaction.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import net.onima.onimaapi.cooldown.utils.Cooldown;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.time.Time.LongTime;
import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.cooldowns.PvPTimerCooldown;
import net.onima.onimafaction.players.Deathban;
import net.onima.onimafaction.players.FPlayer;
import net.onima.onimafaction.players.OfflineFPlayer;

public class DeathbanListener implements Listener {
	
	 @EventHandler(priority = EventPriority.NORMAL)
	 public void onDeathbanJoin(AsyncPlayerPreLoginEvent event) {
		 OfflineFPlayer offline = OfflineFPlayer.getOfflineFPlayers().get(event.getUniqueId());
		 
		 if (offline == null) {
			 event.disallow(Result.KICK_OTHER, ConfigurationService.SQL_ERROR_LOADING_DATA);
			 return;
		 }
		 
		 Deathban deathban = offline.getDeathban();
		 
		 if (deathban != null) {
			 if (deathban.isEotwDeathban()) {
				 event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ConfigurationService.EOTW_DEATHBANNED);
				 return;
			 }
			 
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
					 event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, ConfigurationService.DEATHBAN_KICK_MESSAGE.replace("%death-message%", deathban.getDeathMessage()).replace("%death-time%", Methods.toFormatDate(deathban.getDeathTime(), ConfigurationService.DATE_FORMAT_HOURS)).replace("%time-left%", LongTime.setYMDWHMSFormat(deathban.getExpireTime() - System.currentTimeMillis())));
			 } else
				 offline.setDeathban(null);
		 }
	 }
	 
	 @EventHandler
	 public void onRespawn(PlayerRespawnEvent event) {
		 FPlayer fPlayer = FPlayer.getPlayer(event.getPlayer());
		 Deathban deathban = fPlayer.getDeathban();		 
		 
		 if (deathban != null && deathban.isEotwDeathban())
			 fPlayer.getApiPlayer().toPlayer().kickPlayer(ConfigurationService.EOTW_DEATHBANNED);
	 }
	 
	 @EventHandler(priority = EventPriority.MONITOR) 
	 public void onJoin(PlayerJoinEvent event) {
		 if (OnimaFaction.getInstance().getEOTW().isRunning()) {
			 Cooldown cooldown = Cooldown.getCooldown(PvPTimerCooldown.class);
			 Player player = event.getPlayer();
			 
			 if (cooldown.getTimeLeft(player.getUniqueId()) > 0L) 
				 cooldown.onCancel(APIPlayer.getPlayer(player));
		 }
	 }

}
