package net.onima.onimafaction.events.armorclass;

import net.onima.onimafaction.armorclass.ArmorClass;
import net.onima.onimafaction.players.FPlayer;

public class ArmorClassUnequipEvent extends ArmorClassEvent {
	
	private ArmorClassUnequipCause cause;

	public ArmorClassUnequipEvent(ArmorClass armorClass, FPlayer fPlayer, ArmorClassUnequipCause cause) {
		super(armorClass, fPlayer);
		this.cause = cause;
	}

	public ArmorClassUnequipCause getCause() {
		return cause;
	}
	
	public static enum ArmorClassUnequipCause {
		PLAYER,
		BREAK,
		DEATH,
		CLEAR;
	}

}
