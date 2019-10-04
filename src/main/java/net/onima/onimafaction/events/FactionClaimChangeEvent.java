package net.onima.onimafaction.events;

import org.bukkit.event.Cancellable;

import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.players.FPlayer;

public class FactionClaimChangeEvent extends FactionEvent implements Cancellable {

	private Claim claim;
	private FPlayer fPlayer;
	private ClaimChangeCause cause;
	private boolean cancelled;

	public FactionClaimChangeEvent(Faction faction, Claim claim, FPlayer fPlayer, ClaimChangeCause cause) {
		super(faction);
		this.claim = claim;
		this.fPlayer = fPlayer;
		this.cause = cause;
	}

	public Claim getClaim() {
		return claim;
	}

	public FPlayer getFPlayer() {
		return fPlayer;
	}
	
	public ClaimChangeCause getClaimChangeCause() {
		return cause;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public static enum ClaimChangeCause {
		CLAIM,
		UNCLAIM;
	}
	
}
