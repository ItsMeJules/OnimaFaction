package net.onima.onimafaction.task;

import java.util.Iterator;

import org.bukkit.scheduler.BukkitRunnable;

import net.onima.onimafaction.plants.FastPlant;

public class FastPlantTask extends BukkitRunnable {

	@Override
	public void run() {
		Iterator<FastPlant> iterator = FastPlant.getFastPlants().iterator();
		
		while (iterator.hasNext()) {
			FastPlant plant = iterator.next();
			
			if (plant.getPlantLocation().getChunk().isLoaded())
				plant.initStage();
			
			if (!plant.canGrowFaster() && plant.isSaved())
				iterator.remove();
		}
	}

}
