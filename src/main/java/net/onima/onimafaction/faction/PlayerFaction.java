package net.onima.onimafaction.faction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import net.md_5.bungee.api.chat.BaseComponent;
import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.sql.api.query.QueryResult;
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
import net.onima.onimafaction.faction.struct.Chat;
import net.onima.onimafaction.faction.struct.DTRStatus;
import net.onima.onimafaction.faction.struct.Relation;
import net.onima.onimafaction.faction.struct.Role;
import net.onima.onimafaction.players.Deathban;
import net.onima.onimafaction.players.FPlayer;
import net.onima.onimafaction.players.OfflineFPlayer;

public class PlayerFaction extends Faction {
	
	private static Map<UUID, PlayerFaction> playersFaction;
	
	static {
		playersFaction = new HashMap<>();
	}

	private List<UUID> members;
	private List<String> invitedPlayers;
	private float DTR;
	private Location home;
	private double money;
	private long regenCooldown;
	private Map<String, Relation> requestedRelations, relations;
	private OfflinePlayer focused;
	private int dtrUpdateTime;
	private String announcement;
	
	{
		members = new ArrayList<>();
		invitedPlayers = new ArrayList<>();
		requestedRelations = new HashMap<>();
		regenCooldown = -1;
		relations = new HashMap<>();
		dtrUpdateTime = ConfigurationService.DTR_UPDATE_TIME;
	}

	public PlayerFaction(String name, OfflineFPlayer leader) {
		super(name);
		leader.setFaction(this);
		leader.setRole(Role.LEADER);
		members.add(leader.getOfflineApiPlayer().getUUID());
		DTR = getMaxDTR();
	}
	
	public PlayerFaction(String name) {
		this(name, null);
	}
	
	private String getMemberColor(OfflineFPlayer offlinePlayer, CommandSender sender) {
		Deathban deathBan = offlinePlayer.getDeathban();
		
		if (offlinePlayer.getOfflineApiPlayer().isOnline()) {
			if (sender instanceof Player && !((Player) sender).canSee(((FPlayer) offlinePlayer).getApiPlayer().toPlayer()))
				return "§7";
			else
				return "§a";
		} else if (deathBan != null && deathBan.isActive())
			return "§c";
		else
			return "§7";
	}
	
