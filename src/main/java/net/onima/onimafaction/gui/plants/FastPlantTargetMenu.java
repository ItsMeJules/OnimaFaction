package net.onima.onimafaction.gui.plants;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;

import net.onima.onimaapi.gui.PacketMenu;
import net.onima.onimaapi.gui.PacketMenuType;
import net.onima.onimaapi.gui.buttons.BackButton;
import net.onima.onimaapi.gui.buttons.DisplayButton;
import net.onima.onimaapi.utils.BetterItem;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.plants.FastPlant;

public class FastPlantTargetMenu extends PacketMenu {

	private FastPlant plant;
	private PacketMenu backMenu;

	public FastPlantTargetMenu(FastPlant plant) {
		super("fast_plant_target", "§8§o" + plant.getPlantType().getNiceName(), PacketMenuType.HOPPER, false);
		
		this.plant = plant;
	}
	
	public FastPlantTargetMenu(FastPlant plant, PacketMenu backMenu) {
		super("fast_plant_target", "§8§o" + plant.getPlantType().getNiceName(), PacketMenuType.HOPPER, false);
		
		this.plant = plant;
		this.backMenu = backMenu;
	}

	@Override
	public void registerItems() {
		Location location = plant.getPlantLocation();
		String name = "§6" + plant.getPlantType().getNiceName();
		List<String> lore = Arrays.asList("§7" + ConfigurationService.STAIGHT_LINE,
				"§e" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ(),
				"§eMultiplicateur: §c" + plant.getMultiplier());
		
		buttons.put(0, new DisplayButton(new BetterItem(plant.getPlantType().getFinalMaterial(), 1, 0, name, lore)));
		buttons.put(1, new DisplayButton(new BetterItem(Material.IRON_HOE, 1, 0, "§6A récolter le :", "§c" + Methods.toFormatDate(plant.getHarvestTime(), ConfigurationService.DATE_FORMAT_HOURS))));
		buttons.put(2, new DisplayButton(new BetterItem(Material.BOOK, 1, 0, "§6Initié le :", "§c" + Methods.toFormatDate(plant.getInitiatedTime(), ConfigurationService.DATE_FORMAT_HOURS))));
		buttons.put(3, new DisplayButton(new BetterItem(Material.INK_SACK, 1, 15, "§6Prochaine pousse :", "§c" + Methods.toFormatDate(plant.getNextStageTime(), ConfigurationService.DATE_FORMAT_HOURS))));
		
		if (backMenu != null)
			buttons.put(4, new BackButton(backMenu));
	}
	
}
