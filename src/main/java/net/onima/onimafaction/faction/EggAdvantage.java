package net.onima.onimafaction.faction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.google.common.collect.ImmutableSet;

import net.onima.onimaapi.mongo.saver.MongoSerializer;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.faction.struct.EggAdvantageType;
import net.onima.onimafaction.plants.FastPlant;
import net.onima.onimafaction.plants.PlantType;
import net.onima.onimafaction.plants.type.BambooFastPlant;
import net.onima.onimafaction.plants.type.NetherWartFastPlant;
import net.onima.onimafaction.plants.type.PumpkinFastPlant;
import net.onima.onimafaction.plants.type.WaterMelonFastPlant;

public class EggAdvantage implements MongoSerializer {
	
	private static Set<Material> growingBlocks;
	
	static {
		growingBlocks = ImmutableSet.of(Material.SOIL, Material.SAND, Material.SOUL_SAND, Material.DIRT, Material.GRASS);
	}
	
	private EggAdvantageType type;
	private Map<Location, Location> locations;
	private Faction faction;
	
	{
		locations = new HashMap<>();
	}
	
	public EggAdvantage(EggAdvantageType type, Faction faction) {
		this.type = type;
		this.faction = faction;
	}

	public boolean addEgg(Location location) {
		Location under = location.clone().add(0, -1, 0);
		
		if (under.getBlock().getType() != Material.EMERALD_BLOCK)
			return false;
		
		Claim claim = Claim.getClaimAt(location);
		
		claim.getEggAdvantages().add(this);
		locations.put(under, location);
		
		if (type == EggAdvantageType.CROPS && getAmount() == 1) {
			for (Block block : claim.toCuboid()) {
				if (!growingBlocks.contains(block.getType()))
					continue;
				
				Block up = block.getRelative(BlockFace.UP);
				PlantType plantType = PlantType.fromSeed(up.getType());
				
				if (plantType != null) {
					FastPlant plant = null;
					
					switch (plantType) {
					case MELON:
						plant = new WaterMelonFastPlant(up.getLocation());
						break;
					case PUMPKIN:
						plant = new PumpkinFastPlant(up.getLocation());
						break;
					case SUGAR_CANE:
						plant = new BambooFastPlant(up.getLocation());
						break;
					case NETHER_WART:
						plant = new NetherWartFastPlant(up.getLocation());
						break;
					default:
						plant = new FastPlant(plantType, up.getLocation(), FastPlant.DEFAULT_HARVEST_TIME);
						break;
					}
					
					if (!plant.canGrowFaster())
						continue;
					
					plant.save();
				}
			}
		}
		
		return true;
	}
	
	public EggAdvantageType getType() {
		return type;
	}
	
	public Map<Location, Location> getLocations() {
		return locations;
	}

	public Faction getFaction() {
		return faction;
	}
	
	public int getAmount() {
		return locations.size();
	}

	public void remove(Location location) {
		locations.remove(location);
	}
	
	public void remove() {
		Iterator<Entry<Location, Location>> iterator = locations.entrySet().iterator();
		
		iterator.next();
		iterator.remove();
	}
	
	@Override
	public Document getDocument(Object... objects) {
		List<String> locations = new ArrayList<>();
		
		for (Entry<Location, Location> entry : this.locations.entrySet())
			locations.add(Methods.serializeLocation(entry.getKey(), false) + "@" + Methods.serializeLocation(entry.getValue(), false));
		
		return new Document("type", type.name())
				.append("locations", locations);
	}
	
	public static Set<Material> getGrowingBlocks() {
		return growingBlocks;
	}
	
}
