package net.onima.onimafaction.faction;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Sets;

import net.onima.onimaapi.saver.SQLSaver;
import net.onima.onimaapi.sql.api.query.QueryResult;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.zone.struct.Flag;
import net.onima.onimaapi.zone.type.utils.FlagZone;
import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.events.FactionClaimChangeEvent;
import net.onima.onimafaction.events.FactionClaimChangeEvent.ClaimChangeCause;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.faction.struct.Relation;
import net.onima.onimafaction.faction.type.RoadFaction;
import net.onima.onimafaction.faction.type.SafeZoneFaction;
import net.onima.onimafaction.faction.type.WarZoneFaction;
import net.onima.onimafaction.faction.type.WildernessFaction;
import net.onima.onimafaction.players.FPlayer;

public class Faction implements FlagZone, SQLSaver<String> {

	public static boolean lock;
	protected static List<Faction> factions;
	protected static OnimaFaction plugin;
	protected static boolean refreshed;
	
	static {
		plugin = OnimaFaction.getInstance();
		factions = new ArrayList<>();
	}
	
	protected String name;
	protected long created, renameCooldown;
	protected List<Claim> claims;
	protected List<Flag> flags;
	protected boolean open, permanent;
	
	{
		created = System.currentTimeMillis();
		claims = new ArrayList<>();
		flags = new ArrayList<>();
	}
	
	public Faction(String name) {
		this.name = name;
		
		addFlag(Flag.PVP_TIMER_DENY_ENTRY);
		save();
	}
	
	/**
	 * This method gets the faction name.
	 * 
	 * @return The faction name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * This method sets the faction name.
	 * 
	 * @param name - The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * This method gets the faction creation time.
	 * 
	 * @return The faction creation time.
	 */
	public long getCreated() {
		return created;
	}

	/**
	 * This method sets the faction created time.
	 * 
	 * @param name - The created time.
	 */
	public void setCreated(long created) {
		this.created = created;
	}
	
	/**
	 * This method checks if the faction is opened.
	 * 
	 * @return true if the faction is opened.<br>
	 * false if the faction is closed.
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * This method sets whether the faction is opened or not.
	 * 
	 * @param open - Sets the faction open/closed.
	 */
	public void setOpen(boolean open) {
		this.open = open;
	}

	/**
	 * This method sets the claims for this faction.
	 * 
	 * @param claims - List of claims to set.
	 */
	public void setClaims(List<Claim> claims) {
		this.claims = claims;
	}
	
	/**
	 * This method returns the faction claims.
	 * 
	 * @return The faction claims
	 */
	public List<Claim> getClaims() {
		return claims;
	}
	
	/**
	 * This method adds a claim to the faction, it's calling the {@link FactionClaimChangeEvent} event
	 * 
	 * @param claim - Claim to add.
	 * @param profile - Profile who added this claim.
	 * @return true if the claim was successfully added.<br>
	 * false if the claim wasn't added.
	 */
	public boolean addClaim(Claim claim, FPlayer fPlayer) {
		FactionClaimChangeEvent event = new FactionClaimChangeEvent(this, claim, fPlayer, ClaimChangeCause.CLAIM);
		Bukkit.getPluginManager().callEvent(event);
		
		if (event.isCancelled())
			return false;
		
		return claims.add(claim);
	}
	
	/**
	 * This method removes a claim to the faction, it's calling the {@link FactionClaimChangeEvent} event
	 * 
	 * @param claim - Claim to remove.
	 * @param offlineProfile - Offline profile who removed this claim.
	 * @return true if the claim was successfully removed.<br>
	 * false if the faction didn't have this claim.
	 */
	public boolean removeClaim(Claim claim, FPlayer fPlayer) {
		FactionClaimChangeEvent event = new FactionClaimChangeEvent(this, claim, fPlayer, ClaimChangeCause.UNCLAIM);
		Bukkit.getPluginManager().callEvent(event);
		
		if (event.isCancelled())
			return false;
		
		claim.remove();
		return claims.remove(claim);
	}
	
	/**
	 * This method clears all the faction's claims.
	 */
	public void clearClaims() {
		claims.forEach(claim -> claim.remove());
		claims.clear();
	}
	
	/**
	 * This method checks if the faction is a Road or not.
	 * 
	 * @return true if the faction is a Road.<br>
	 * false if the faction is not a Road.
	 */
	public boolean isRoad() {
		return this instanceof RoadFaction;
	}
	
	/**
	 * This method checks if the faction is a SafeZone or not.
	 * 
	 * @return true if the faction is a SafeZone.<br>
	 * false if the faction is not a SafeZone.
	 */
	public boolean isSafeZone() {
		return this instanceof SafeZoneFaction;
	}
	
	/**
	 * This method checks if the faction is a Wilderness or not.
	 * 
	 * @return true if the faction is a Wilderness.<br>
	 * false if the faction is not a Wilderness.
	 */
	public boolean isWilderness() {
		return this instanceof WildernessFaction;
	}
	
	/**
	 * This method checks if the faction is a WarZone or not.
	 * 
	 * @return true if the faction is a WarZone.<br>
	 * false if the faction is not a WarZone.
	 */
	public boolean isWarZone() {
		return this instanceof WarZoneFaction;
	}
	
