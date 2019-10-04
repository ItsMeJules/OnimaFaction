package net.onima.onimafaction.events;

import org.bukkit.event.Cancellable;

import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.players.OfflineFPlayer;

public class FactionPlayerJoinEvent extends FactionEvent implements Cancellable {

	private OfflineFPlayer offlineFPlayer;
	private boolean cancelled, forceJoin;

	public FactionPlayerJoinEvent(OfflineFPlayer offlineFPlayer, Faction faction, boolean forceJoin) {
		super(faction);
		
		this.offlineFPlayer = offlineFPlayer;
		this.forceJoin = forceJoin;
	}

	public OfflineFPlayer getOfflineFPlayer() {
		return offlineFPlayer;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public boolean isForceJoin() {
		return forceJoin;
	}

}
