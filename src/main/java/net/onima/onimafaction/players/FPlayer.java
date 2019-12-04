package net.onima.onimafaction.players;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import net.onima.onimaapi.fakeblock.FakeBlock;
import net.onima.onimaapi.fakeblock.FakeBlockData;
import net.onima.onimaapi.fakeblock.FakeType;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.utils.CombatLogger;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.zone.struct.Flag;
import net.onima.onimaapi.zone.type.Region;
import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.armorclass.Archer;
import net.onima.onimafaction.armorclass.ArmorClass;
import net.onima.onimafaction.armorclass.Bard;
import net.onima.onimafaction.armorclass.Mineur;
import net.onima.onimafaction.armorclass.Reaper;
import net.onima.onimafaction.armorclass.Rogue;
import net.onima.onimafaction.cooldowns.CombatTagCooldown;
import net.onima.onimafaction.cooldowns.PvPTimerCooldown;
import net.onima.onimafaction.events.armorclass.ArmorClassLoadEvent;
import net.onima.onimafaction.events.armorclass.ArmorClassUnequipEvent;
import net.onima.onimafaction.events.armorclass.ArmorClassUnequipEvent.ArmorClassUnequipCause;
import net.onima.onimafaction.faction.StuckRequest;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.faction.claim.ClaimSelection;
import net.onima.onimafaction.faction.claim.WildernessClaim;

public class FPlayer extends OfflineFPlayer {
	
	private static Map<UUID, FPlayer> players;
	
	static {
		players = new HashMap<>();
	}
	
	private APIPlayer apiPlayer;
	private ClaimSelection claimSelection;
	private StuckRequest stuckRequest;
	private ArmorClass[] armorClasses;
	private int archerTag;
	private Collection<PotionEffect> effectsTookFromBarding;
	private Collection<String> effectsFromBard;
	private Region region;
	
	public FPlayer(APIPlayer apiPlayer) {
		super(apiPlayer);
		
		this.apiPlayer = apiPlayer;
		
		players.put(apiPlayer.getUUID(), this);
	}
	
	public void loadLogin() {
		armorClasses = new ArmorClass[] {new Mineur(this), new Bard(this), new Archer(this), new Reaper(this), new Rogue(this)};
		effectsTookFromBarding = new HashSet<>();
		effectsFromBard = new HashSet<>();
	}
	
	public void loadJoin() {
		region = Claim.getClaimAndRegionAt(apiPlayer.toPlayer().getLocation());
	}
	
	public APIPlayer getApiPlayer() {
		return apiPlayer;
	}

	public ClaimSelection getClaimSelection() {
		return claimSelection;
	}

	public void setClaimSelection(ClaimSelection claimSelection) {
		this.claimSelection = claimSelection;
	}

	public StuckRequest getStuckRequest() {
		return stuckRequest;
	}

	public void setStuckRequest(StuckRequest stuckRequest) {
		this.stuckRequest = stuckRequest;
	}
	
	public ArmorClass getArmorClass(Class<? extends ArmorClass> armorClass) {
		for (ArmorClass clazz : armorClasses) {
			if (clazz.getClass() == armorClass)
				return clazz;
		}
		return null;
	}
	
	public boolean hasArmorClass(Class<? extends ArmorClass> armorClass) {
		return getArmorClass(armorClass) != null;
	}

	public ArmorClass getEquippedClass() {
		for (ArmorClass clazz : armorClasses) {
			if (clazz.isActivated())
				return clazz;
		}
		return null;
	}

	public ArmorClass getLoadingClass() {
		for (ArmorClass clazz : armorClasses) {
			if (clazz.isLoading())
				return clazz;
		}
		return null;
	}
	