	@Override
	public void sendShow(CommandSender sender) {
		OfflineFPlayer leader = getLeader();
		Player player = sender instanceof Player ? ((Player) sender) : null;
		List<String> coLeaders = new ArrayList<>();
		List<String> officers = new ArrayList<>();
		List<String> members = new ArrayList<>();
		
		for (OfflineFPlayer coLeader : getColeaders())
			coLeaders.add(getMemberColor(coLeader, sender) + coLeader.getOfflineApiPlayer().getName() + "§e[§a" + coLeader.getOfflineApiPlayer().getKills() + "§e]");

		for (OfflineFPlayer officer : getOfficers())
			officers.add(getMemberColor(officer, sender) + officer.getOfflineApiPlayer().getName() + "§e[§a" + officer.getOfflineApiPlayer().getKills() + "§e]");
		
		for (OfflineFPlayer member : this.members.parallelStream().map(OfflineFPlayer::getByUuid).filter(member -> member.getRole() == Role.MEMBER).collect(Collectors.toCollection(() -> new ArrayList<>(this.members.size() - (coLeaders.size() + officers.size())))))
			members.add(getMemberColor(member, sender) + member.getOfflineApiPlayer().getName() + "§e[§a" + member.getOfflineApiPlayer().getKills() + "§e]");
		
		sender.sendMessage(ConfigurationService.STAIGHT_LINE);
		sender.sendMessage(getDisplayName(sender) + " §7[" + getOnlineMembers(player).size() + '/' + getMembers().size() + " Connecté] §7- §eHome : §7" + (home == null ? "Aucun" : home.getBlockX() + " | " + home.getBlockZ()) + (open ? " §a[Ouverte]" : " §8[Fermée]"));
		if (permanent) sender.sendMessage(" §aCette faction est permanente.");
		sender.sendMessage(" §eCréée le : " + Methods.toFormatDate(created, ConfigurationService.DATE_FORMAT_HOURS));
		sender.sendMessage(" §eLeader : " + getMemberColor(leader, sender) + leader.getOfflineApiPlayer().getName() + "§e[§a" + leader.getOfflineApiPlayer().getKills() + "§e]");
		if (!coLeaders.isEmpty()) sender.sendMessage(" §eCo-Leader" + (coLeaders.size() > 1 ? 's' : "") + " : " + StringUtils.join(coLeaders, "§7,"));
		if (!officers.isEmpty()) sender.sendMessage(" §eOfficier" + (officers.size() > 1 ? 's' : "") + " : " + StringUtils.join(officers, "§7,"));
		if (!members.isEmpty()) sender.sendMessage(" §eMembre" + (members.size() > 1 ? 's' : "") + " : " + StringUtils.join(members, "§7,"));
		sender.sendMessage(" §eBalance : §7" + Methods.round("0.0", money)+ ' ' + ConfigurationService.MONEY_SYMBOL + "§e | §6Kills : §a" + getTotalFactionKills());
		sender.sendMessage(" §eDTR : " + getDTRColour() + Methods.round("0.00", DTR) + getDTRStatut().getSymbol() + "§7/" + getMaxDTR());
		if (regenCooldown > 0) sender.sendMessage("§eDTR Freeze : §c" + LongTime.setYMDWHMSFormat(regenCooldown));
		if (player != null) {
			PlayerFaction faction = FPlayer.getByPlayer(player).getFaction();
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
	
	public List<UUID> getMembers() {
		return members;
	}

	public void setMembers(List<UUID> members) {
		this.members = members;
	}
	
	public boolean addMember(OfflineFPlayer offlinePlayer, Role role) {
		if (offlinePlayer == null) return false;
		
		
		if (members.add(offlinePlayer.getOfflineApiPlayer().getUUID())) {
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
		
		return members.remove(offlinePlayer.getOfflineApiPlayer().getUUID());
	}

	public OfflineFPlayer getMember(String name) {
		for (UUID uuid : members) {
			OfflineFPlayer offline = OfflineFPlayer.getByUuid(uuid);
			
			if (offline.getOfflineApiPlayer().getName().equalsIgnoreCase(name))
				return offline;
		}
		return null;
	}
	
	public Collection<FPlayer> getOnlineMembers(Player player) {
		Set<FPlayer> onlineMembers = new HashSet<>();
		
		for (UUID uuid : members) {
			OfflineFPlayer offline = OfflineFPlayer.getByUuid(uuid);
			
			if (offline.getOfflineApiPlayer().isOnline()) {
				FPlayer fPlayer = (FPlayer) offline;
				if (player != null && !player.canSee(fPlayer.getApiPlayer().toPlayer()))
					continue;
				else
					onlineMembers.add(fPlayer);
			}
		}
		return onlineMembers;
	}
	
	public Set<OfflineFPlayer> getOfficers() {
		return members.parallelStream().map(OfflineFPlayer::getByUuid).filter(offlinePlayer -> offlinePlayer.getRole() == Role.OFFICER).collect(Collectors.toSet());
	}
	
	public OfflineFPlayer getLeader() {
		for (UUID uuid : members) {
			if (OfflineFPlayer.getByUuid(uuid).getRole() == Role.LEADER)
				return OfflineFPlayer.getByUuid(uuid);
		}
		return null;
	}
	
	public Set<OfflineFPlayer> getColeaders() {
		return members.parallelStream().map(OfflineFPlayer::getByUuid).filter(offlinePlayer -> offlinePlayer.getRole() == Role.COLEADER).collect(Collectors.toSet());
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

	public void setDTR(float DTR, DTRChangeCause cause) {
		DTR = Math.min(DTR, getMaxDTR());
		
		if (Math.abs(DTR - this.DTR) != 0) {
			FactionDTRChangeEvent event = new FactionDTRChangeEvent(this, cause, this.DTR, DTR);
			Bukkit.getPluginManager().callEvent(event);
			
			if (event.isCancelled()) return;
			
			DTR = event.getNewDTR();
			
			if (ConfigurationService.MIN_DTR > DTR) this.DTR = ConfigurationService.MIN_DTR;
			else if (ConfigurationService.MAX_DTR < DTR) this.DTR = ConfigurationService.MAX_DTR;
			else this.DTR = Float.valueOf(Methods.round("0.00", DTR));
		}
	}
	
	public float getMaxDTR() {
		if (members.size() == 1)
			return ConfigurationService.SOLO_DTR;
		
		return (float) Math.min(ConfigurationService.MAX_DTR, Double.valueOf(Methods.round("0.00", members.size() * ConfigurationService.PLAYER_DTR)));
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
		return regenCooldown == -1 ? -1 : regenCooldown-System.currentTimeMillis();
	}

	public void setRegenCooldown(long regenCooldown) {
		this.regenCooldown = regenCooldown + System.currentTimeMillis();
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
		Relation relation = relations.get(name);
		
		if (name.equalsIgnoreCase(this.name)) return Relation.MEMBER;
		
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
		
		for (UUID uuid : members)
			kills += OfflineAPIPlayer.getByUuid(uuid).getKills();

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
		
		super.remove();
		
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
		
		Iterator<UUID> iterator = members.iterator();
		
		while (iterator.hasNext()) {
			OfflineFPlayer offlinePlayer = OfflineFPlayer.getByUuid(iterator.next());
			FactionPlayerLeaveEvent leaveEvent = new FactionPlayerLeaveEvent(offlinePlayer, this, LeaveReason.DISBAND, player);
			Bukkit.getPluginManager().callEvent(leaveEvent);
			
			if (leaveEvent.isCancelled()) continue;
			
			offlinePlayer.setFaction(null);
			offlinePlayer.setRole(Role.NO_ROLE);
			offlinePlayer.setChat(Chat.GLOBAL);
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
	
	@Override
	public void queryDatabase(QueryResult<String> queryResult) {
	}
	
	@Override
	public void updateDatabase() {
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

	public static Map<UUID, PlayerFaction> getPlayersFaction() {
		return playersFaction;
	}

}
