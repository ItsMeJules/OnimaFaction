package net.onima.onimafaction.plants.type;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import net.onima.onimafaction.plants.FastPlant;
import net.onima.onimafaction.plants.PlantStage;
import net.onima.onimafaction.plants.PlantType;

public class BambooFastPlant extends FastPlant {

	public BambooFastPlant(Location plantLocation) {
		super(PlantType.SUGAR_CANE, plantLocation, DEFAULT_HARVEST_TIME);
	}
	
	@Override
	public void initData() {
		initialData = (byte) PlantStage.BambooStage.SHORT.getId();
		
		for (byte i = 1; i < 4; ++i) {
			if (plantLocation.clone().add(0, i, 0).getBlock().getType() != Material.SUGAR_CANE_BLOCK) {
				initialData += i - 1;
				break;
			}
		}
		
		stage = PlantStage.fromId(initialData);
		harvestTime = (initialHarvestTime / stage.maxOrdinal()) * (stage.maxOrdinal() - stage.ordinal());
	}
	
	@Override
	public void grow() {
		Block next = plantLocation.clone().add(0, stage.ordinal(), 0).getBlock();
		
		if (next.getType() == Material.AIR)
			next.setType(Material.SUGAR_CANE_BLOCK);
		else
			initData();
	}
	
}
