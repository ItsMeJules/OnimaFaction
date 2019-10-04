package net.onima.onimafaction.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;

public class FactionDisbandEvent extends FactionEvent implements Cancellable {

	private boolean cancel;
	private Player player;

	public FactionDisbandEvent(Faction faction, Player player) {
		super(faction);
		this.player = player;
	}
	
	@Override
	public PlayerFaction getFaction() {
		return (PlayerFaction) super.getFaction();
	}
	
	public Player getPlayer() {
		return player;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

}
