package net.onima.onimafaction.events;

import org.bukkit.event.Cancellable;

import net.onima.onimafaction.faction.PlayerFaction;

public class FactionDTRChangeEvent extends FactionEvent implements Cancellable {
	
	private DTRChangeCause cause;
	private float oldDTR, newDTR;
	private boolean cancel;

	public FactionDTRChangeEvent(PlayerFaction faction, DTRChangeCause cause, float oldDTR, float newDTR) {
		super(faction);
		
		this.cause = cause;
		this.oldDTR = oldDTR;
		this.newDTR = newDTR;
	}
	
	public DTRChangeCause getDTRChangeCause() {
		return cause;
	}

	public float getOldDTR() {
		return oldDTR;
	}

	public float getNewDTR() {
		return newDTR;
	}
	
	public void setNewDTR(float newDTR) {
		this.newDTR = newDTR;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
	
	public static enum DTRChangeCause {
		REGENERATING,
		ADMIN,
		DEATH,
		PLUGIN;
	}

}
