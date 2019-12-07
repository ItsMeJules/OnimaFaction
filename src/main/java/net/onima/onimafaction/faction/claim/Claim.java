package net.onima.onimafaction.faction.claim;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.onima.onimaapi.mongo.saver.MongoSerializer;
import net.onima.onimaapi.utils.BetterItem;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.WorldBorder;
import net.onima.onimaapi.zone.Cuboid;
import net.onima.onimaapi.zone.struct.CuboidFace;
import net.onima.onimaapi.zone.struct.Flag;
import net.onima.onimaapi.zone.type.Region;
import net.onima.onimafaction.faction.EggAdvantage;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.EggAdvantageType;
import net.onima.onimafaction.faction.type.WildernessFaction;
import net.onima.onimafaction.players.FPlayer;

public class Claim extends Region implements MongoSerializer {
	
	public static final ItemStack CLAIMING_WAND;
	private static List<Claim> claims;
	
	static {
		claims = new ArrayList<>();
		CLAIMING_WAND = new BetterItem(Material.BLAZE_ROD, 1, 0, ConfigurationService.CLAIMING_WAND_NAME, ConfigurationService.CLAIMING_WAND_LORE).toItemStack();
	}
	
	private Faction faction;
	private double price;
	private List<EggAdvantage> eggAdvantages;
	
	{
		eggAdvantages = new ArrayList<>();
	}
	
	public Claim(Faction faction, String creator, double price, Location location1, Location location2) {
		super(faction.getName() + '_' + claims.size(), "lol", creator, location1, location2);
		this.faction = faction;
		this.price = price;
	}
	
	public Claim(Faction faction, String creator, Location location1, Location location2) {
		this(faction, creator, 0.0D, location1, location2);
	}
	
	public Claim(Faction faction, Location location1, Location location2) {
		this(faction, null, location1, location2);
	}
	
	@Override
	public String getDisplayName(CommandSender sender) {
		return faction.getDisplayName(sender);
	}
	
	/**
	 * This method returns the faction which owns this claim.
	 * 
	 * @return The faction which owns this claim.
	 */
	public Faction getFaction() {
		return faction;
	}
	
	/**
	 * This method returns the price of this claim.
	 * 
	 * @return The price of this claim.
	 */
	public double getPrice() {
		return price;
	}
	
	/**
	 * This method sets the price of this claim.
	 * 
	 * @param price - The price of this claim.
	 */
	public void setPrice(double price) {
		this.price = price;
	}
	
