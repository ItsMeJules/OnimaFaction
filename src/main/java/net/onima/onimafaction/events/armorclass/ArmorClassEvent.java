package net.onima.onimafaction.events.armorclass;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.onima.onimafaction.armorclass.ArmorClass;
import net.onima.onimafaction.players.FPlayer;

public class ArmorClassEvent extends Event {
	
	private static HandlerList handlers = new HandlerList();
	
	private ArmorClass armorClass;
	private FPlayer fPlayer;
	
	public ArmorClassEvent(ArmorClass armorClass, FPlayer fPlayer) {
		this.armorClass = armorClass;
		this.fPlayer = fPlayer;
	}

	public ArmorClass getArmorClass() {
		return armorClass;
	}

	public FPlayer getFPlayer() {
		return fPlayer;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
