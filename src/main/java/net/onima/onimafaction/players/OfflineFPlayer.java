package net.onima.onimafaction.players;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.sql.api.query.QueryResult;
import net.onima.onimaapi.sql.api.table.Row;
import net.onima.onimafaction.armorclass.ArmorClass;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.Chat;
import net.onima.onimafaction.faction.struct.Role;

public class OfflineFPlayer {
	
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
	protected ArmorClass[] armorClasses;
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
		OfflineFPlayer old = getByUuid(uuid);
		
		if (old == null) return;
		
		fMap = old.fMap;
		factionBypass = old.factionBypass;
		role = old.role;
		chat = old.chat;
		factionSpying = old.factionSpying;
		deathban = old.deathban;
		playerFaction = old.playerFaction;
	}
	
	public void load(QueryResult<UUID> result) {
		Row row = result.getResults();
		
		playerFaction = row.getValue("player_faction", PlayerFaction.class);
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

	public static OfflineFPlayer getByUuid(UUID uuid) {
		return offlineFPlayers.get(uuid);
	}
	
	@SuppressWarnings("deprecation")
	public static OfflineFPlayer getByName(String name) {
		OfflinePlayer offline = Bukkit.getOfflinePlayer(name);
		
		if (offline == null)
			return null;
		else
			return getByUuid(offline.getUniqueId());
	}
	
	public static OfflineFPlayer getByOfflinePlayer(OfflinePlayer offline) {
		return offlineFPlayers.get(offline.getUniqueId());
	}
	
	public static Map<UUID, OfflineFPlayer> getOfflineFPlayers() {
		return offlineFPlayers;
	}
	
	public static Collection<OfflineFPlayer> getDisconnectedOfflineFPlayers() {
		return offlineFPlayers.values();
	}

}