	public List<EggAdvantage> getEggAdvantages() {
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
	public List<Flag> getFlags() {
		return faction.getFlags();
	}
	
	@Override
	public void addFlag(Flag flag) {
		faction.addFlag(flag);
	}
	
	@Override
	public void setFlags(Flag... flags) {
		faction.setFlags(flags);
	}
	
	@Override
	public void setFlags(String string) {
		faction.setFlags(string);
	}
	
	@Override
	public String flagsToString() {
		return faction.flagsToString();
	}
	
	@Override
	public boolean hasFlags() {
		return faction.hasFlags();
	}
	
	@Override
	public boolean hasFlags(Flag... flags) {
		return faction.hasFlags(flags);
	}
	
	@Override
	public boolean hasOneOfThisFlags(Flag... flags) {
		return faction.hasOneOfThisFlags(flags);
	}
	
	@Override
	public boolean hasFlag(Flag flag) {
		return faction.hasFlag(flag);
	}
	
	@Override
	public void removeFlag(Flag flag) {
		faction.removeFlag(flag);
	}
	
	@Override
	public void save() {
		claims.add(this);
	}
	
	@Override
	public void remove() {
		claims.remove(this);
	}
	
	@Override
	public boolean isSaved() {
		return claims.contains(this);
	}
	
	@Override
	public Document getDocument(Object... objects) {
		return new Document("name", name).append("price", price)
				.append("creator", creator).append("created", created)
				.append("location_1", Methods.serializeLocation(getLocation1(), false))
				.append("location_2", Methods.serializeLocation(getLocation2(), false))
				.append("deathban", deathban).append("dtr_loss", dtrLoss)
				.append("priority", priority).append("deathban_multiplier", deathbanMultiplier)
				.append("acces_rank", accessRank == null ? null : accessRank.name())
				.append("eggs", eggAdvantages.stream().map(EggAdvantage::getDocument).collect(Collectors.toCollection(() -> new ArrayList<>(eggAdvantages.size()))));
	}
	
	public static boolean tryToBuyClaim(Player player, ClaimSelection claimSelection) {
		World world = claimSelection.getWorld();
		FPlayer fPlayer = FPlayer.getPlayer(player);
		PlayerFaction faction = fPlayer.getFaction();
		
		if (faction.getClaims().size() >= ConfigurationService.MAX_CLAIMS) {
			player.sendMessage("§cVotre faction a atteind le maximum de claims qui est de " + ConfigurationService.MAX_CLAIMS + '.');
			return false;
		}
		
		Cuboid cuboid = claimSelection.toCuboid();
		double facMoney = faction.getMoney();
		double price = ClaimSelection.calculatePrice(cuboid, faction.getClaims().size());
		
		if (price > facMoney) {
			player.sendMessage("§cVotre faction a besoin de " + (price - facMoney) + ConfigurationService.MONEY_SYMBOL + " pour acheter ce claim.");
			return false;
		}
		
		if (cuboid.getXLength() > ConfigurationService.CLAIM_MAX_WIDTH) {
			player.sendMessage("§cLa largeur d'un claim ne doit pas dépasser " + ConfigurationService.CLAIM_MAX_WIDTH + " blocks.");
			return true;
		}

		if (cuboid.getZLength() > ConfigurationService.CLAIM_MAX_LENGTH) {
			player.sendMessage("§cLa longueur d'un claim ne doit pas dépasser " + ConfigurationService.CLAIM_MAX_LENGTH + " blocks.");
			return false;
		}

		if (cuboid.getXLength() < ConfigurationService.CLAIM_MIN_LENGTH) {
			player.sendMessage("§cLa longueur d'un claim ne doit pas être en-dessous de " + ConfigurationService.CLAIM_MIN_LENGTH + " blocks.");
			return false;
		}

		if (cuboid.getZLength() < ConfigurationService.CLAIM_MIN_WIDTH) {
			player.sendMessage("§cLa largeur d'un claim ne doit pas être en-dessous de " + ConfigurationService.CLAIM_MIN_WIDTH + " blocks.");
			return false;
		}
		
		int lowX = (int) cuboid.getMinX();
		int upX = (int) cuboid.getMaxX();
		int lowZ = (int) cuboid.getMinZ();
		int upZ = (int) cuboid.getMaxZ();
		
		for (int x = lowX - ConfigurationService.CLAIM_BUFFER_RADIUS; x < upX + ConfigurationService.CLAIM_BUFFER_RADIUS; x++) {
			for (int z = lowZ - ConfigurationService.CLAIM_BUFFER_RADIUS; z < upZ + ConfigurationService.CLAIM_BUFFER_RADIUS; z++) {
				Region region = getClaimAndRegionAt(new Location(world, upX, 60, upZ));
				
				if (region instanceof Claim) {
					Claim found = (Claim) region;
					
					if (!(found instanceof WildernessClaim) && !ConfigurationService.CLAIM_ALLOWED_BESIDE_ROAD && !found.faction.isRoad() && !found.faction.equals(faction)) {
						player.sendMessage("§cLe claim contient un claim ennemie à moins de " + ConfigurationService.CLAIM_BUFFER_RADIUS + " blocks.");
						return false;
					}
				} else return false;
			}
				
		}

		List<Claim> claims = faction.getClaims();
		boolean joined = claims.isEmpty();
		
		if (!joined) {
			for (Claim facClaim : claims) {
				Cuboid expanded = facClaim.toCuboid().clone().expand(CuboidFace.HORIZONTAL);
				
				if (expanded.contains(cuboid)) {
					joined = true;
					break;
				}
			}
			
			if (!joined) {
				player.sendMessage("§cTous les claims de votre faction doivent être collé.");
				return false;
			}
		}
		
		Claim claim = null;
		claimSelection.setPrice(price);
		
		if (faction.addClaim(claim = claimSelection.toClaim(faction, fPlayer.getApiPlayer().getName()), fPlayer)) {
			claim.setCuboid(cuboid);
			claim.setName(faction.getName() + '_' + claims.size());
			
			Location middle = cuboid.getCenterLocation();
			
			player.sendMessage("§7Claim acheté pour §e" + price + ConfigurationService.MONEY_SYMBOL + "§7.");
			faction.broadcast(new JSONMessage("§d§o" + fPlayer.getRole().getRole() + claim.getCreator() +" §7a claim un territoire pour la faction. Passez votre souris pour plus d'informations.",
					"§e" + claim.getName() + ' ' + cuboid.getXLength() + 'x' + cuboid.getZLength() + " §7- §d§o" + middle.getBlockX() + " §c| §d§o" + middle.getBlockZ() + " §7(§e" + claim.getPrice() + ConfigurationService.MONEY_SYMBOL + "§7)"));
			faction.removeMoney(price);
		}

		return true;
	}
	
	public static boolean canSelectHere(Player player, Location location, boolean sendMessage) {
		if (!ConfigurationService.CLAIMABLE_WORLD.contains(location.getWorld().getName())) {
			if (sendMessage) player.sendMessage("§cVous ne pouvez pas claim dans ce monde.");
			return false;
		}
		
		Region region = null;
		
		if (!((region = getClaimAndRegionAt(location)) instanceof WildernessClaim)) {
			if (sendMessage) player.sendMessage("§cCette location est dans " + (region instanceof Claim ? "un claim existant." : "une région existante."));
			return false;
		}
		
		if (WorldBorder.border(location)) {
			if (sendMessage) player.sendMessage("§cVous ne pouvez pas claim en-dehors de la bordure.");
			return false;
		}
		
		return true;
	}
	
	public static Claim getClaimAt(Location location) {
		List<Claim> claims = new LinkedList<>();
		
		for (Claim claim : Claim.claims) {
			if (claim.toCuboid().contains(location))
				 claims.add(claim);
		}
		
		if (claims.isEmpty())
			return WildernessFaction.claim();
		else {
			Claim priorited = claims.get(0);
			
			if (claims.size() > 1) {
				for (Claim claim : claims) {
					if (claim.getPriority() > priorited.getPriority())
						priorited = claim;
				}
			}
			
			return priorited;
		}
	}
	
	public static Claim getClaimAt(World world, int x, int z) {
		return getClaimAt(new Location(world, x, 10, z));
	}
	
	public static Region getClaimAndRegionAt(Location location) {
		List<Region> regions = new LinkedList<>();
		
		for (Claim claim : Claim.claims) {
			if (claim.toCuboid().contains(location))
				 regions.add(claim);
		}
		
		for (Region region : Region.regions) {
			if (region.toCuboid().contains(location))
				regions.add(region);
		}
		
		if (regions.isEmpty())
			return WildernessFaction.claim();
		else {
			Region priorited = regions.get(0);
			
			if (regions.size() > 1) {
				for (Region region : regions) {
					if (region.getPriority() > priorited.getPriority())
						priorited = region;
				}
			}
			
			return priorited;
		}
	}
	
}