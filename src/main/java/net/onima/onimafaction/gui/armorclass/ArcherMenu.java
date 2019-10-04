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
import net.onima.onimafaction.armorclass.Archer;

public class ArcherMenu extends PacketMenu implements PacketStaticMenu {

	public ArcherMenu() {
		super("archerclass", "§7Classe " + Archer.NAME_COLOR + Archer.NAME + ".", MIN_SIZE * 2, true);
	}

	@Override
	public void registerItems() {
		buttons.put(0, new DisplayButton(new ItemStack(Material.LEATHER_HELMET)));
		buttons.put(1, new DisplayButton(new ItemStack(Material.LEATHER_CHESTPLATE)));
		buttons.put(2, new DisplayButton(new ItemStack(Material.LEATHER_LEGGINGS)));
		buttons.put(3, new DisplayButton(new ItemStack(Material.LEATHER_BOOTS)));
		buttons.put(4, new DisplayButton(new BetterItem(Material.BOW, 1, 0, "§eArcher tag", Lists.newArrayList("", "§7Vous pouvez tag un joueur jusqu'à", "§72 fois maximum, pour que cela", "§7fonctionne, vous devez charger", "§7votre arc au maximum.", "", "§eMark n°1 :", "§f - §cDégâts subit x2", "", "§eMark n°2 :", "§f - §cDégâts subit x3"))));
		buttons.put(5, new DisplayButton(new BetterItem(Material.PAPER, 1, 0, "§7Il ne peut y avoir que §c2 archer §7par §efaction§7.")));
		buttons.put(6, new DisplayButton(new BetterItem(Material.FEATHER, 1, 0, "§aJump buff", Lists.newArrayList("", "§7Lorsque vous cliquez dessus,", "§7vous recevrez un effet de", "§2Jump Boost IV §7pour §25 secondes§7.", "", "", "§c§oCe buff a un cooldown de 30 secondes."))));
		buttons.put(7, new DisplayButton(new BetterItem(Material.SUGAR, 1, 0, "§bSpeed buff", Lists.newArrayList("", "§7Lorsque vous cliquez dessus,", "§7vous recevrez un effet de", "§2Speed IV §7pour §25 secondes§7.", "", "", "§c§oCe buff a un cooldown de 30 secondes."))));
		buttons.put(8, new BackButton(PacketMenu.getMenu("armorclass")));
		buttons.put(9, new DisplayButton(new BetterItem(Material.BREWING_STAND_ITEM, 1, 0, "§2Effets", Lists.newArrayList("", "§7Effets :", "§f - §2Speed III", "§f - §2Resistance II", "", "§7Ces effets sont applicables seulement", "§7quand la classe est équipée."))));
		buttons.put(10, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 12, "§6")));
		buttons.put(11, new DisplayButton(new BetterItem(Material.INK_SACK, 1, 2, "§2Armure de poison", Lists.newArrayList("", "§7Si vous colorez votre armure", "§7seulement avec des colorants", "§7de cactus, vous aurez §e50%", "§7de chance lorsque vous taggez", "§7un joueur de lui infliger du", "§2Poison II §7pour §23 secondes§7.", "                                                 "))));
		buttons.put(12, new DisplayButton(new BetterItem(Material.INK_SACK, 1, 0, "§2Armure de wither", Lists.newArrayList("", "§7Si vous colorez votre armure", "§7seulement avec des colorants", "§7de poulpe, vous aurez §e30%", "§7de chance lorsque vous taggez", "§7un joueur de lui infliger du", "§2Wither I §7pour §26 secondes§7.", "                                                 "))));
		buttons.put(13, new DisplayButton(new BetterItem(Material.INK_SACK, 1, 4, "§2Armure de slowness", Lists.newArrayList("", "§7Si vous colorez votre armure", "§7seulement avec des colorants", "§7de lapis, vous aurez §e60%", "§7de chance lorsque vous taggez", "§7un joueur de lui infliger du", "§2Slowness II §7pour §23 secondes§7.", "                                                  "))));
		buttons.put(14, new DisplayButton(new BetterItem(Material.INK_SACK, 1, 8, "§2Armure de blindness", Lists.newArrayList("", "§7Si vous colorez votre armure", "§7seulement avec des colorants", "§7gris, vous aurez §e40%", "§7de chance lorsque vous taggez", "§7un joueur de lui infliger du", "§2Blindness I §7pour §26 secondes§7.", "                                                  "))));
		buttons.put(15, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 12, "§6")));
		buttons.put(16, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 12, "§6")));
		buttons.put(17, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 12, "§6")));
	}

	@Override
	public void setup() {
		inventory.clear();
		for (Entry<Integer, Button> entry : buttons.entrySet())
			inventory.setItem(entry.getKey(), createItemStack(null, entry.getValue()));
	}

}
