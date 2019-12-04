package net.onima.onimafaction.timed.event;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.OSound;
import net.onima.onimafaction.events.server_event.FactionEventServerStartEvent;
import net.onima.onimafaction.task.TimedTask;
import net.onima.onimafaction.timed.FactionServerEvent;
import net.onima.onimafaction.timed.TimedEvent;

public class SOTW extends TimedEvent implements FactionServerEvent {
	
	{
		delayedMessages = new int[] {120, 60, 30, 20, 15, 3, 2, 1};
		oSoundDelay = new OSound(Sound.WITHER_DEATH, 2.0F, 10F);
		oSoundStart = new OSound(Sound.WITHER_DEATH, 2.0F, 10F);
		oSoundStop = new OSound(Sound.WITHER_DEATH, 2.0F, 10F);
		oSoundCancel = new OSound(Sound.WITHER_DEATH, 2.0F, 10F);
		
		delayMessage = "§a§lLe SOTW va commencer dans %delay%s";
		startMessage = "\n"
				+ "§2§m------------------------------§r"
				+ "\n"
				+ "\n             §a§lSOTW ACTIF !"
				+ "\n"
				+ "\n§2§m------------------------------§r"
				+ "\n";
		stopMessage = "\n"
				+ "§2§m------------------------------§r"
				+ "\n"
				+ "\n   §a§lFIN DU SOTW BONNE CHANCE !"
				+ "\n"
				+ "\n§2§m------------------------------§r"
				+ "\n";
		cancelMessage = "§a§oLe SOTW a été arrêté par %player%";
	}
	
	public SOTW(long time, long delay) {
		super(time, delay);
	}

	@Override
	public void start(long time, long delay, String starterName) {
		FactionEventServerStartEvent event = new FactionEventServerStartEvent(this);
		Bukkit.getPluginManager().callEvent(event);
		
		if (event.isCancelled())
			return;
		
		timedTask = new TimedTask(this, time, delay);
		super.starterName = starterName;
		
		timedTask.runTaskTimer(plugin, 0L, 20L);
		runningTask = timedTask;
		plugin.setFactionServerEvent(this);
	}

	@Override
	public void stop() {
		timedTask.cancel();
		runningTask = null;
		timedTask = null;
		starterName = null;
		plugin.setFactionServerEvent(null);
	}
	
	@Override
	public void action(EventAction action) {
		switch (action) {
		case DELAYED:
			break;
		case STARTED:
			break;
		case STOPPED:
			break;
		case CANCELLED:
			Methods.playServerSound(oSoundCancel);
			
			stop();
			break;
		default:
			break;
		}
		
		state = action;
	}
	
	@Override
	public String getDelayScoreboardLine() {
		return "§2SOTW dans §6: §c";
	}
	
	@Override
	public String getRunningScoreboardLine() {
		return "§2SOTW §6: §c";
	}

}
