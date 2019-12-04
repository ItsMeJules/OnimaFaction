package net.onima.onimafaction.task;

import java.util.List;

import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.TaskPerEntry;
import net.onima.onimaapi.zone.type.Region;
import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.events.FactionDTRChangeEvent.DTRChangeCause;
import net.onima.onimafaction.faction.EggAdvantage;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.faction.struct.DTRStatus;
import net.onima.onimafaction.faction.struct.EggAdvantageType;
import net.onima.onimafaction.players.FPlayer;

public class RegenerationEntryTask extends TaskPerEntry<PlayerFaction> {
	
	private static RegenerationEntryTask regenerationTask;

	public RegenerationEntryTask() {
		super(50);
	}

	@Override
	public void run(List<PlayerFaction> list) {
		for (PlayerFaction faction : list) {
			if (faction.getDTRStatut() == DTRStatus.REGENERATING) {
				if (faction.getOnlineMembers(null).isEmpty()) continue;
				
				if (faction.getDtrUpdateTime() > 0) {
					EggAdvantage egg = faction.getEggAdvantage(EggAdvantageType.DTR_REGEN);
					
					int updateTime = faction.getDtrUpdateTime();
					
					if (egg.getAmount() > 0)
						updateTime -= (egg.getAmount() - 1 + egg.getType().getChanger());
					else
						updateTime -= 1;
						
					faction.setDtrUpdateTime(updateTime);
					continue;
				} else {
					faction.setDTR(faction.getDTR() + ConfigurationService.DTR_TO_ADD_PER_UPDATE * initMultiplier(faction), DTRChangeCause.REGENERATING);
					faction.setDtrUpdateTime(ConfigurationService.DTR_UPDATE_TIME);
				}
			}
		}	
	}
	
	private float initMultiplier(PlayerFaction faction) {
		float multiplier = ConfigurationService.DTR_TO_MULTIPLIER_OUTSIDE_CLAIMS;
				
		for (FPlayer member : faction.getOnlineMembers(null)) {
			Region standing = member.getRegionOn();
			
			if (standing instanceof Claim) {
				Faction standingFaction = ((Claim) standing).getFaction();
						
				if (standingFaction.isSafeZone() || standingFaction.getName().equalsIgnoreCase(faction.getName())) {
					multiplier = 1;
					break;
				}
			}
		}
		return multiplier;
	}
	
	public static void init(OnimaFaction plugin) {
		regenerationTask = new RegenerationEntryTask();
		
		regenerationTask.task(task -> task.runTaskTimerAsynchronously(plugin, 40L, 20L));
		regenerationTask.insert(PlayerFaction.getPlayersFaction().values());
	}
	
	public static RegenerationEntryTask get() {
		return regenerationTask;
	}
}
