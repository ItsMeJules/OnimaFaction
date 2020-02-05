package net.onima.onimafaction.workload.manager;

import net.onima.onimaapi.workload.manager.WorkloadDistributor;
import net.onima.onimaapi.workload.manager.WorkloadThread;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.DTRStatus;
import net.onima.onimafaction.workload.RegenerationWorkload;

public class WorkloadManager {
	
	private WorkloadDistributor distributor;
	
	private int regenerationId;
	
	public WorkloadManager(WorkloadDistributor distributor) {
		this.distributor = distributor;
	}
	
	public void registerWorkloads() {
		WorkloadThread regeneration = distributor.newThread(10);
		
		regenerationId = regeneration.getId();
		
		PlayerFaction.getPlayersFaction().values().forEach(faction -> {
			if (faction.getDTRStatut() != DTRStatus.FULL)
				regeneration.addWorkload(new RegenerationWorkload(faction));
		});
		
		distributor.startPerSecond(regenerationId, true, 1L);
	}
	
	public int getRegenerationId() {
		return regenerationId;
	}
	
}
