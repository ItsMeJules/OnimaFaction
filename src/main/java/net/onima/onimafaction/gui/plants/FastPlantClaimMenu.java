package net.onima.onimafaction.gui.plants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;

import com.google.common.collect.ImmutableMap;

import net.onima.onimaapi.gui.buttons.BackButton;
import net.onima.onimaapi.gui.buttons.MenuOpenerButton;
import net.onima.onimaapi.gui.buttons.utils.Button;
import net.onima.onimaapi.gui.menu.utils.PageMenu;
import net.onima.onimaapi.utils.BetterItem;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.plants.FastPlant;
import net.onima.onimafaction.plants.PlantType;

public class FastPlantClaimMenu extends PageMenu {
	
	private Claim claim;
	private PlantType plantType;

	public FastPlantClaimMenu(Claim claim) {
		super("fast_plant_claim", "§9FastPlants du claim", MAX_SIZE, false);
		
		this.claim = claim;
	}
	
	public FastPlantClaimMenu(Claim claim, PlantType plantType) {
		super("fast_plant_claim", "§9FastPlants du claim", MAX_SIZE, false);
		
		this.claim = claim;
		this.plantType = plantType;
	}
	
	@Override
	public Map<Integer, Button> getAllPagesItems() {
		Map<Integer, Button> buttons = new HashMap<>();
		
		for (FastPlant plant : claim.getFastPlants()) {
			if (plantType == null || plant.getPlantType() == plantType) {
				Location location = plant.getPlantLocation();
				String name = "§6" + plant.getPlantType().getNiceName();
				List<String> lore = Arrays.asList("§7" + ConfigurationService.STAIGHT_LINE,
						"§e" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ(),
						"§eMultiplicateur: §c" + plant.getMultiplier());
				
				buttons.put(buttons.size(), new MenuOpenerButton(new BetterItem(plant.getPlantType().getFinalMaterial(), 1, 0, name, lore), new FastPlantTargetMenu(plant, this)));
			}
		}

		return buttons;
	}
	
	@Override
	public Map<Integer, Button> getGlobalButtons() {
		return ImmutableMap.of(52, new BackButton(new PlantTypeClaimMenu(claim)));
	}

	@Override
	public int getMaxItemsPerPage() {
		return 51;
	}

}