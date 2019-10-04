package net.onima.onimafaction.events;

import org.bukkit.event.Cancellable;

import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.Relation;

public class FactionRelationCreateEvent extends FactionEvent implements Cancellable {

	private PlayerFaction targetFaction;
	private Relation relation;
	private boolean cancel;

	public FactionRelationCreateEvent(PlayerFaction faction, PlayerFaction targetFaction, Relation relation) {
		super(faction);
		
		this.targetFaction = targetFaction;
		this.relation = relation;
	}
	
	public PlayerFaction getTargetFaction() {
		return targetFaction;
	}

	public Relation getRelation() {
		return relation;
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
