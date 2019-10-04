package net.onima.onimafaction.events.battle_royale;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.timed.event.BattleRoyale;
import net.onima.onimafaction.timed.event.BattleRoyale.Phase;

public class BattleRoyalePhaseEvent extends Event {
	
	private static HandlerList handlers = new HandlerList();
	protected static BattleRoyale battleRoyale = OnimaFaction.getInstance().getBattleRoyale();
	
	private Phase phase;
	
	public BattleRoyalePhaseEvent(Phase phase) {
		this.phase = phase;
	}
	
	public Phase getPhase() {
		return phase;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