	public void equipClass(ArmorClass armorClass, ArmorClassUnequipCause cause) {
		if (armorClass == null) {
			ArmorClass equipped = getEquippedClass();
			
			if (equipped != null && equipped.onUnequip()) {
				equipped.setActivated(false);
				Bukkit.getPluginManager().callEvent(new ArmorClassUnequipEvent(equipped, this, cause));
			}
		} else if (!armorClass.equals(getEquippedClass()) && armorClass.onLoad()) {
			armorClass.setLoading(true);
			Bukkit.getPluginManager().callEvent(new ArmorClassLoadEvent(armorClass, this));
		}
	}
	
	public ArmorClass[] getPlayerClasses() {
		return armorClasses;
	}
	
	public int getArcherTag() {
		return archerTag;
	}
	
	public void setArcherTag(int archerTag) {
		this.archerTag = archerTag;
	}
	
	public void incrementTag() {
		int tag = getArcherTag();
		
		if (tag < 2)
			archerTag++;
	}
	
	public void setfMap(boolean fMap, boolean message) {
		Location location = apiPlayer.toPlayer().getLocation();
		int radius = 50, count = -1;
		List<Claim> toMap = new ArrayList<>();
		
		if (fMap) {
			for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
				for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
					Claim claim = Claim.getClaimAt(location.getWorld(), x, z);
					if (!(claim instanceof WildernessClaim) && !toMap.contains(claim))
						toMap.add(claim);
				}
			}
			
			if (toMap.isEmpty() && message) {
				apiPlayer.sendMessage("§cIl n'y a pas de claims dans un rayon de 50 blocks autour de vous.");
				return;
			}
			
			setfMap(true);
			List<FakeBlock> toGenerate = new ArrayList<FakeBlock>(toMap.size() * 4 * 150);
			
			for (Claim claim : toMap) {
				if (++count >= FakeBlockData.CLAIM_MAP_BLOCKS_DATA.length) count = -1;
				
				toGenerate.addAll(FakeType.CLAIM_MAP.toBlocks(claim.toCuboid().getCornersPillars(), count));
				
				if (message)
					apiPlayer.sendMessage(claim.getDisplayName(apiPlayer.toPlayer()) + " §7possède §e" + claim.getName() + " §7(affiché avec §e" + Methods.getItemName(new ItemStack(FakeBlockData.CLAIM_MAP_BLOCKS_DATA[count].getItemType())) + "§7)");
			}
			
