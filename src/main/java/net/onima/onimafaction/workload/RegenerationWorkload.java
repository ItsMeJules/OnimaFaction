package net.onima.onimafaction.workload;

import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.workload.Workload;
import net.onima.onimaapi.zone.type.Region;
import net.onima.onimafaction.events.FactionDTRChangeEvent.DTRChangeCause;
import net.onima.onimafaction.faction.EggAdvantage;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.faction.struct.DTRStatus;
import net.onima.onimafaction.faction.struct.EggAdvantageType;
import net.onima.onimafaction.players.FPlayer;

public class RegenerationWorkload implements Workload {
	
	private PlayerFaction faction;
	
	public RegenerationWorkload(PlayerFaction faction) {
		this.faction = faction;
	}

	@Override
	public void compute() {
		if (faction.getDTRStatut() == DTRStatus.REGENERATING && !faction.getOnlineMembers(null).isEmpty()) {
			if (faction.getDtrUpdateTime() > 0) {
				EggAdvantage egg = faction.getEggAdvantage(EggAdvantageType.DTR_REGEN);
				
				int updateTime = faction.getDtrUpdateTime();
				
				if (egg.getAmount() > 0)
					updateTime -= (egg.getAmount() - 1 + egg.getType().getChanger());
				else
					updateTime -= 1;
					
				faction.setDtrUpdateTime(updateTime);
			} else {
				faction.setDTR(faction.getDTR() + ConfigurationService.DTR_TO_ADD_PER_UPDATE * initMultiplier(), DTRChangeCause.REGENERATING);
				faction.setDtrUpdateTime(ConfigurationService.DTR_UPDATE_TIME);
			}
		}
	}
	
	@Override
	public boolean reschedule() {
		return faction.getDTRStatut() != DTRStatus.FULL && !faction.shouldDelete();
	}
	
	private float initMultiplier() {
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
	
}
