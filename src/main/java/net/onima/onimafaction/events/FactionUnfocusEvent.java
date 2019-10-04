package net.onima.onimafaction.events;

import org.bukkit.OfflinePlayer;

import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.players.FPlayer;

public class FactionUnfocusEvent extends FactionFocusEvent {

	public FactionUnfocusEvent(FPlayer fPlayer, PlayerFaction faction, OfflinePlayer focused) {
		super(fPlayer, faction, focused);
	}

}
