package net.onima.onimafaction.gui.armorclass;

import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import net.onima.onimaapi.gui.PacketMenu;
import net.onima.onimaapi.gui.PacketStaticMenu;
import net.onima.onimaapi.gui.buttons.BackButton;
import net.onima.onimaapi.gui.buttons.DisplayButton;
import net.onima.onimaapi.gui.buttons.utils.Button;
import net.onima.onimaapi.utils.BetterItem;
import net.onima.onimafaction.armorclass.Mineur;

public class MineurMenu extends PacketMenu implements PacketStaticMenu {

	public MineurMenu() {
		super("mineurclass", "§7Classe " + Mineur.NAME_COLOR + Mineur.NAME + ".", MIN_SIZE * 2, true);
	}

	@Override
	public void registerItems() {
		buttons.put(0, new DisplayButton(new ItemStack(Material.IRON_HELMET)));
		buttons.put(1, new DisplayButton(new ItemStack(Material.IRON_CHESTPLATE)));
		buttons.put(2, new DisplayButton(new ItemStack(Material.IRON_LEGGINGS)));
		buttons.put(3, new DisplayButton(new ItemStack(Material.IRON_BOOTS)));
		buttons.put(4, new DisplayButton(new BetterItem(Material.DIAMOND_PICKAXE, 1, 0, "§9Invisibilité", Lists.newArrayList("", "§7Si vous vous trouvez en-dessous de", "§7la couche §e20 §7vous bénéficierez d'un", "§7effet §2Invisibility infini §7tant que", "§7vous ne vous faites pas attaquer par un joueur."))));
		buttons.put(5, new DisplayButton(new BetterItem(Material.PAPER, 1, 0, "§c§oIl n'y a pas de restriction sur cette classe.")));
		buttons.put(6, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 7, "§6")));
		buttons.put(7, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 7, "§6")));
		buttons.put(8, new BackButton(PacketMenu.getMenu("armorclass")));
		buttons.put(9, new DisplayButton(new BetterItem(Material.BREWING_STAND_ITEM, 1, 0, "§2Effets", Lists.newArrayList("", "§7Effets :", "§f - §2Haste II", "§f - §2Fire Resistance I", "§f - §2Night Vision I", "", "§7Ces effets sont applicables seulement", "§7quand la classe est équipée."))));
		buttons.put(10, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 7, "§6")));
		buttons.put(11, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 7, "§6")));
		buttons.put(12, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 7, "§6")));
		buttons.put(13, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 7, "§6")));
		buttons.put(14, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 7, "§6")));
		buttons.put(15, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 7, "§6")));
		buttons.put(16, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 7, "§6")));
		buttons.put(17, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 7, "§6")));
	}
	
	@Override
	public void setup() {
		inventory.clear();
		for (Entry<Integer, Button> entry : buttons.entrySet())
			inventory.setItem(entry.getKey(), createItemStack(null, entry.getValue()));
	}
	
//	public MineurMenu() {
//		super(18, "mineurclass", "§7Classe " + Mineur.NAME_COLOR + Mineur.NAME + ".");
//		
//		mainPage = currentPage = new MainPage(this);
//		currentPage.registerItems();
//	}
//	
//	public class MainPage extends MenuPage {
//
//		public MainPage(Menu menu) {
//			super(menu);
//		}
//
//		@Override
//		public void registerItems() {
//			BetterItem invisibility = new BetterItem(Material.DIAMOND_PICKAXE, 1, 0, "§9Invisibilité", Lists.newArrayList("", "§7Si vous vous trouvez en-dessous de", "§7la couche §e20 §7vous bénéficierez d'un", "§7effet §2Invisibility infini §7tant que", "§7vous ne vous faites pas attaquer par un joueur."));
//			BetterItem effects = new BetterItem(Material.BREWING_STAND_ITEM, 1, 0, "§2Effets", Lists.newArrayList("", "§7Effets :", "§f - §2Haste II", "§f - §2Fire Resistance I", "§f - §2Night Vision I", "", "§7Ces effets sont applicables seulement", "§7quand la classe est équipée."));
//			BetterItem maxMineur = new BetterItem(Material.PAPER, 1, 0, "§c§oIl n'y a pas de restriction sur cette classe.");
//			
//			inventory.setItem(0, new ItemStack(Material.IRON_HELMET));
//			inventory.setItem(1, new ItemStack(Material.IRON_CHESTPLATE));
//			inventory.setItem(2, new ItemStack(Material.IRON_LEGGINGS));
//			inventory.setItem(3, new ItemStack(Material.IRON_BOOTS));
//			inventory.setItem(4, invisibility.toItemStack());
//			inventory.setItem(5, new BetterItem(Material.STAINED_GLASS_PANE, 1, 7, "§6").toItemStack());
//			inventory.setItem(6, new BetterItem(Material.STAINED_GLASS_PANE, 1, 7, "§6").toItemStack());
//			inventory.setItem(7, new BetterItem(Material.STAINED_GLASS_PANE, 1, 7, "§6").toItemStack());
//			inventory.setItem(8, new BetterItem(Material.STAINED_GLASS_PANE, 1, 7, "§6").toItemStack());
//			inventory.setItem(9, effects.toItemStack());
//			inventory.setItem(10, new BetterItem(Material.STAINED_GLASS_PANE, 1, 12, "§6").toItemStack());
//			inventory.setItem(11, new BetterItem(Material.STAINED_GLASS_PANE, 1, 7, "§6").toItemStack());
//			inventory.setItem(12, new BetterItem(Material.STAINED_GLASS_PANE, 1, 7, "§6").toItemStack());
//			inventory.setItem(13, new BetterItem(Material.STAINED_GLASS_PANE, 1, 7, "§6").toItemStack());
//			inventory.setItem(14, new BetterItem(Material.STAINED_GLASS_PANE, 1, 7, "§6").toItemStack());
//			inventory.setItem(15, new BetterItem(Material.STAINED_GLASS_PANE, 1, 12, "§6").toItemStack());
//			inventory.setItem(16, new BetterItem(Material.STAINED_GLASS_PANE, 1, 12, "§6").toItemStack());
//			inventory.setItem(17, maxMineur.toItemStack());
//		}
//
//		@Override
//		public void click(ItemStack item, Player player, InventoryClickEvent event) {
//			event.setCancelled(true);
//		}
//
//		@Override
//		public MenuPage next() {
//			// TODO Auto-generated method stub
//			return null;
//		}
//		
//	}
	
	
}
