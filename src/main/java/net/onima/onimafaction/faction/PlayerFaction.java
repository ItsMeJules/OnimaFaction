package net.onima.onimafaction.faction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import net.md_5.bungee.api.chat.BaseComponent;
import net.onima.onimaapi.caching.UUIDCache;
import net.onima.onimaapi.mongo.OnimaMongo;
import net.onima.onimaapi.mongo.OnimaMongo.OnimaCollection;
import net.onima.onimaapi.mongo.api.result.MongoQueryResult;
import net.onima.onimaapi.rank.RankType;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.time.Time.LongTime;
import net.onima.onimafaction.armorclass.ArmorClass;
import net.onima.onimafaction.events.FactionDTRChangeEvent;
import net.onima.onimafaction.events.FactionDTRChangeEvent.DTRChangeCause;
import net.onima.onimafaction.events.FactionDisbandEvent;
import net.onima.onimafaction.events.FactionPlayerLeaveEvent;
import net.onima.onimafaction.events.FactionPlayerLeaveEvent.LeaveReason;
import net.onima.onimafaction.events.FactionPlayerLeftEvent;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.faction.struct.Chat;
import net.onima.onimafaction.faction.struct.DTRStatus;
import net.onima.onimafaction.faction.struct.EggAdvantageType;
import net.onima.onimafaction.faction.struct.Relation;
import net.onima.onimafaction.faction.struct.Role;
import net.onima.onimafaction.players.Deathban;
import net.onima.onimafaction.players.FPlayer;
import net.onima.onimafaction.players.OfflineFPlayer;
import net.onima.onimafaction.task.RegenerationEntryTask;

public class PlayerFaction extends Faction {
	
	private static Map<String, PlayerFaction> playersFaction;
	private static Map<UUID, Deathban> notRegisteredPlayersDeathbans;
	
	static {
		playersFaction = new HashMap<>();
		notRegisteredPlayersDeathbans = new HashMap<>();
	}

	private Map<UUID, Role> members;
	private List<String> invitedPlayers;
	private float DTR;
	private Location home;
	private double money;
	private long regenCooldown;
	private Map<String, Relation> requestedRelations, relations;
	private OfflinePlayer focused;
	private int dtrUpdateTime;
	private String announcement;
	private List<EggAdvantage> eggAdvantages;
	
	{
		members = new HashMap<>();
		invitedPlayers = new ArrayList<>();
		requestedRelations = new HashMap<>();
		regenCooldown = -1;
		relations = new HashMap<>();
		dtrUpdateTime = ConfigurationService.DTR_UPDATE_TIME;
		eggAdvantages = new ArrayList<>(EggAdvantageType.values().length);
		
		for (EggAdvantageType eggAdvantageType : EggAdvantageType.values())
			eggAdvantages.add(new EggAdvantage(eggAdvantageType, this));
	}

	public PlayerFaction(String name, OfflineFPlayer leader) {
		super(name);
		
		if (leader != null) {
			leader.setFaction(this);
			leader.setRole(Role.LEADER);
			members.put(leader.getOfflineApiPlayer().getUUID(), leader.getRole());
		}
		
		DTR = getMaxDTR();
		playersFaction.put(name, this);
	}
	
	public PlayerFaction(String name) {
		this(name, null);
	}
	
	private String getMemberColor(OfflinePlayer offlinePlayer, CommandSender sender, Deathban deathban) {
		if (offlinePlayer.isOnline()) {
			if (sender instanceof Player && !((Player) sender).canSee((Player) offlinePlayer))
				return "§7";
			else
				return "§a";
		} else if (deathban != null && deathban.isActive())
			return "§c";
		else
			return "§7";
	}
	
