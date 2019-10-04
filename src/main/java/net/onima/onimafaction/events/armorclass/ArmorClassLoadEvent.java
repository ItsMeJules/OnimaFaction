package net.onima.onimafaction.events.armorclass;

import net.onima.onimafaction.armorclass.ArmorClass;
import net.onima.onimafaction.players.FPlayer;

public class ArmorClassLoadEvent extends ArmorClassEvent {
	
	public ArmorClassLoadEvent(ArmorClass armorClass, FPlayer fPlayer) {
		super(armorClass, fPlayer);
	}
	
}
