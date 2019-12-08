package net.onima.onimafaction.players;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import net.onima.onimaapi.caching.UUIDCache;
import net.onima.onimaapi.event.mongo.AbstractPlayerLoadEvent;
import net.onima.onimaapi.mongo.api.result.MongoQueryResult;
import net.onima.onimaapi.mongo.saver.NoSQLSaver;
import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.callbacks.VoidCallback;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.Chat;
import net.onima.onimafaction.faction.struct.Role;

public class OfflineFPlayer implements NoSQLSaver {
	
	protected static Map<UUID, OfflineFPlayer> offlineFPlayers;
	
	static {
		offlineFPlayers = new HashMap<>();
	}
	
	protected OfflineAPIPlayer offlineApiPlayer;
	protected boolean fMap, factionBypass, askingLifeUse;
	protected Role role;
	protected Chat chat;
	protected List<String> factionSpying;
	protected Deathban deathban;
	protected PlayerFaction playerFaction;
	protected int lives;
	
	{
		role = Role.NO_ROLE;
		chat = Chat.GLOBAL;
		factionSpying = new ArrayList<>();
	}
	
	public OfflineFPlayer(OfflineAPIPlayer offlineApiPlayer) {
		this.offlineApiPlayer = offlineApiPlayer;
		
		if (this instanceof FPlayer)
			transferInstance(offlineApiPlayer.getUUID());
			
		offlineFPlayers.put(offlineApiPlayer.getUUID(), this);
	}
	
	private void transferInstance(UUID uuid) {
		getPlayer(uuid, old -> {
			if (old == null)
				return;
			
			fMap = old.fMap;
			factionBypass = old.factionBypass;
			role = old.role;
			chat = old.chat;
			factionSpying = old.factionSpying;
			deathban = old.deathban;
			playerFaction = old.playerFaction;
			
		});
	}
	
	public OfflineAPIPlayer getOfflineApiPlayer() {
		return offlineApiPlayer;
	}

	public boolean hasfMap() {
		return fMap;
	}

	public void setfMap(boolean fMap) {
		this.fMap = fMap;
	}

	public boolean hasFactionBypass() {
		return factionBypass;
	}

	public void setFactionBypass(boolean factionBypass) {
		this.factionBypass = factionBypass;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public Chat getChat() {
		return chat;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	public List<String> getFactionSpying() {
		return factionSpying;
	}

	public void setFactionSpying(List<String> factionSpying) {
		this.factionSpying = factionSpying;
	}

	public Deathban getDeathban() {
		return deathban;
	}

	public void setDeathban(Deathban deathban) {
		this.deathban = deathban;
		
		if (PlayerFaction.getNotRegisteredPlayersDeathban().containsKey(offlineApiPlayer.getUUID()))
			PlayerFaction.getNotRegisteredPlayersDeathban().remove(offlineApiPlayer.getUUID());
			
		if (deathban != null)
			deathban.ban();
	}
	
	public boolean hasFaction() {
		return playerFaction != null;
	}

	public PlayerFaction getFaction() {
		return playerFaction;
	}
	
	public void setFaction(PlayerFaction playerFaction) {
		this.playerFaction = playerFaction;
	}
	
	public int getLives() {
		return lives;
	}
	
	public void setLives(int lives) {
		this.lives = lives;
	}

	public boolean isAskingToUseALife() {
		return askingLifeUse;
	}
	
	public void setAskingToUseALife(boolean askingLifeUse) {
		this.askingLifeUse = askingLifeUse;
	}
	
	public static void getPlayer(UUID uuid, VoidCallback<OfflineFPlayer> callback) {
		if (!offlineFPlayers.containsKey(uuid)) {
			AbstractPlayerLoadEvent event = new AbstractPlayerLoadEvent(uuid) {
				
				@Override
				public void done() {
					callback.call(offlineFPlayers.get(uuid));
				}
			};
			
			Bukkit.getPluginManager().callEvent(event);
		} else
			callback.call(offlineFPlayers.get(uuid));
	}
	
	public static void getPlayer(String name, VoidCallback<OfflineFPlayer> callback) {
		OfflinePlayer offline = Bukkit.getOfflinePlayer(UUIDCache.getUUID(name));

		if (offline.hasPlayedBefore())
			getPlayer(offline.getUniqueId(), callback);
	}
	
	public static void getPlayer(OfflinePlayer offline, VoidCallback<OfflineFPlayer> callback) {
		getPlayer(offline.getUniqueId(), callback);
	}
	
	
	public static Map<UUID, OfflineFPlayer> getOfflineFPlayers() {
		return offlineFPlayers;
	}
	
	public static Collection<OfflineFPlayer> getDisconnectedOfflineFPlayers() {
		return offlineFPlayers.values();
	}

	@Override
	public void save() {
		offlineFPlayers.put(offlineApiPlayer.getUUID(), this);
	}

	@Override
	public void remove() {
		offlineFPlayers.remove(offlineApiPlayer.getUUID());		
	}

	@Override
	public boolean isSaved() {
		return offlineFPlayers.containsKey(offlineApiPlayer.getUUID());
	}

	@Override
	public void queryDatabase(MongoQueryResult result) {
		Document document = result.getValue("player", Document.class);
		Document deathbanDoc = result.getValue("deathban", Document.class);
		
		fMap = document.getBoolean("f_map");
		factionBypass = document.getBoolean("f_bypass");
		role = Role.valueOf(document.getString("role"));
		chat = Chat.valueOf(document.getString("chat"));
		lives = document.getInteger("lives");
		
		String faction = document.getString("faction_name");
		
		if (faction != null)
			playerFaction = PlayerFaction.getPlayersFaction().get(faction);

		if (deathbanDoc != null) {
			String killerUUID = deathbanDoc.getString("killer_uuid");
			
			deathban = new Deathban(offlineApiPlayer.getUUID(),
					killerUUID == null ? null : UUID.fromString(killerUUID),
					Methods.deserializeLocation(deathbanDoc.getString("location"), false),
					0.0D,
					deathbanDoc.getString("death_message"));
			
			deathban.setExpireTime(deathbanDoc.getLong("expire_time"));
			deathban.setDeathTime(deathbanDoc.getLong("death_time"));
			deathban.setEotwBan(deathbanDoc.getBoolean("eotw_ban"));
		}
	}
	
	@Deprecated
	@Override
	public Document getDocument(Object... objects) {return null;}
	
	@Deprecated
	@Override
	public boolean shouldDelete() {return false;}

}
