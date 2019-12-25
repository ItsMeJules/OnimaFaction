package net.onima.onimafaction.events;

import org.bukkit.command.CommandSender;

import net.onima.onimafaction.events.FactionPlayerLeaveEvent.LeaveReason;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.players.OfflineFPlayer;

public class FactionPlayerLeftEvent extends FactionEvent {

	private OfflineFPlayer offlineFPlayer;
	private CommandSender kicker;
	private LeaveReason leaveReason;

	public FactionPlayerLeftEvent(OfflineFPlayer offlineFPlayer, Faction faction, LeaveReason leaveReason, CommandSender kicker) {
		super(faction);
		
		this.offlineFPlayer = offlineFPlayer;
		this.kicker = kicker;
		this.leaveReason = leaveReason;
	}

	public OfflineFPlayer getOfflineFPlayer() {
		return offlineFPlayer;
	}
	
	public CommandSender getKicker() {
		return kicker;
	}
	
	public LeaveReason getLeaveReason() {
		return leaveReason;
	}

}
