package net.onima.onimafaction.plants;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import net.onima.onimaapi.saver.Saver;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.time.Time;
import net.onima.onimafaction.faction.EggAdvantage;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.faction.struct.EggAdvantageType;

public class FastPlant implements Saver {
	
	//TODO Ajouter le check de la luminosité
	/*
	 * Faire un gui permettant de gérer les fast plant
	 * 1 Item qui permet de gérer toutes les fastplant du claim
	 * 1 Item qui permet de gérer des fastplant à la location (on peut essayer de les classer par taille de location (x + y + z = taille))
	 * 
	 * Quand on clique avec un item spécial sur une fastplant ça ouvre un menu pour gérer la taille du crop.
	 *  
	 *  Peut-être généraliser toutes les crops du claim pour définir leur vitesse de pousse?
	 */
	
	public static final long DEFAULT_HARVEST_TIME;
	
	protected static Set<FastPlant> plants;
	
	static {
		DEFAULT_HARVEST_TIME = Time.HOUR;
		plants = new HashSet<>();
	}
	
	protected long initiatedTime, initialHarvestTime, harvestTime;
	protected PlantType plantType;
	protected PlantStage stage;
	protected Location plantLocation;
	protected byte initialData;
	
	{
		initiatedTime = System.currentTimeMillis();
	}
	
	public FastPlant(PlantType plantType, Location plantLocation, long initialHarvestTime) {
		this.plantType = plantType;
		this.plantLocation = plantLocation;
		this.initialHarvestTime = initialHarvestTime;
		
		initData();
	}
	
	@SuppressWarnings("deprecation")
	public void initData() {
		initialData = plantLocation.getBlock().getData();
		stage = PlantStage.fromOrdinal(PlantStage.CropStage.GERMINATED, initialData);
		harvestTime = (initialHarvestTime / stage.maxOrdinal()) * (stage.maxOrdinal() - stage.ordinal());
	}

	public long getInitiatedTime() {
		return initiatedTime;
	}
	
	public long getHarvestTime() {
		return initiatedTime + (harvestTime / getMultiplier());
	}
	
	public long getNextStageTime() {
		if (stage.ordinal() == stage.maxOrdinal())
			return getHarvestTime();
		
		long timePerStage = initialHarvestTime / stage.maxOrdinal() / getMultiplier();
		
		return (getHarvestTime() - timePerStage * (stage.maxOrdinal() - (stage.ordinal() + 1)));
	}
	
	public PlantType getPlantType() {
		return plantType;
	}
	
	public void setStage(PlantStage stage) {
		this.stage = stage;
	}
	
	public PlantStage getStage() {
		return stage;
	}
	
	public Location getPlantLocation() {
		return plantLocation;
	}
	
	public void initStage() {
		if (System.currentTimeMillis() < getNextStageTime() && stage.ordinal() != stage.maxOrdinal())
			return;
		
		PlantStage oldStage = stage;
		stage = PlantStage.fromOrdinal(stage, stage.ordinal() + 1 > stage.maxOrdinal() ? stage.maxOrdinal() : stage.ordinal() + 1);
		
		if (oldStage != stage)
			grow();
	}
	
	@SuppressWarnings("deprecation")
	public void grow() {
		plantLocation.getBlock().setData((byte) stage.ordinal());
	}
	
//	public void drop(ItemStack... items) {
//	}
	
	public boolean canGrowFaster() {
		Block soil = plantLocation.clone().add(0, -1, 0).getBlock();
		
		for (BlockFace face : new BlockFace[] {BlockFace.NORTH, BlockFace.DOWN, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST}) {
			if (soil.getRelative(face).getType() == Material.DIAMOND_BLOCK)
				return true;
		}
		
		return false;
	}
	
	public int getMultiplier() {
		Claim claim = Claim.getClaimAt(plantLocation);
		
		if (!(claim.getFaction() instanceof PlayerFaction))
			return 1;
		
		EggAdvantage egg = ((PlayerFaction) claim.getFaction()).getEggAdvantage(EggAdvantageType.CROPS);
		
		return (int) (egg.getAmount() - 1 + egg.getType().getChanger());
	}
	
	@Override
	public boolean isSaved() {
		return plants.contains(this);
	}
	
	@Override
	public void remove() {
		plants.remove(this);
		
		Claim claim = Claim.getClaimAt(plantLocation);
		
		if (claim.getFaction().isNormal())
			claim.getFastPlants().remove(this);
	}
	
	@Override
	public void save() {
		plants.add(this);
		
		Claim claim = Claim.getClaimAt(plantLocation);
		
		if (claim.getFaction().isNormal())
			claim.getFastPlants().add(this);
		
//		OnimaAPI.getDistributor().get(OnimaFaction.getInstance().getWorkloadManager().getPlantId()).addWorkload(new FastPlantWorkload(this));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((plantLocation == null) ? 0 : plantLocation.hashCode());
		result = prime * result + ((plantType == null) ? 0 : plantType.hashCode());
		result = prime * result + ((stage == null) ? 0 : stage.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		FastPlant other = (FastPlant) obj;
		
		if (plantLocation == null) {
			if (other.plantLocation != null)
				return false;
		} else if (!plantLocation.equals(other.plantLocation))
			return false;
		
		if (plantType != other.plantType)
			return false;
		
		if (stage == null) {
			if (other.stage != null)
				return false;
			
		} else if (!stage.equals(other.stage))
			return false;
		
		return true;
	}

	public static Set<FastPlant> getFastPlants() {
		return plants;
	}

	public static FastPlant getByLocation(Location location) {
		for (FastPlant plant : plants) {
			if (Methods.locationEquals(plant.plantLocation, location))
				return plant;
		}
		
		return null;
	}

}
