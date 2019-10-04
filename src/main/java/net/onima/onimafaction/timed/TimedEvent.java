package net.onima.onimafaction.timed;

import net.onima.onimaapi.utils.OSound;
import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.task.TimedTask;

public abstract class TimedEvent {
	
	protected static OnimaFaction plugin;
	protected static TimedTask runningTask;
	
	static {
		plugin = OnimaFaction.getInstance();
	}
	
	protected long time, delay;
	protected int[] delayedMessages;
	protected TimedTask timedTask;
	protected OSound oSoundStart, oSoundDelay, oSoundStop, oSoundCancel;
	protected String startMessage, delayMessage, stopMessage, cancelMessage;
	protected EventAction state;
	
	public TimedEvent(long time, long delay) {
		this.time = time;
		this.delay = delay;
	}
	
	public abstract void start(long time, long delay);
	public abstract void stop();
	public abstract void action(EventAction action);
	public abstract String getDelayScoreboardLine();
	public abstract String getRunningScoreboardLine();
	
	public void start() {
		start(time, delay);
	}
	
	public TimedTask getTask() {
		return timedTask;
	}
	
	public boolean isRunning() {
		return timedTask != null;
	}
	
	public long getTime() {
		return time;
	}
	
	public long getDelay() {
		return delay;
	}
	
	public int[] getDelayedMessages() {
		return delayedMessages;
	}
	
	public void setDelayedMessage(int[] delayedMessages) {
		this.delayedMessages = delayedMessages;
	}
	
	public OSound getOSound(EventAction action) {
		switch (action) {
		case DELAYED:
			return oSoundDelay;
		case STARTED:
			return oSoundStart;
		case STOPPED:
			return oSoundStop;
		case CANCELLED:
			return oSoundCancel;
		default:
			return null;
		}
	}
	
	public String getMessage(EventAction action) {
		switch (action) {
		case DELAYED:
			return delayMessage;
		case STARTED:
			return startMessage;
		case STOPPED:
			return stopMessage;
		case CANCELLED:
			return cancelMessage;
		default:
			return null;
		}
	}
	
	public static TimedTask getRunningTask() {
		return runningTask;
	}
	
	public static enum EventAction {
		
		STARTED,
		DELAYED,
		STOPPED,
		CANCELLED;

	}

}
