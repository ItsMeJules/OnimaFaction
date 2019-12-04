package net.onima.onimafaction.timed.event;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.OSound;
import net.onima.onimaapi.utils.time.Time;
import net.onima.onimafaction.events.FactionDTRChangeEvent.DTRChangeCause;
import net.onima.onimafaction.events.server_event.FactionEventServerStartEvent;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.task.TimedTask;
import net.onima.onimafaction.timed.FactionServerEvent;
import net.onima.onimafaction.timed.TimedEvent;

public class Raidable extends TimedEvent implements FactionServerEvent {
	
	{
		delayedMessages = new int[] {120, 60, 30, 20, 15, 3, 2, 1};
		oSoundDelay = new OSound(Sound.WITHER_DEATH, 2.0F, 10F);
		oSoundStart = new OSound(Sound.WITHER_DEATH, 2.0F, 10F);
		oSoundStop = new OSound(Sound.WITHER_DEATH, 2.0F, 10F);
		oSoundCancel = new OSound(Sound.WITHER_DEATH, 2.0F, 10F);
		
		delayMessage = "§6§lL'event raidable va commencer dans %delay%s";
		startMessage = "\n"
				+ "§6§m------------------------------§r"
				+ "\n"
				+ "\n       §6§lRAIDABLE EVENT ACTIF !"
				+ "\n"
				+ "\n§6§m------------------------------§r"
				+ "\n";
		stopMessage = "\n"
				+ "§6§m------------------------------§r"
				+ "\n"
				+ "\n     §6§lFIN DU RAIDABLE EVENT !"
				+ "\n"
				+ "\n§6§m------------------------------§r"
				+ "\n";
		cancelMessage = "§6§lL'event raidable a été arrêté par %player%";
	}
	
	public Raidable(long time, long delay) {
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
			Faction.getFactions().parallelStream().filter(faction -> faction instanceof PlayerFaction).forEach(faction -> {
				PlayerFaction pFaction = (PlayerFaction) faction;
				
				pFaction.setDTR(ConfigurationService.MIN_DTR, DTRChangeCause.PLUGIN);
				pFaction.setRegenCooldown(10 * Time.HOUR);
			});
			break;
		case STOPPED:
		case CANCELLED:
			if (state == EventAction.STARTED) {
				Faction.getFactions().parallelStream().filter(faction -> faction instanceof PlayerFaction).forEach(faction -> {
					PlayerFaction pFaction = (PlayerFaction) faction;
					
					pFaction.setDTR(pFaction.getMaxDTR(), DTRChangeCause.PLUGIN);
					pFaction.setRegenCooldown(0);
				});
			}
			
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
		return "§6Raidable dans : §c";
	}
	
	@Override
	public String getRunningScoreboardLine() {
		return "§6Raidable : §c";
	}
	
}
