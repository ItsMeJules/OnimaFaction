package net.onima.onimafaction.events.battle_royale;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.timed.event.BattleRoyale;

public class BattleRoyaleEvent extends Event implements Cancellable {
	
	private static HandlerList handlers = new HandlerList();
	protected static BattleRoyale battleRoyale = OnimaFaction.getInstance().getBattleRoyale();
	
	private boolean cancelled;
	
	protected BattleRoyaleEvent() {
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
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