	/**
	 * This method checks if the faction is normal. It means that it checks if it's not a SafeZone, a Road, a Wilderness and a WarZone.
	 * 
	 * @return true if the faction is normal.<br>
	 * false if the faction is not normal.
	 */
	public boolean isNormal() {
		return !isSafeZone() && !isRoad() && !isWilderness() && !isWarZone();
	}
	
	/**
	 * This method sends the informations of this faction.
	 * 
	 * @param sender - CommandSender to send the informations.
	 */
	public void sendShow(CommandSender sender) {
		sender.sendMessage(ConfigurationService.STAIGHT_LINE);
		sender.sendMessage(' ' + (open ? "§a[Ouverte]" : "§8[Fermée]"));
		if (permanent)
			sender.sendMessage("§aCette faction est permanente.");
		sender.sendMessage(" §eCréée le : "+Methods.toFormatDate(created, ConfigurationService.DATE_FORMAT_HOURS));
		sender.sendMessage(' ' + getDisplayName(sender));
		sender.sendMessage(' ' + "Location : §cAucune");
		sender.sendMessage(" §eClaims : §b" + claims.size() + "§e/§b" + ConfigurationService.MAX_CLAIMS);
		sender.sendMessage(ConfigurationService.STAIGHT_LINE);
	}
	
	/**
	 * This method checks the relation between this faction and the given faction.
	 * 
	 * @param faction - Faction to check the relation with.
	 * @return A relation.
	 */
	public Relation getRelation(Faction faction) {
		if (faction instanceof PlayerFaction) {
			PlayerFaction playerFaction = (PlayerFaction) faction;

			if (playerFaction == this)
				return Relation.MEMBER;

			if (playerFaction.getRelations().containsKey(name))
				return Relation.ALLY;
		}

		return Relation.ENEMY;
	}

	/**
	 * This method checks the relation between this faction and the given command sender.
	 * 
	 * @param sender - CommandSender to check the relation with.
	 * @return A relation.
	 */
	public Relation getRelation(CommandSender sender) {
		return sender instanceof Player ? getRelation(FPlayer.getByPlayer((Player) sender).getFaction()) : Relation.ENEMY;
	}
	
	/**
	 * This method gets the name that should be displayed for a given command sender.
	 * 
	 * @param sender - CommandSender who would see the formatted name.
	 * @return A string with the name formatted.
	 */
	public String getDisplayName(CommandSender sender) {
		return getRelation(sender).getColor() + name;
	}

	/**
	 * This method gets the name that should be displayed for a given faction.
	 * 
	 * @param faction - Faction to get the color.
	 * @return A string with the name formatted.
	 */
	public String getDisplayName(Faction faction) {
		return getRelation(faction).getColor() + name;
	}
	
	public boolean isPermanent() {
		return permanent;
	}
	
	public void setPermanent(boolean permanent) {
		this.permanent = permanent;
	}
	
	public long getRenameCooldown() {
		return renameCooldown;
	}
	
	public void setRenameCooldown(long renameCooldown) {
		this.renameCooldown = renameCooldown;
	}
	
	@Override
	public List<Flag> getFlags() {
		return flags;
	}
	
	@Override
	public void addFlag(Flag flag) {
		flags.add(flag);
	}
	
	@Override
	public void setFlags(Flag... flags) {
		this.flags.clear();
		
		if (flags != null)
			this.flags.addAll(Sets.newHashSet(flags));
	}
	
	@Override
	public void setFlags(String string) {
		String[] flagsOnString = string.split(";");
		flags.clear();
		
		if(string.isEmpty()) return;
		
		for(String flagName : flagsOnString) {
			Flag flag = Flag.fromName(flagName);
			
			if(flag != null) flags.add(flag);
		}
	}
	
	@Override
	public String flagsToString() {
		StringBuilder builder = new StringBuilder();
		
		if(flags.isEmpty()) {
			builder.append("");
			return builder.toString();
		}
		
		for(Flag flag : flags)
			builder.append(flag.getName()+";");
		
		return builder.toString();
	}
	
	@Override
	public boolean hasFlags() {
		return !flags.isEmpty();
	}
	
	@Override
	public boolean hasFlags(Flag... flags) {
		return this.flags.containsAll(Sets.newHashSet(flags));
	}
	
	@Override
	public boolean hasOneOfThisFlags(Flag... flags) {
		for(Flag flag : flags)
			return this.flags.contains(flag);
		
		return false;
	}
	
	@Override
	public boolean hasFlag(Flag flag) {
		return flags.contains(flag);
	}
	
	@Override
	public void removeFlag(Flag flag) {
		flags.remove(flag);
	}
	
	@Override
	public boolean isSaved() {
		return factions.contains(this);
	}

	@Override
	public void remove() {
		factions.remove(this);
	}

	@Override
	public void save() {
		factions.add(this);
	}

	@Override
	public void updateDatabase() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void queryDatabase(QueryResult<String> result) {
		
	}
	
	public static Faction getFaction(String name) {
		name = ChatColor.stripColor(name);
		
		for (Faction faction : factions) {
			if (faction instanceof PlayerFaction) {
				if (((PlayerFaction) faction).getMember(name) != null)
					return faction;
			}
			
			if (faction.name.equalsIgnoreCase(name))
				return faction;
		}
		
		return null;
	}

	public static List<Faction> getFactions() {
		return factions;
	}
	
}
