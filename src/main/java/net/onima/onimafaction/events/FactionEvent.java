package net.onima.onimafaction.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.onima.onimafaction.faction.Faction;

public class FactionEvent extends Event {
	
	private static HandlerList handlers = new HandlerList();
	
	private Faction faction;
	
	public FactionEvent(Faction faction) {
		this.faction = faction;
	}
	
	public Faction getFaction() {
		return faction;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
