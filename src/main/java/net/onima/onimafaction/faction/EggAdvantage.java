package net.onima.onimafaction.faction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.Material;

import net.onima.onimaapi.mongo.saver.MongoSerializer;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.faction.struct.EggAdvantageType;

public class EggAdvantage implements MongoSerializer {
	
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
		
		Claim.getClaimAt(location).getEggAdvantages().add(this);
		locations.put(under, location);
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
	
}
