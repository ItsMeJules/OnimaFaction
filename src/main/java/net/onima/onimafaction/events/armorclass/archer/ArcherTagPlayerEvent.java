package net.onima.onimafaction.events.armorclass.archer;

import net.onima.onimafaction.armorclass.Archer;
import net.onima.onimafaction.armorclass.ArmorClass;
import net.onima.onimafaction.armorclass.utils.ColoredArmor;
import net.onima.onimafaction.events.armorclass.ArmorClassEvent;
import net.onima.onimafaction.players.FPlayer;

public class ArcherTagPlayerEvent extends ArmorClassEvent {
	
	private FPlayer fPlayerTagged;
	private int mark;
	private double distance, nextDouble;
	private ColoredArmor colouredArmor;

	public ArcherTagPlayerEvent(ArmorClass armorClass, FPlayer fPlayer, FPlayer fPlayerTagged, int mark, double distance, 
			double nextDouble, ColoredArmor colouredArmor) {
		super(armorClass, fPlayer);
		this.fPlayerTagged = fPlayerTagged;
		this.mark = mark;
		this.distance = distance;
		this.nextDouble = nextDouble;
		this.colouredArmor = colouredArmor;
	}
	
	@Override
	public Archer getArmorClass() {
		return (Archer) super.getArmorClass();
	}

	public FPlayer getFPlayerTagged() {
		return fPlayerTagged;
	}

	public int getMark() {
		return mark;
	}
	
	public double getDistance() {
		return distance;
	}

	public double getNextDouble() {
		return nextDouble;
	}

	public ColoredArmor getColouredArmor() {
		return colouredArmor;
	}
	
}
