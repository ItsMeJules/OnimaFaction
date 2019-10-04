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
import net.onima.onimafaction.armorclass.Rogue;

public class RogueMenu extends PacketMenu implements PacketStaticMenu {
	
	public RogueMenu() {
		super("rogueclass", "§7Classe " + Rogue.NAME_COLOR + Rogue.NAME + ".", MIN_SIZE * 2, true);
		
	}
	
	@Override
	public void registerItems() {
		buttons.put(0, new DisplayButton(new ItemStack(Material.CHAINMAIL_HELMET)));
		buttons.put(1, new DisplayButton(new ItemStack(Material.CHAINMAIL_CHESTPLATE)));
		buttons.put(2, new DisplayButton(new ItemStack(Material.CHAINMAIL_LEGGINGS)));
		buttons.put(3, new DisplayButton(new ItemStack(Material.CHAINMAIL_BOOTS)));
		buttons.put(4, new DisplayButton(new BetterItem(Material.GOLD_SWORD, 1, 0, "§3Backstab", Lists.newArrayList("", "§7Si vous réussissez à mettre", "§7un coup d'épée en or à un joueur", "§7vous multiplierez alors les dégâts", "§7infligés par §c4§7.", "", "", "§7Au cours de cette action, vous perdrez votre épée."))));
		buttons.put(5, new DisplayButton(new BetterItem(Material.PAPER, 1, 0, "§7Il ne peut y avoir que §c1 rogue §7par §efaction§7.")));
		buttons.put(6, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 11, "§6")));
		buttons.put(7, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 11, "§6")));
		buttons.put(8, new BackButton(PacketMenu.getMenu("armorclass")));
		buttons.put(9, new DisplayButton(new BetterItem(Material.BREWING_STAND_ITEM, 1, 0, "§2Effets", Lists.newArrayList("", "§7Effets :", "§f - §2Speed II", "§f - §2Regeneration II", "§f - §2Jump Boost IV", "", "§7Ces effets sont applicables seulement", "§7quand la classe est équipée."))));
		buttons.put(10, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 11, "§6")));
		buttons.put(11, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 11, "§6")));
		buttons.put(12, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 11, "§6")));
		buttons.put(13, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 11, "§6")));
		buttons.put(14, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 11, "§6")));
		buttons.put(15, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 11, "§6")));
		buttons.put(16, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 11, "§6")));
		buttons.put(17, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 11, "§6")));
	}
	
	@Override
	public void setup() {
		inventory.clear();
		for (Entry<Integer, Button> entry : buttons.entrySet())
			inventory.setItem(entry.getKey(), createItemStack(null, entry.getValue()));
	}
	
}
