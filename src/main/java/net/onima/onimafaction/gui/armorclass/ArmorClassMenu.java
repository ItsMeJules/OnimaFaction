package net.onima.onimafaction.gui.armorclass;

import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import net.onima.onimaapi.gui.PacketMenu;
import net.onima.onimaapi.gui.PacketStaticMenu;
import net.onima.onimaapi.gui.buttons.DisplayButton;
import net.onima.onimaapi.gui.buttons.utils.Button;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.utils.BetterItem;
import net.onima.onimafaction.armorclass.Archer;
import net.onima.onimafaction.armorclass.Bard;
import net.onima.onimafaction.armorclass.Mineur;
import net.onima.onimafaction.armorclass.Reaper;
import net.onima.onimafaction.armorclass.Rogue;

public class ArmorClassMenu extends PacketMenu implements PacketStaticMenu { //TODO Faire les items glowing sans montrer l'enchant

	public ArmorClassMenu() {
		super("armorclass", "§fListe des §2classes.", MIN_SIZE, true);
	}

	@Override
	public void registerItems() {
	}
	
	@Override
	public void setup() {
		inventory.clear();
		for (Entry<Integer, Button> entry : buttons.entrySet())
			inventory.setItem(entry.getKey(), createItemStack(null, entry.getValue()));		
	}
	
	public void registerClasses() {
		for (int i = 0; i < size; i++) {
			if (i % 2 == 1)
				buttons.put(i, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 15, "§r")));
		}
		
		BetterItem mineur = new BetterItem(Material.DIAMOND_PICKAXE, 1, 0, "§7Classe " + Mineur.NAME_COLOR + Mineur.NAME, "", "§aCliquez pour afficher les caractéristiques.");
		BetterItem bard = new BetterItem(Material.BLAZE_ROD, 1, 0, "§7Classe " + Bard.NAME_COLOR + Bard.NAME, "", "§aCliquez pour afficher les caractéristiques.");
		BetterItem archer = new BetterItem(Material.BOW, 1, 0, "§7Classe " + Archer.NAME_COLOR + Archer.NAME, "", "§aCliquez pour afficher les caractéristiques.");
		BetterItem reaper = new BetterItem(Material.QUARTZ, 1, 0, "§7Classe " + Reaper.NAME_COLOR + Reaper.NAME, "", "§aCliquez pour afficher les caractéristiques.");
		BetterItem rogue = new BetterItem(Material.GOLD_SWORD, 1, 0, "§7Classe " + Rogue.NAME_COLOR + Rogue.NAME, "", "§aCliquez pour afficher les caractéristiques.");
		
		mineur.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		bard.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		archer.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		reaper.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		rogue.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
		
		buttons.put(0, new ClassButton(PacketMenu.getMenu("mineurclass"), mineur));
		buttons.put(2, new ClassButton(PacketMenu.getMenu("bardclass"), bard));
		buttons.put(4, new ClassButton(PacketMenu.getMenu("archerclass"), archer));
		buttons.put(6, new ClassButton(PacketMenu.getMenu("reaperclass"), reaper));
		buttons.put(8, new ClassButton(PacketMenu.getMenu("rogueclass"), rogue));
		setup();
	}
	
	public class ClassButton extends DisplayButton {

		private PacketMenu menu;
		
		public ClassButton(PacketMenu menu, BetterItem item) {
			super(item);
			
			this.menu = menu;
		}
		
		@Override
		public void click(PacketMenu menu, Player clicker, ItemStack current, InventoryClickEvent event) {
			event.setCancelled(true);
			this.menu.open(APIPlayer.getByPlayer(clicker));
		}
		
	}

}