	@Override
	public void sendShow(CommandSender sender) {
		OfflinePlayer leader = getLeader();
		Player player = sender instanceof Player ? ((Player) sender) : null;
		List<String> coLeaders = new ArrayList<>();
		List<String> officers = new ArrayList<>();
		List<String> members = new ArrayList<>();
		EggAdvantage fDtrEgg = getEggAdvantage(EggAdvantageType.F_DTR);
		
		for (OfflinePlayer coLeader : getColeaders())
			coLeaders.add(getMemberColor(coLeader, sender, Deathban.getFor(coLeader.getUniqueId())) + Methods.getRealName(coLeader) + "§e[§a" + Methods.getKills(coLeader.getUniqueId()) + "§e]");
		
		for (OfflinePlayer officer : getOfficers())
			officers.add(getMemberColor(officer, sender, Deathban.getFor(officer.getUniqueId())) + Methods.getRealName(officer) + "§e[§a" + Methods.getKills(officer.getUniqueId()) + "§e]");
			
		this.members.forEach((uuid, role) -> {
			if (role == Role.MEMBER) {
				OfflinePlayer member = Bukkit.getOfflinePlayer(uuid);
				
				members.add(getMemberColor(member, sender, Deathban.getFor(uuid)) + Methods.getRealName(member) + "§e[§a" + Methods.getKills(uuid) + "§e]");	
			}
		});
		
		sender.sendMessage(ConfigurationService.STAIGHT_LINE);
		sender.sendMessage(getDisplayName(sender) + " §7[" + getOnlineMembers(player).size() + '/' + getMembers().size() + " Connecté] §7- §eHome : §7" + (home == null ? "Aucun" : home.getBlockX() + " | " + home.getBlockZ()) + (open ? " §a[Ouverte]" : " §8[Fermée]"));
		if (permanent) sender.sendMessage(" §aCette faction est permanente.");
		sender.sendMessage(" §eCréée le : " + Methods.toFormatDate(created, ConfigurationService.DATE_FORMAT_HOURS));
		if (leader != null) sender.sendMessage(" §eLeader : " + getMemberColor(leader, sender, Deathban.getFor(leader.getUniqueId())) + Methods.getRealName(leader) + "§e[§a" + Methods.getKills(leader.getUniqueId()) + "§e]");
		if (!coLeaders.isEmpty()) sender.sendMessage(" §eCo-Leader" + (coLeaders.size() > 1 ? 's' : "") + " : " + StringUtils.join(coLeaders, "§7, "));
		if (!officers.isEmpty()) sender.sendMessage(" §eOfficier" + (officers.size() > 1 ? 's' : "") + " : " + StringUtils.join(officers, "§7, "));
		if (!members.isEmpty()) sender.sendMessage(" §eMembre" + (members.size() > 1 ? 's' : "") + " : " + StringUtils.join(members, "§7, "));
		sender.sendMessage(" §eBalance : §7" + Methods.round("0.0", money)+ ' ' + ConfigurationService.MONEY_SYMBOL + "§e | §6Kills : §a" + getTotalFactionKills());
		sender.sendMessage(" §eDTR : " + getDTRColour() + Methods.round("0.00", DTR) + getDTRStatut().getSymbol() + "§7/" + getMaxDTR() + (fDtrEgg.getAmount() > 0 ? " §7[§c+" + fDtrEgg.getAmount() * fDtrEgg.getType().getChanger() + "§7]" : ""));
		
		long actualRegen = regenCooldown - System.currentTimeMillis();
		
		if (actualRegen > 0)
			sender.sendMessage("§eDTR Freeze : §c" + LongTime.setYMDWHMSFormat(actualRegen));
		else
			regenCooldown = -1;
		
		if (player != null) {
			PlayerFaction faction = FPlayer.getPlayer(player).getFaction();
			if (faction != null && faction.equals(this) && announcement != null) 
				sender.sendMessage(" §eAnnonce : §d" + announcement);
		}
		sender.sendMessage(ConfigurationService.STAIGHT_LINE);
	}
	
	public JSONMessage jsonHoverTooltip(Player viewer, int index) {
		return new JSONMessage(
				(index + 1) + ". §d§o" + name + " §7(§a" + getOnlineMembers(viewer).size() + "§7/§a" + getMembers().size() + "§7) " + getDTRColour() + Methods.round("0.00", DTR) + getDTRStatut().getSymbol() + " §7DTR",
				"§eHome : §7" + (home == null ? "Aucun" : home.getBlockX() + " | " + home.getBlockZ()) + "\n§eKills : " + getTotalFactionKills(),
				true, "/f show " + name
				);
	}
	
