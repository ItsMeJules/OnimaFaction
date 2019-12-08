package net.onima.onimafaction.players;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import net.onima.onimaapi.mongo.saver.MongoSerializer;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.rank.RankType;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.time.Time;
import net.onima.onimaapi.utils.time.Time.LongTime;
import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.EggAdvantageType;

public class Deathban implements MongoSerializer {

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
	private boolean eotwBanned;
	
	public Deathban(UUID player, UUID killer, Location location, double deathbanMultiplier, String deathMessage) {
		this.player = player;
		this.killer = killer;
		this.location = location;
		this.deathMessage = deathMessage;

		if (OnimaFaction.getInstance().getEOTW().isRunning() && OfflineAPIPlayer.getOfflineAPIPlayers().get(player).getRank().getRankType().getValue() < 10)
			eotwBanned = true;
		else
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
	
	public void setDeathTime(long death) {
		this.death = death;
	}

	public long getExpireTime() {
		return expire;
	}
	
	public void setExpireTime(long expire) {
		this.expire = expire;
	}

	public String getDeathMessage() {
		return deathMessage;
	}
	
	public void setDeathMessage(String deathMessage) {
		this.deathMessage = deathMessage;
	}
	
	public boolean isEotwDeathban() {
		return eotwBanned;
	}
	
	public void setEotwBan(boolean eotwBanned) {
		this.eotwBanned = eotwBanned;
	}
	
	public boolean isActive() {
		return expire > System.currentTimeMillis();
	}
	
	public void ban() {
		if (expire <= System.currentTimeMillis() && !eotwBanned)
			return;
		
		OfflineAPIPlayer offline = OfflineAPIPlayer.getOfflineAPIPlayers().get(player);
		
		if (offline != null && offline.isOnline()) {
			Bukkit.getScheduler().runTaskLater(OnimaFaction.getInstance(), () -> {
				if (!offline.isOnline())
					return;
				
				((APIPlayer) offline).toPlayer().kickPlayer(eotwBanned ? ConfigurationService.EOTW_DEATHBANNED : ConfigurationService.DEATHBAN_KICK_MESSAGE.replace("%death-message%", deathMessage).replace("%death-time%", Methods.toFormatDate(death, ConfigurationService.DATE_FORMAT_HOURS)).replace("%time-left%", LongTime.setYMDWHMSFormat(expire - System.currentTimeMillis())));
			}, 100L);
		}
			
	}
	
	@Override
	public Document getDocument(Object... objects) {
		return new Document("killer_uuid", killer == null ? null : killer.toString())
				.append("location", Methods.serializeLocation(location, false)).append("death_time", death)
				.append("expire_time", expire).append("death_message", deathMessage)
				.append("eotw_ban", eotwBanned);
	}
	
	public static long getBanTime(UUID uuid) {
		OfflineFPlayer offline = OfflineFPlayer.getOfflineFPlayers().get(uuid);
		
		if (offline == null)
			return -1;
		
		long banTime = Deathban.banTime.get(offline.getOfflineApiPlayer().getRank().getRankType());
		
		if (offline.getFaction() != null && offline.getFaction().getEggAdvantage(EggAdvantageType.DEATHBAN).getAmount() > 0)
			banTime = banTime * (2 / 3);
		
		return banTime;
	}
	
	public static Deathban getFor(UUID uuid) {
		Deathban deathban = null;
		
		if (OfflineFPlayer.getOfflineFPlayers().containsKey(uuid))
			deathban = OfflineFPlayer.getOfflineFPlayers().get(uuid).getDeathban();
		else
			deathban = PlayerFaction.getNotRegisteredPlayersDeathban().get(uuid);
		
		return deathban;
	}

}
