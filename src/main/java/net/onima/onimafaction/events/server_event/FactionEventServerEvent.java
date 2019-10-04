package net.onima.onimafaction.events.server_event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.onima.onimafaction.timed.FactionServerEvent;

public class FactionEventServerEvent extends Event {
	
	private static HandlerList handlers = new HandlerList();
	
	private FactionServerEvent serverEvent;
	
	public FactionEventServerEvent(FactionServerEvent serverEvent) {
		this.serverEvent = serverEvent;
	}
	
	public FactionServerEvent getServerEvent() {
		return serverEvent;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
