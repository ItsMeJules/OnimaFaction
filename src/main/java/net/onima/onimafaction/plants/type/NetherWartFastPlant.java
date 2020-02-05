package net.onima.onimafaction.plants.type;

import org.bukkit.Location;

import net.onima.onimaapi.utils.time.Time;
import net.onima.onimafaction.plants.FastPlant;
import net.onima.onimafaction.plants.PlantStage;
import net.onima.onimafaction.plants.PlantType;

public class NetherWartFastPlant extends FastPlant {

	public NetherWartFastPlant(Location plantLocation) {
		super(PlantType.NETHER_WART, plantLocation, 30 * Time.MINUTE);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void initData() {
		initialData = plantLocation.getBlock().getData();
		stage = PlantStage.fromOrdinal(PlantStage.NetherWartStage.SEEDED, initialData);
		harvestTime = (initialHarvestTime / stage.maxOrdinal()) * (stage.maxOrdinal() - stage.ordinal());
	}
	
}
