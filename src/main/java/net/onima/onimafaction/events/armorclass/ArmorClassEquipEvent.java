package net.onima.onimafaction.events.armorclass;

import net.onima.onimafaction.armorclass.ArmorClass;
import net.onima.onimafaction.players.FPlayer;

public class ArmorClassEquipEvent extends ArmorClassEvent {

	public ArmorClassEquipEvent(ArmorClass armorClass, FPlayer fPlayer) {
		super(armorClass, fPlayer);
	}

}
