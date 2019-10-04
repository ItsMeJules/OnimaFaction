package net.onima.onimafaction.task;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.zone.type.Region;
import net.onima.onimafaction.events.FactionDTRChangeEvent;
import net.onima.onimafaction.events.FactionDTRChangeEvent.DTRChangeCause;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.faction.struct.DTRStatus;
import net.onima.onimafaction.players.FPlayer;

public class RegenerationTask extends BukkitRunnable {
	
	@Override
	public void run() {
		for (Faction faction : Faction.getFactions()) {
			if (faction instanceof PlayerFaction) {
				PlayerFaction pFac = (PlayerFaction) faction;
				
				if (pFac.getDTRStatut() == DTRStatus.REGENERATING) {
					if (pFac.getOnlineMembers(null).isEmpty()) continue;
					
					if (pFac.getDtrUpdateTime() > 0) {
						pFac.setDtrUpdateTime(pFac.getDtrUpdateTime() - 1);
						continue;
					} else {
						float newDTR = pFac.getDTR() + ConfigurationService.DTR_TO_ADD_PER_UPDATE * initMultiplier(pFac);
						FactionDTRChangeEvent event = new FactionDTRChangeEvent(faction, DTRChangeCause.REGENERATING, pFac.getDTR(), newDTR);
						Bukkit.getPluginManager().callEvent(event);
						
						if (event.isCancelled()) continue;
						
						pFac.setDTR(newDTR, DTRChangeCause.REGENERATING);
						pFac.setDtrUpdateTime(ConfigurationService.DTR_UPDATE_TIME);
					}
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

}
