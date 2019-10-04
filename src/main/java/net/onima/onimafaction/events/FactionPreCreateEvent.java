package net.onima.onimafaction.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class FactionPreCreateEvent extends FactionEvent implements Cancellable {
	
	private boolean cancelled;
	private String name;
	private Player player;

	public FactionPreCreateEvent(String name, Player player) {
		super(null);
		this.name = name;
		this.player = player;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Player getPlayer() {
		return player;
	}

}
