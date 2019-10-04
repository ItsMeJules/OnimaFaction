package net.onima.onimafaction.events.battle_royale;

import org.bukkit.event.Cancellable;

import net.onima.onimafaction.timed.event.BattleRoyale.Phase;

public class BattleRoyalePhaseShrinkEvent extends BattleRoyalePhaseEvent implements Cancellable {

	private boolean cancelled;
	
	public BattleRoyalePhaseShrinkEvent(Phase phase) {
		super(phase);
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
