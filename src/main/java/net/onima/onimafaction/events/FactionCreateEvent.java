package net.onima.onimafaction.events;

import org.bukkit.entity.Player;

import net.onima.onimafaction.faction.PlayerFaction;

public class FactionCreateEvent extends FactionEvent {
	
	private Player player;

	public FactionCreateEvent(PlayerFaction faction, Player player) {
		super(faction);
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

}
