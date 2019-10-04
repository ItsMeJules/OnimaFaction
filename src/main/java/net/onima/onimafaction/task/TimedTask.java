package net.onima.onimafaction.task;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.timed.TimedEvent;
import net.onima.onimafaction.timed.TimedEvent.EventAction;

public class TimedTask extends BukkitRunnable {
	
	private long time, delay, initialTime;
	private TimedEvent timedEvent;
	
	public TimedTask(TimedEvent timedEvent, long time, long delay) {
		this.timedEvent = timedEvent;
		this.time = time / 1000;
		this.delay = delay / 1000;
		initialTime = time;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public long getDelay() {
		return delay;
	}

	public boolean isDelayed() {
		return delay >= 0;
	}
	
	public TimedEvent getTimedEvent() {
		return timedEvent;
	}
	
	@Override
	public void run() {
		if (delay >= 0) delay--;

		for (int i = 0; i < timedEvent.getDelayedMessages().length; i++) {
			if (delay == timedEvent.getDelayedMessages()[i]) {
				Methods.playServerSound(timedEvent.getOSound(EventAction.DELAYED));
				Bukkit.broadcastMessage(timedEvent.getMessage(EventAction.DELAYED).replace("%delay%", String.valueOf(delay)));
				timedEvent.action(EventAction.DELAYED);
			}
		}
		
		if (!isDelayed()) {
			if (time == initialTime) {
				Methods.playServerSound(timedEvent.getOSound(EventAction.STARTED));
				Bukkit.broadcastMessage(timedEvent.getMessage(EventAction.STARTED));
				timedEvent.action(EventAction.STARTED);
			}
			
			if (time > 0) time--;
			
			if (time <= 0) {
				Methods.playServerSound(timedEvent.getOSound(EventAction.STOPPED));
				Bukkit.broadcastMessage(timedEvent.getMessage(EventAction.STOPPED));
				timedEvent.action(EventAction.STOPPED);
				timedEvent.stop();
			}
		}
		
	}
	
}
