package net.onima.onimafaction.timed.event;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitTask;

import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.OSound;
import net.onima.onimaapi.utils.time.Time;
import net.onima.onimaapi.zone.struct.Flag;
import net.onima.onimaapi.zone.type.Region;
import net.onima.onimafaction.events.FactionDTRChangeEvent.DTRChangeCause;
import net.onima.onimafaction.events.server_event.FactionEventServerStartEvent;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.task.TimedTask;
import net.onima.onimafaction.timed.FactionServerEvent;
import net.onima.onimafaction.timed.TimedEvent;

public class EOTW extends TimedEvent implements FactionServerEvent {
	
	private BukkitTask task;
	
	{
		delayedMessages = new int[] {120, 60, 30, 20, 15, 3, 2, 1};
		oSoundDelay = new OSound(Sound.WITHER_DEATH, 2.0F, 10F);
		oSoundStart = new OSound(Sound.WITHER_DEATH, 2.0F, 10F);
		oSoundStop = new OSound(Sound.WITHER_DEATH, 2.0F, 10F);
		oSoundCancel = new OSound(Sound.WITHER_DEATH, 2.0F, 10F);
		
		delayMessage = "§c§lL'EOTW va commencer dans %delay%s";
		startMessage = "\n"
				+ "§4§m------------------------------§r"
				+ "\n"
				+ "\n             §c§lEOTW ACTIF !"
				+ "\n"
				+ "\n§4§m------------------------------§r"
				+ "\n";
		stopMessage = "\n"
				+ "§4§m------------------------------§r"
				+ "\n"
				+ "\n           §c§lFIN DE L'EOTW!"
				+ "\n"
				+ "\n§4§m------------------------------§r"
				+ "\n";
		cancelMessage = "§c§oL'EOTW a été arrêté par %player%";
	}
	
	public EOTW(long time, long delay) {
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
			Faction.getFactions().stream().forEach(faction -> {
				if (faction instanceof PlayerFaction) {
					PlayerFaction pFaction = (PlayerFaction) faction;
					
					pFaction.setDTR(ConfigurationService.MIN_DTR, DTRChangeCause.PLUGIN);
					pFaction.setRegenCooldown(10 * Time.HOUR);	
				}

				faction.setFlags(new Flag[0]);
			});
			
			for (Claim claim : Claim.getClaims())
				claim.setDeathban(true);
			
			for (Region region : Region.getRegions())
				region.setDeathban(true);
			
			task = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> plugin.getBattleRoyale().start(), (timedTask.getTime() - plugin.getBattleRoyale().phasesDuration() / 1000) *20);
			break;
		case STOPPED:
			
			break;
		case CANCELLED:
			if (state == EventAction.STARTED) {
				Faction.getFactions().parallelStream().filter(faction -> faction instanceof PlayerFaction).forEach(faction -> {
					PlayerFaction pFaction = (PlayerFaction) faction;
					
					pFaction.setDTR(pFaction.getMaxDTR(), DTRChangeCause.PLUGIN);
					pFaction.setRegenCooldown(0);
				});
				
				task.cancel();
				task = null;
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
		return "§4EOTW dans §6: §c";
	}
	
	@Override
	public String getRunningScoreboardLine() {
		return "§4EOTW §6: §c";
	}

}