			Bukkit.getScheduler().runTaskAsynchronously(OnimaFaction.getInstance(), () -> {
				FakeBlock.generate(apiPlayer, toGenerate);
			});
			
			
		} else {
			if (message)
				apiPlayer.sendMessage("§ePillers de claim disparu.");
			apiPlayer.removeFakeBlockByType(FakeType.CLAIM_MAP);
			setfMap(false);
		}
	}
	
	public Collection<PotionEffect> getEffectsTookFromBarding() {
		return effectsTookFromBarding;
	}
	
	public void addEffectTookFromBarding(PotionEffect effect) {
		for (PotionEffect took : effectsTookFromBarding) {
			if (took.getType().equals(effect.getType()))
				return;
		}
		effectsTookFromBarding.add(effect);
	}
	
	public Collection<String> getEffectsFromBard() {
		return effectsFromBard;
	}
	
	public Region getRegionOn() {
		return region;
	}
	
	public void setRegionOn(Region region) {
		this.region = region;
	}
	
	public List<FakeBlock> playerBarriers(Location to) {
		int toX = to.getBlockX(), toY = to.getBlockY(), toZ = to.getBlockZ();
		World world = to.getWorld();
		Iterator<FakeBlock> iterator = apiPlayer.getFakeBlocks().iterator();
		List<FakeBlock> blocks = new ArrayList<>(ConfigurationService.DISTANCE_BARRIER_HORIZONTAL * (ConfigurationService.DISTANCE_BARRIER_ABOVE + ConfigurationService.DISTANCE_BARRIER_BELOW) * 2);
		
		while (iterator.hasNext()) {
			FakeBlock fakeBlock = iterator.next();
			Location location = fakeBlock.getLocation();
			FakeType type = fakeBlock.getType();
			
			if (type == FakeType.CLAIM_MAP || type == FakeType.CREATE_CLAIM) continue;
			
			if (location.getWorld().getUID().equals(world.getUID())
					&& (Math.abs(toX - location.getBlockX()) > ConfigurationService.DISTANCE_BARRIER_HORIZONTAL
							|| Math.abs(toY - location.getBlockY()) > ConfigurationService.DISTANCE_BARRIER_ABOVE
							|| Math.abs(toZ - location.getBlockZ()) > ConfigurationService.DISTANCE_BARRIER_HORIZONTAL)) {
				iterator.remove();
				fakeBlock.reset(apiPlayer.toPlayer());
			}
		}
		
		for (int x = toX - ConfigurationService.DISTANCE_BARRIER_HORIZONTAL; x < toX + ConfigurationService.DISTANCE_BARRIER_HORIZONTAL; x++) {
			for (int y = toY - ConfigurationService.DISTANCE_BARRIER_BELOW; y < toY + ConfigurationService.DISTANCE_BARRIER_ABOVE; y++) {
				for (int z = toZ - ConfigurationService.DISTANCE_BARRIER_HORIZONTAL; z < toZ + ConfigurationService.DISTANCE_BARRIER_HORIZONTAL; z++) {
					Location loc = new Location(world, x, y, z);
					
					if (loc.getBlock().getType() != Material.AIR) continue;
					
					Region region = Claim.getClaimAndRegionAt(loc);
					
					if (region == null || region instanceof WildernessClaim) continue;
					if (!region.toCuboid().wallContains(loc)) continue;
					
					if (apiPlayer.getTimeLeft(CombatTagCooldown.class) > 0L) {
						if (region.hasFlag(Flag.COMBAT_TAG_DENY_ENTRY))
							blocks.add(new FakeBlock(FakeType.COMBAT_REGION_BORDER, loc));
					} else if (apiPlayer.getTimeLeft(PvPTimerCooldown.class) > 0L) {
						if (region.hasFlag(Flag.PVP_TIMER_DENY_ENTRY))
							blocks.add(new FakeBlock(FakeType.PVP_TIMER_REGION_BORDER, loc));
					} else if (apiPlayer.getRank().getRankType().getValue() < region.getAccessRank().getValue())
						blocks.add(new FakeBlock(FakeType.RANK_TOO_LOW_BORDER, loc));
				}
			}
		}
		
		return blocks;
	}
	
	public void spawnCombatLogger() {
		CombatLogger logger = new CombatLogger(apiPlayer.toPlayer().getLocation(), ConfigurationService.COMBAT_LOGGER_NAME.replace("%player%", apiPlayer.getColoredName(false)), apiPlayer.getUUID());
		PlayerInventory inventory = apiPlayer.toPlayer().getInventory();
		
		if (playerFaction != null)
			logger.getTeamMates().addAll(playerFaction.getMembersUUID());
		else
			logger.getTeamMates().add(apiPlayer.getUUID());
		
		logger.setItems(inventory.getArmorContents(), inventory.getContents());
		logger.setExperience(apiPlayer.getExperienceManager().getCurrentExp());
		logger.spawn();
	}
	
	@Override
	public void save() {
		players.put(apiPlayer.getUUID(), this);
	}
	
	@Override
	public void remove() {
		super.remove();
		players.remove(apiPlayer.getUUID());
	}

	public static FPlayer getPlayer(UUID uuid) {
		return players.get(uuid);
	}
	
	public static FPlayer getPlayer(String name) {
		Player online = Bukkit.getPlayer(name);
		
		if (online == null)
			return null;
		else
			return getPlayer(online.getUniqueId());
	}
	
	public static FPlayer getPlayer(Player player) {
		return players.get(player.getUniqueId());
	}
	
	public static Map<UUID, FPlayer> getFPlayers() {
		return players;
	}
	
	public static Collection<FPlayer> getOnlineFPlayers() {
		return players.values();
	}

}
