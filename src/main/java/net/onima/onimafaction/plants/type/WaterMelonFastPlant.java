package net.onima.onimafaction.plants.type;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import net.onima.onimafaction.plants.FastPlant;
import net.onima.onimafaction.plants.PlantStage;
import net.onima.onimafaction.plants.PlantType;

public class WaterMelonFastPlant extends FastPlant {

	public WaterMelonFastPlant(Location plantLocation) {
		super(PlantType.MELON, plantLocation, DEFAULT_HARVEST_TIME);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void initData() {
		Block block = plantLocation.getBlock();
		initialData = (byte) (block.getData() + PlantStage.SeedBlockStage.SEEDED.getId());
		
		for (BlockFace face : new BlockFace[] {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}) {
			if (block.getRelative(face).getType() == Material.MELON_BLOCK) {
				initialData = (byte) PlantStage.SeedBlockStage.BLOCK_GROWN.getId();
				break;
			}
		}
		
		stage = PlantStage.fromId(initialData);
		harvestTime = (initialHarvestTime / stage.maxOrdinal()) * (stage.maxOrdinal() - stage.ordinal());
	}
	
	@Override
	public void grow() {
		if (stage.getId() == 19) {
			
			Block block = plantLocation.getBlock();
//			boolean reset = true;
			
			for (BlockFace face : new BlockFace[] {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}) {
				Block next = block.getRelative(face);
				
				if (next.getType() == Material.AIR && next.getRelative(BlockFace.DOWN).getType().isSolid()) {
					next.setType(Material.MELON_BLOCK);
//					reset = false;
					return;
				}
			}
			
//			if (reset)
//				initData();
			
		} else
			super.grow();
	}
	
}