	public JSONMessage jsonHoverTooltip(int online, int index) {
		return new JSONMessage(
				(index + 1) + ". §d§o" + name + " §7(§a" + online + "§7/§a" + getMembers().size() + "§7) " + getDTRColour() + Methods.round("0.00", DTR) + getDTRStatut().getSymbol() + " §7DTR",
				"§eHome : §7" + (home == null ? "Aucun" : home.getBlockX() + " | " + home.getBlockZ()) + "\n§eKills : " + getTotalFactionKills(),
				true, "/f show " + name
				);
	}
	
	public Map<UUID, Role> getMembers() {
		return members;
	}
	
	public Set<UUID> getMembersUUID() {
		return members.keySet();
	}

	public void setMembers(Map<UUID, Role> members) {
		this.members = members;
	}
	
	public boolean addMember(OfflineFPlayer offlinePlayer, Role role) {
		if (offlinePlayer == null)
			return false;
		
		if (members.put(offlinePlayer.getOfflineApiPlayer().getUUID(), role) == null) {
			offlinePlayer.setFaction(this);
			offlinePlayer.setRole(role == null ? Role.MEMBER : role);
			
			return true;
		}
		
		return false;
	}
	
	public boolean addMember(OfflineFPlayer offlinePlayer) {
		return addMember(offlinePlayer, Role.MEMBER);
	}
	
	public boolean removeMember(OfflineFPlayer offlinePlayer, LeaveReason reason, CommandSender kicker) {
		FactionPlayerLeaveEvent event = new FactionPlayerLeaveEvent(offlinePlayer, this, reason, kicker);
		Bukkit.getPluginManager().callEvent(event);
		
		if (event.isCancelled()) return false;
		
		offlinePlayer.setFaction(null);
		offlinePlayer.setRole(Role.NO_ROLE);
		
		boolean removed = members.remove(offlinePlayer.getOfflineApiPlayer().getUUID()) != null;
		
		Bukkit.getPluginManager().callEvent(new FactionPlayerLeftEvent(offlinePlayer, this, reason, kicker));
		return removed;
	}

	public OfflinePlayer getMember(String name) {
		UUID uuid = UUIDCache.getUUID(name);
		
		if (members.containsKey(uuid))
			return Bukkit.getOfflinePlayer(uuid);
		else
			return null;
	}
	
	public Collection<FPlayer> getOnlineMembers(Player player) {
		Set<FPlayer> onlineMembers = new HashSet<>();
		
		for (UUID uuid : members.keySet()) {
			OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
			
			if (offline.isOnline()) {
				if (player != null && !player.canSee((Player) offline))
					continue;
				else
					onlineMembers.add(FPlayer.getPlayer(offline.getUniqueId()));
			}
		}
		return onlineMembers;
	}
	
	public Set<OfflinePlayer> getOfficers() {
		Set<OfflinePlayer> officers = new HashSet<>(5);
		
		members.forEach((uuid, role) -> {
			if (role == Role.OFFICER)
				officers.add(Bukkit.getOfflinePlayer(uuid));
		});
		
		return officers;
	}
	
	public OfflinePlayer getLeader() {
		for (Entry<UUID, Role> entry : members.entrySet()) {
			if (entry.getValue() == Role.LEADER)
				return Bukkit.getOfflinePlayer(entry.getKey());
		}
		return null;
	}
	
	public Set<OfflinePlayer> getColeaders() {
		Set<OfflinePlayer> coLeaders = new HashSet<>(3);
		
		members.forEach((uuid, role) -> {
			if (role == Role.COLEADER)
				coLeaders.add(Bukkit.getOfflinePlayer(uuid));
		});
		
		return coLeaders;
	}

	public List<String> getInvitedPlayers() {
		return invitedPlayers;
	}

	public void setInvitedPlayers(List<String> invitedPlayers) {
		this.invitedPlayers = invitedPlayers;
	}
	
	public float getDTR() {
		return DTR;
	}

	public boolean setDTR(float DTR, DTRChangeCause cause) {
		DTR = Math.min(DTR, getMaxDTR());
		
		if (Math.abs(DTR - this.DTR) != 0) {
			FactionDTRChangeEvent event = new FactionDTRChangeEvent(this, cause, this.DTR, DTR);
			Bukkit.getPluginManager().callEvent(event);
			
			if (event.isCancelled()) return false;
			
			DTR = event.getNewDTR();
			
			if (ConfigurationService.MIN_DTR > DTR) this.DTR = ConfigurationService.MIN_DTR;
			else if (ConfigurationService.MAX_DTR < DTR) this.DTR = ConfigurationService.MAX_DTR;
			else this.DTR = Float.valueOf(Methods.round("0.00", DTR));
		}
		
		return true;
	}
	
