package net.onima.onimafaction.gui.plants;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.onima.onimaapi.gui.PacketMenu;
import net.onima.onimaapi.gui.buttons.utils.Button;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.utils.BetterItem;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.plants.PlantType;

public class PlantTypeClaimMenu extends PacketMenu {
	
	protected static Button splitter;
	
	static 	{
		splitter = new Button() {
			
			@Override
			public BetterItem getButtonItem(Player player) {
				return new BetterItem(Material.STAINED_GLASS_PANE, 1, 15, "§r");
			}
			
			@Override
			public void click(PacketMenu menu, Player clicker, ItemStack current, InventoryClickEvent event) {
				event.setCancelled(true);
			}
		};
	}

	private Claim claim;
	
	public PlantTypeClaimMenu(Claim claim) {
		super("plant_type_claim", "§9FastPlant du claim", MIN_SIZE * 3, true);
		
		this.claim = claim;
	}
	
	@Override
	public void registerItems() {
		for (int i = 0; i < PlantType.values().length; i++)
			buttons.put(i, new CropTypeButton(i));
			
		for (int i = 9; i < 18; i++)
			buttons.put(i, splitter);
		
		for (int i = 18; i < size; i++)
			buttons.put(i, splitter); //TODO A finir
	}
	
	public class CropTypeButton implements Button {
		
		private PlantType type;
		
		public CropTypeButton(int i) {
			type = PlantType.fromOrdinal(i);
		}

		@Override
		public BetterItem getButtonItem(Player player) {
			return new BetterItem(type.getFinalMaterial(), 1, 0, "§7Gérer les §e" + type.getNiceName() + " §7du claim.");
		}

		@Override
		public void click(PacketMenu menu, Player clicker, ItemStack current, InventoryClickEvent event) {
			APIPlayer.getPlayer(clicker).openMenu(new FastPlantClaimMenu(claim, type));
			event.setCancelled(true);
		}
		
	}
	
}