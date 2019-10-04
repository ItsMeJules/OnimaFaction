package net.onima.onimafaction.faction.claim;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import net.onima.onimafaction.faction.type.WildernessFaction;

/**
 * This class is just to return the wilderness claim when no claims is found.
 */
public class WildernessClaim extends Claim {

	/** Locations = 10000, -10000, 10000<br>
	 * Price = 0
	 */
	public WildernessClaim() {
		super(WildernessFaction.get(), new Location(Bukkit.getWorld("world"), 10000, -10000, 10000), new Location(Bukkit.getWorld("world"), 10000, -10000, 10000));
		super.priority = -10000;
	}

}
