package net.onima.onimafaction.faction.claim;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.block.Action;

import net.onima.onimaapi.fakeblock.FakeBlock;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.zone.Cuboid;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.players.FPlayer;

public class ClaimSelection {
	
	private Location location1, location2;
	private Double price;
	private long lastUpdate;
	private List<FakeBlock> pillar1, pillar2;
	private Claim claim;
	
	{
		pillar1 = new ArrayList<>();
		pillar2 = new ArrayList<>();
	}
	
	public ClaimSelection(Location location1, Location location2) {
		this.location1 = location1;
		this.location2 = location2;
	}
	
	public ClaimSelection() {
		this(null, null);
	}

	public void setLocation(Action action, Location location) {
		lastUpdate = System.currentTimeMillis();
		if (action == Action.LEFT_CLICK_BLOCK)
			 location1 = location;
		else if (action == Action.RIGHT_CLICK_BLOCK)
			location2 = location;
	}
	
	public Location getLocation(Action action) {
		if (action == Action.LEFT_CLICK_BLOCK)
			return location1;
		else
			return location2;
	}
	
	public Location getLocation1() {
		return location1;
	}
	
	public Location getLocation2() {
		return location2;
	}
	
	public World getWorld() {
		return location1.getWorld();
	}
	
	public int getPosition(Action action) {
		switch(action) {
		case LEFT_CLICK_BLOCK:
			return 1;
		case RIGHT_CLICK_BLOCK:
			return 2;
		default:
			return 0;
		}
	}
	
	public List<FakeBlock> setPillar(Action action, List<FakeBlock> blocks) {
		if (action == Action.LEFT_CLICK_BLOCK) {
			pillar1.clear();
			pillar1 = blocks;
		} else if (action == Action.RIGHT_CLICK_BLOCK) {
			pillar2.clear();
			pillar2 = blocks;
		}

		return blocks;
	}
	
	public List<FakeBlock> getPillar(Action action) {
		if (action == Action.LEFT_CLICK_BLOCK) 
			return pillar1;
		else if (action == Action.RIGHT_CLICK_BLOCK)
			return pillar2;
		
		return null;
	}
	
	public void remove(APIPlayer apiPlayer) {
		pillar1.forEach(fb -> apiPlayer.removeFakeBlock(fb));
		pillar2.forEach(fb -> apiPlayer.removeFakeBlock(fb));
		FPlayer.getPlayer(apiPlayer.getUUID()).setClaimSelection(null);
		apiPlayer.toPlayer().setItemInHand(null);
	}
	
	public boolean hasBothLocationSet() {
		return location1 != null && location2 != null;
	}

	public Claim toClaim(Faction faction, String creator) {
		return hasBothLocationSet() ? claim == null ? claim = new Claim(faction, creator, price, location1, location2) : claim : null;
	}
	
	public Cuboid toCuboid() {
		return new Cuboid(location1, location2, false).expandVertical();
	}
	
	public void setPrice(Double price) {
		this.price = price;
	}
	
	public long getLastUpdate() {
		return lastUpdate;
	}
	
	public static double calculatePrice(Cuboid claim, int factionClaims) {
		double area = claim.getArea(); //retourne l'aire du claim ex 32x32 = 1024
		double blockMultiplier = 0.4; //multiplieur de block en trop quand c'est au-dessus de maxRange
		double price = 0; //prix du claim
		
		do { 									//tant que l'aire est supérieur à 250
			price += blockMultiplier * 250;		//je fais 250 * 0.4 etc etc à chaque itération j'augment de 0.4 le block multiplier
			blockMultiplier += 0.4;
		} while ((area -= 250) > 250);
		
		if (area > 0)						//et pour les blocks qui restent bah je les multiplie par le block multiplier
			price += blockMultiplier * area; 
		
		return Double.valueOf(Methods.round("0.00", price + 500 * factionClaims)); //je retourne la valeur arrondie au centième du prix du claim
	}

}
