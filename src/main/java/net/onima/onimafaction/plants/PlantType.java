package net.onima.onimafaction.plants;

import org.bukkit.Material;

public enum PlantType {
	
	PUMPKIN(Material.PUMPKIN_STEM, Material.PUMPKIN, "Citrouilles"),
	MELON(Material.MELON_STEM, Material.MELON_BLOCK, "Pastèques"),
	WHEAT(Material.CROPS, Material.WHEAT, "Blés"),
	POTATO(Material.POTATO, Material.POTATO_ITEM, "Patates"),
	CARROT(Material.CARROT, Material.CARROT_ITEM, "Carrotes"),
	SUGAR_CANE(Material.SUGAR_CANE_BLOCK, Material.SUGAR_CANE, "Cannes à sucre"),
	NETHER_WART(Material.NETHER_WARTS, Material.NETHER_STALK, "Nether wart");
	
	private Material plantMaterial;
	private Material finalMaterial;
	private String niceName;
	
	PlantType(Material plantMaterial, Material finalMaterial, String niceName) {
		this.plantMaterial = plantMaterial;
		this.finalMaterial = finalMaterial;
		this.niceName = niceName;
	}

	public Material getPlantMaterial() {
		return plantMaterial;
	}

	public Material getFinalMaterial() {
		return finalMaterial;
	}
	
	public String getNiceName() {
		return niceName;
	}

	public static PlantType fromSeed(Material material) {
		switch (material) {
		case PUMPKIN_STEM:
			return PUMPKIN;
		case MELON_STEM:
			return MELON;
		case CROPS:
			return WHEAT;
		case POTATO:
			return POTATO;
		case CARROT:
			return CARROT;
		case SUGAR_CANE_BLOCK:
			return SUGAR_CANE;
		case NETHER_WARTS:
			return NETHER_WART;
		default:
			return null;
		}
	}
	
	public static PlantType fromOrdinal(int ordinal) {
		switch (ordinal) {
		case 0:
			return PUMPKIN;
		case 1:
			return MELON;
		case 2:
			return WHEAT;
		case 3:
			return POTATO;
		case 4:
			return CARROT;
		case 5:
			return SUGAR_CANE;
		case 6:
			return NETHER_WART;
		default:
			return null;
		}
	}
	
}