	public float getMaxDTR() {
		float maxDTR = 0;
		
		if (members.size() == 1)
			maxDTR = ConfigurationService.SOLO_DTR;
		else
			maxDTR = (float) Math.min(ConfigurationService.MAX_DTR, Double.valueOf(Methods.round("0.00", members.size() * ConfigurationService.PLAYER_DTR)));
		
		EggAdvantage egg = getEggAdvantage(EggAdvantageType.F_DTR);
		
		if (egg.getAmount() > 0)
			maxDTR += egg.getAmount() * egg.getType().getChanger();
		
		return maxDTR;
	}

	public DTRStatus getDTRStatut() {
		if (getRegenCooldown() >= 0)
			return DTRStatus.FROZEN;
		else if (DTR < getMaxDTR())
			return DTRStatus.REGENERATING;
		else
			return DTRStatus.FULL;
	}
	
	public String getDTRColour() {
		if (DTR < 0)
			return "§c";
		else if (DTR < 1)
			return "§e";
		else
			return "§a";
	}
	

	public boolean hasHome() {
		return home != null;
	}
	
	public Location getHome() {
		return home;
	}

	public void setHome(Location home) {
		this.home = home;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}
	
	public void addMoney(double money) {
		this.money += money;
	}
	
	public void removeMoney(double money) {
		this.money -= money;
	}

	public long getRegenCooldown() {
		return regenCooldown == -1 ? -1 : regenCooldown - System.currentTimeMillis();
	}

	public void setRegenCooldown(long regenCooldown) {
		this.regenCooldown = regenCooldown == 0 ? -1 : regenCooldown + System.currentTimeMillis();
	}

	public Map<String, Relation> getRequestedRelations() {
		return requestedRelations;
	}

	public void setRequestedRelations(Map<String, Relation> requestedRelations) {
		this.requestedRelations = requestedRelations;
	}
	
	public void addRelationRequest(Faction faction, Relation relation) {
		requestedRelations.put(faction.getName(), relation);
	}
	
	public void removeRequestedRelation(Faction faction) {
		requestedRelations.remove(faction.getName());
	}
	
	public void addRelation(Faction faction, Relation relation) {
		relations.put(faction.getName(), relation);
	}
	
	public void removeRelation(Faction faction) {
		relations.remove(faction.getName());
	}
	
	public Relation getRelation(String name) {
		if (name.equalsIgnoreCase(this.name))
			return Relation.MEMBER;
		
		Relation relation = relations.get(name);
		
		return relation == null ? Relation.ENEMY : relation;	
	}
	
	public Relation getRelation(Faction faction) {
		return faction == null ? Relation.ENEMY : getRelation(faction.getName());
	}
	
	public Collection<String> getAllies() {
		return Maps.filterValues(relations, (Relation relation) -> relation == Relation.ALLY).keySet();
	}

	public Map<String, Relation> getRelations() {
		return relations;
	}

	public void setRelations(Map<String, Relation> relations) {
		this.relations = relations;
	}

	public OfflinePlayer getFocused() {
		return focused;
	}

	public void setFocused(OfflinePlayer focused) {
		this.focused = focused;
	}
	
	public void broadcast(String message) {
		for (FPlayer member : getOnlineMembers(null))
			member.getApiPlayer().sendMessage(message);
	}
	
	public void broadcast(JSONMessage jsonMessage) {
		BaseComponent[] message = jsonMessage.build();
		
		for (FPlayer member : getOnlineMembers(null))
			member.getApiPlayer().sendMessage(message);
	}
	
	public int getTotalFactionKills() {
		int kills = 0;
		
		for (UUID uuid : members.keySet())
			kills += Methods.getKills(uuid);
		
		return kills;
	}
	
	public int getDtrUpdateTime() {
		return dtrUpdateTime;
	}

	public void setDtrUpdateTime(int updateTime) {
		this.dtrUpdateTime = updateTime;
	}
	
	public boolean isRaidable() {
		return DTR <= 0;
	}
	
	public void disband(Player player) {
		FactionDisbandEvent event = new FactionDisbandEvent(this, player);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) return;
		
