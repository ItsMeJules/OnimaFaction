package net.onima.onimafaction.events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;

import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.players.OfflineFPlayer;

public class FactionPlayerLeaveEvent extends FactionEvent implements Cancellable {

	private OfflineFPlayer offlineFPlayer;
	private CommandSender kicker;
	private LeaveReason leaveReason;
	private boolean cancelled;

	public FactionPlayerLeaveEvent(OfflineFPlayer offlineFPlayer, Faction faction, LeaveReason leaveReason, CommandSender kicker) {
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

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	public static enum LeaveReason {
		LEAVE,
		KICK,
		DISBAND;
	}

}
