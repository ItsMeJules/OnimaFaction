package net.onima.onimafaction.events.server_event;

import org.bukkit.event.Cancellable;

import net.onima.onimafaction.timed.FactionServerEvent;

public class FactionEventServerStartEvent extends FactionEventServerEvent implements Cancellable {

	private boolean cancelled;
	
	public FactionEventServerStartEvent(FactionServerEvent serverEvent) {
		super(serverEvent);
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

}
