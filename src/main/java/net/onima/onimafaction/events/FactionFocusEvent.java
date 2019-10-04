package net.onima.onimafaction.events;

import org.bukkit.OfflinePlayer;

import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.players.FPlayer;

public class FactionFocusEvent extends FactionEvent {
	
	private FPlayer fPlayer;
	private OfflinePlayer focused;

	public FactionFocusEvent(FPlayer fPlayer, PlayerFaction faction, OfflinePlayer focused) {
		super(faction);
		this.fPlayer = fPlayer;
		this.focused = focused;
	}

	public FPlayer getFPlayer() {
		return fPlayer;
	}

	public OfflinePlayer getFocused() {
		return focused;
	}

}