		remove();
		
		RegenerationEntryTask.get().safeRemove(this);
		
		for (String allyName : getAllies()) {
			PlayerFaction ally = (PlayerFaction) Faction.getFaction(allyName);
			
			ally.removeRelation(this);
			removeRelation(ally);
		}
		
		getOnlineMembers(null).forEach(member -> {
			if (member.hasfMap()) {
				member.setfMap(false, false);
				member.setfMap(true, false);
			}
		});
		
		Iterator<UUID> iterator = members.keySet().iterator();
		
		while (iterator.hasNext()) {
			OfflineFPlayer.getPlayer(iterator.next(), offlinePlayer -> {
				FactionPlayerLeaveEvent leaveEvent = new FactionPlayerLeaveEvent(offlinePlayer, this, LeaveReason.DISBAND, player);
				Bukkit.getPluginManager().callEvent(leaveEvent);
				
				if (!leaveEvent.isCancelled()) {
					offlinePlayer.setFaction(null);
					offlinePlayer.setRole(Role.NO_ROLE);
					offlinePlayer.setChat(Chat.GLOBAL);
					
					Bukkit.getPluginManager().callEvent(new FactionPlayerLeftEvent(offlinePlayer, this, LeaveReason.DISBAND, player));
				}
			});
		}
	}
	
	public String getAnnouncement() {
		return announcement;
	}
	
	public void setAnnouncement(String announcement) {
		this.announcement = announcement;
	}
	
	public int getCountOfActivated(Class<? extends ArmorClass> armorClass) {
		int count = 0;
		
		for (FPlayer fPlayer : getOnlineMembers(null)) {
			if (fPlayer.hasArmorClass(armorClass))
				count++;
		}
		
		return count;
	}
	
	public List<EggAdvantage> getAllEggAdvantages() {
		return eggAdvantages;
	}
	
	public EggAdvantage getEggAdvantage(EggAdvantageType type) {
		for (EggAdvantage egg : eggAdvantages) {
			if (egg.getType() == type)
				return egg;
		}
		
		return null;
	}
	
	@Override
	public void save() {
		super.save();
		
		PlayerFaction.getPlayersFaction().put(name, this);
	}
	
	@Override
	public void remove() {
		super.remove();
		
		PlayerFaction.getPlayersFaction().remove(name);
	}
	
	@Override
	public void queryDatabase(MongoQueryResult result) {
		Document document = result.getValue("faction", Document.class);
		
		//Faction part
		uuid = UUID.fromString(document.getString("uuid"));
		name = document.getString("name");
		created = ((Number) document.get("created")).longValue(); //MongoDB stores it as an Integer whenever it can.
		permanent = document.getBoolean("permanent");
		
		setFlags(document.getString("flags"));
		
		for (Document doc : document.getList("claims", Document.class)) {
			Claim claim = new Claim(this, Methods.deserializeLocation(doc.getString("location_1"), false), Methods.deserializeLocation(doc.getString("location_2"), false));
			
			claim.setPrice(doc.getDouble("price"));
			claim.setName(doc.getString("name"));
			claim.setDeathban(doc.getBoolean("deathban"));
			claim.setDTRLoss(doc.getBoolean("dtr_loss"));
			claim.setPriority(doc.getInteger("priority"));
			claim.setDeathbanMultiplier(doc.getDouble("deathban_multiplier"));
			claim.setAccessRank(doc.getString("access_rank") == null ? null : RankType.fromString(doc.getString("access_rank")));
			
			for (Document eggDoc : doc.getList("eggs", Document.class)) {
				EggAdvantage eggAdvantage = new EggAdvantage(EggAdvantageType.valueOf(eggDoc.getString("type")), this);
				
				for (String locs : eggDoc.getList("locations", String.class)) {
					String[] str = locs.split("@");
					
					eggAdvantage.getLocations().put(Methods.deserializeLocation(str[0], false), Methods.deserializeLocation(str[1], false));
				}
				
				claim.getEggAdvantages().add(eggAdvantage);
			}
		}
		
		//PlayerFaction part
		for (Document doc : document.getList("members", Document.class))
			members.put(UUID.fromString(doc.getString("uuid")), Role.fromString(doc.getString("role")));
		
		invitedPlayers = document.getList("invited_players", String.class);
		DTR = ((Number) document.get("dtr")).floatValue(); //I have to because mongo doesn't know about floats.
		home = document.getString("home").isEmpty() ? null : Methods.deserializeLocation(document.getString("home"), false);
		money = document.getDouble("money");
		regenCooldown = ((Number) document.get("regen_cooldown")).longValue();
		
		for (Document doc : document.getList("requested_relations", Document.class))
			requestedRelations.put(doc.getString("faction_name"), Relation.valueOf(doc.getString("relation")));
		
		for (Document doc : document.getList("relations", Document.class))
			relations.put(doc.getString("faction_name"), Relation.valueOf(doc.getString("relation")));
		
		dtrUpdateTime = document.getInteger("dtr_update_time");
		announcement = document.getString("announcement");
		
		if (getDTRStatut() != DTRStatus.FULL)
			RegenerationEntryTask.get().insert(this);
	}
	
	@Override
	public Document getDocument(Object... objects) {
		List<Document> membersDoc = new ArrayList<>();
		List<Document> requestedRelationsDoc = new ArrayList<>();
		List<Document> relationsDoc = new ArrayList<>();
		
		for (Entry<UUID, Role> entry : members.entrySet())
			membersDoc.add(new Document("uuid", entry.getKey().toString()).append("role", entry.getValue().name()));
		
		for (Entry<String, Relation> entry : requestedRelations.entrySet())
			requestedRelationsDoc.add(new Document("faction_name", entry.getKey()).append("relation", entry.getValue().name()));
		
		for (Entry<String, Relation> entry : relations.entrySet())
			requestedRelationsDoc.add(new Document("faction_name", entry.getKey()).append("relation", entry.getValue().name()));
		
		return super.getDocument().append("members", membersDoc)
				.append("invited_players", invitedPlayers).append("dtr", DTR)
				.append("home", Methods.serializeLocation(home, false))
				.append("money", money).append("regen_cooldown", regenCooldown)
				.append("requested_relations", requestedRelationsDoc)
				.append("relations", relationsDoc).append("focused", focused == null ? null : focused.getUniqueId().toString())
				.append("dtr_update_time", dtrUpdateTime).append("announcement", announcement);
	}
	
	@Override
	public boolean shouldDelete() {
		return super.shouldDelete() && !playersFaction.containsKey(name);
	}
	
	public static Map<PlayerFaction, Integer> getByMostPlayersOnline() {
		Map<PlayerFaction, Integer> factions = new HashMap<>();
		
		for (Faction faction : Faction.getFactions()) {
			if (faction instanceof PlayerFaction) {
				PlayerFaction pFac = (PlayerFaction) faction;
				
				factions.put(pFac, pFac.getOnlineMembers(null).size());
			}
		}
		
		return Methods.sortMapByValue(factions);
	}

	public static Map<String, PlayerFaction> getPlayersFaction() {
		return playersFaction;
	}
	
	public static Map<UUID, Deathban> getNotRegisteredPlayersDeathban() {
		return notRegisteredPlayersDeathbans;
	}
	
	public static void loadDeathbans() {
		Bson proj = Projections.include("uuid", "deathban.killer_uuid", "deathban.location", "deathban.message", "deathban.expire_time", "deathban.death_time");
		Iterator<Document> iterator = OnimaMongo.get(OnimaCollection.PLAYERS)
				.find(Filters.and(
						Filters.ne("deathban", null),
						Filters.gt("deathban.expire_time", System.currentTimeMillis()))).projection(proj).iterator();
		
		while (iterator.hasNext()) {
			Document document = iterator.next();
			Document deathbanDocument = document.get("deathban", Document.class);
			
			String killerUUID = deathbanDocument.getString("killer_uuid");
			
			Deathban deathban = new Deathban(UUID.fromString(document.getString("uuid")),
					killerUUID == null ? null : UUID.fromString(killerUUID),
					Methods.deserializeLocation(deathbanDocument.getString("location"), false),
					0L,
					deathbanDocument.getString("message"));
			
			deathban.setExpireTime(deathbanDocument.getLong("expire_time"));
			deathban.setDeathTime(deathbanDocument.getLong("death_time"));
			
			notRegisteredPlayersDeathbans.put(deathban.getPlayer(), deathban);
		}
	}

}
