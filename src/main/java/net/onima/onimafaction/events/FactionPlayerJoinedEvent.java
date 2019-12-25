package net.onima.onimafaction.events;

import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.players.OfflineFPlayer;

public class FactionPlayerJoinedEvent extends FactionEvent {

	private OfflineFPlayer offlineFPlayer;
	private boolean forceJoin;

	public FactionPlayerJoinedEvent(OfflineFPlayer offlineFPlayer, Faction faction, boolean forceJoin) {
		super(faction);
		
		this.offlineFPlayer = offlineFPlayer;
		this.forceJoin = forceJoin;
	}

	public OfflineFPlayer getOfflineFPlayer() {
		return offlineFPlayer;
	}

	public boolean isForceJoin() {
		return forceJoin;
	}


}
