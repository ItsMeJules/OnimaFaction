package net.onima.onimafaction.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.onima.onimafaction.faction.EggAdvantage;
import net.onima.onimafaction.faction.claim.Claim;

public class EggAdvantageRemovedEvent extends Event {
	
	private static HandlerList handlers = new HandlerList();
	
	private EggAdvantage eggAdvantage;
	private Claim claim;
	
	public EggAdvantageRemovedEvent(EggAdvantage eggAdvantage, Claim claim) {
		this.eggAdvantage = eggAdvantage;
		this.claim = claim;
	}

	public EggAdvantage getEggAdvantage() {
		return eggAdvantage;
	}

	public Claim getClaim() {
		return claim;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
