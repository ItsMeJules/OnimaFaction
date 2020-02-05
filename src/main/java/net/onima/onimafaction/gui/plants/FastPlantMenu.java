package net.onima.onimafaction.gui.plants;

import java.util.Arrays;

import org.bukkit.Material;

import net.onima.onimaapi.gui.PacketMenu;
import net.onima.onimaapi.gui.buttons.MenuOpenerButton;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.BetterItem;
import net.onima.onimafaction.faction.claim.Claim;

public class FastPlantMenu extends PacketMenu {
	
	private Claim claim;

	public FastPlantMenu(Claim claim) {
		super("fast_plant", "§9FastPlant", MAX_SIZE, false);
		
		this.claim = claim;
		
		permission = OnimaPerm.ONIMAFACTION_FASTPLANT_COMMAND;
	}

	@Override
	public void registerItems() {
		buttons.put(getSlot(4, 2), new MenuOpenerButton(new BetterItem(Material.GOLD_HOE, 1, 0, "§e§lGérer toutes les fastplant d'un claim.",
				Arrays.asList("§7Vous permet de définir la taille ou de définir",
						"§7quand il faut récolter les plantation.")), new PlantTypeClaimMenu(claim)));
		buttons.put(getSlot(4, 5), new MenuOpenerButton(new BetterItem(Material.WHEAT, 1, 0, "§6§lGérer des fastplant à la location.",
				Arrays.asList("§7Vous permet de définir la taille ou de définir",
						"§7quand il faut récolter une plantation.")), new FastPlantClaimMenu(claim)));
	}
	
}
