package net.onima.onimafaction.players;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.rank.RankType;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.time.Time;
import net.onima.onimaapi.utils.time.Time.LongTime;
import net.onima.onimafaction.OnimaFaction;

public class Deathban {

	private static Map<RankType, Long> banTime;
	
	static {
		banTime = new HashMap<>();
		
		banTime.put(RankType.OWNER, 0L);
		banTime.put(RankType.HEAD_ADMIN, 0L);
		banTime.put(RankType.ADMIN, 0L);
		banTime.put(RankType.CHEF_DEVELOPPEUR, 0L);
		banTime.put(RankType.DEVELOPPEUR, 0L);
		banTime.put(RankType.BUILDER, 0L);
		banTime.put(RankType.MOD, 0L);
		banTime.put(RankType.TRIAL_MOD, 0L);
		banTime.put(RankType.FAMOUS, 5 * Time.MINUTE);
		banTime.put(RankType.YOUTUBE, 10 * Time.MINUTE);
		banTime.put(RankType.FRIEND, 0L);
		banTime.put(RankType.SHOGUN, 5 * Time.MINUTE);
		banTime.put(RankType.KACHI, 15 * Time.MINUTE);
		banTime.put(RankType.KOMONO, 25 * Time.MINUTE);
		banTime.put(RankType.RONIN, 35 * Time.MINUTE);
		banTime.put(RankType.NINJA, 45 * Time.MINUTE);
		banTime.put(RankType.DEFAULT, Time.HOUR);
		banTime.put(RankType.BOT, 2 * Time.HOUR);
	}
	
	private UUID player, killer;
	private Location location;
	private long death, expire;
	private String deathMessage;
	
	public Deathban(UUID player, UUID killer, Location location, double deathbanMultiplier, String deathMessage) {
		this.player = player;
		this.killer = killer;
		this.location = location;
		this.deathMessage = deathMessage;
		
		expire = (long) (System.currentTimeMillis() + getBanTime(player) * deathbanMultiplier);
		death = System.currentTimeMillis();
	}

	public UUID getPlayer() {
		return player;
	}

	public UUID getKiller() {
		return killer;
	}

	public Location getLocation() {
		return location;
	}
	
	public long getDeathTime() {
		return death;
	}

	public long getExpireTime() {
		return expire;
	}

	public String getDeathMessage() {
		return deathMessage;
	}
	
	public void setDeathMessage(String deathMessage) {
		this.deathMessage = deathMessage;
	}
	
	public boolean isActive() {
		return expire > System.currentTimeMillis();
	}
	
	public void ban() {
		if (expire <= System.currentTimeMillis())
			return;
		
		OfflineAPIPlayer offline = OfflineAPIPlayer.getByUuid(player);
		
		if (offline.isOnline()) {
			Bukkit.getScheduler().runTaskLater(OnimaFaction.getInstance(), () -> {
				if (!offline.isOnline())
					return;
				
				((APIPlayer) offline).toPlayer().kickPlayer(ConfigurationService.DEATHBAN_KICK_MESSAGE.replace("%death-message%", deathMessage).replace("%death-time%", Methods.toFormatDate(death, ConfigurationService.DATE_FORMAT_HOURS)).replace("%time-left%", LongTime.setYMDWHMSFormat(expire - System.currentTimeMillis())));
			}, 100L);
		}
			
	}
	
	public static long getBanTime(UUID uuid) {
		OfflineAPIPlayer offline = OfflineAPIPlayer.getByUuid(uuid);
		
		if (offline == null)
			return -1;
		
		return banTime.get(offline.getRank().getRankType());
	}

}
