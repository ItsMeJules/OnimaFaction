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
import net.onima.onimafaction.armorclass.Bard;

public class BardMenu extends PacketMenu implements PacketStaticMenu {

	public BardMenu() {
		super("bardclass", "§7Classe " + Bard.NAME_COLOR + Bard.NAME + ".", MIN_SIZE * 2, true);
	}

	@Override
	public void registerItems() {
		for (int i = 5; i < 8; i++)
			buttons.put(i, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 4, "§6")));
		
		buttons.put(0, new DisplayButton(new ItemStack(Material.GOLD_HELMET)));
		buttons.put(1, new DisplayButton(new ItemStack(Material.GOLD_CHESTPLATE)));
		buttons.put(2, new DisplayButton(new ItemStack(Material.GOLD_LEGGINGS)));
		buttons.put(3, new DisplayButton(new ItemStack(Material.GOLD_BOOTS)));
		buttons.put(4, new DisplayButton(new BetterItem(Material.PAPER, 1, 0, "§7Il ne peut y avoir que §c2 bard §7par §efaction§7.")));
		
		buttons.put(8, new BackButton(PacketMenu.getMenu("armorclass")));
		buttons.put(9, new DisplayButton(new BetterItem(Material.BREWING_STAND_ITEM, 1, 0, "§2Effets", Lists.newArrayList("", "§7Effets :", "§f - §2Speed II", "§f - §2Régénération II", "§f - §2Resistance I", "", "§7Ces effets sont applicables seulement", "§7quand la classe est équipée."))));
		buttons.put(10, new DisplayButton(new BetterItem(Material.WHEAT, 1, 0, "§e§lSaturation", Lists.newArrayList("", "§6Effets :", " §e» §2Saturation I §7pour §25 secondes §7(quand tenu)", " §e» §2Saturation II §7pour §210 secondes §7(quand cliqué)", "", "§3Cet item utilise §e20 §3de power quand cliqué."))));
		buttons.put(11, new DisplayButton(new BetterItem(Material.BLAZE_ROD, 1, 0, "§c§lForce", Lists.newArrayList("", "§6Effets :", " §e» §2Strength I §7pour §25 secondes §7(quand tenu)", " §e» §2Strength II §7pour §25 secondes §7(quand cliqué)", "", "§3Cet item utilise §e45 §3de power quand cliqué."))));
		buttons.put(12, new DisplayButton(new BetterItem(Material.SUGAR, 1, 0, "§b§lSpeed", Lists.newArrayList("", "§6Effets :", " §e» §2Speed II §7pour §25 secondes §7(quand tenu)", " §e» §2Speed III §7pour §25 secondes §7(quand cliqué)", "", "§3Cet item utilise §e30 §3de power quand cliqué."))));
		buttons.put(13, new DisplayButton(new BetterItem(Material.FEATHER, 1, 0, "§a§lJump", Lists.newArrayList("", "§6Effets :", " §e» §2Jump Boost II §7pour §25 secondes §7(quand tenu)", " §e» §2Jump Boost VI §7pour §210 secondes §7(quand cliqué)", "", "§3Cet item utilise §e20 §3de power quand cliqué."))));
		buttons.put(14, new DisplayButton(new BetterItem(Material.IRON_INGOT, 1, 0, "§7§lRésistance", Lists.newArrayList("", "§6Effets :", " §e» §2Resistance I §7pour §25 secondes §7(quand tenu)", " §e» §2Resistance III §7pour §25 secondes §7(quand cliqué)", "", "§3Cet item utilise §e25 §3de power quand cliqué."))));
		buttons.put(15, new DisplayButton(new BetterItem(Material.MAGMA_CREAM, 1, 0, "§6§lFire Résistance", Lists.newArrayList("", "§6Effets :", " §e» §2Fire Résistance I §7pour §25 secondes §7(quand tenu)", " §e» §2Fire Résistance I §7pour §220 secondes §7(quand cliqué)", "", "§3Cet item utilise §e15 §3de power quand cliqué."))));
		buttons.put(16, new DisplayButton(new BetterItem(Material.GHAST_TEAR, 1, 0, "§d§lRégénération", Lists.newArrayList("", "§6Effets :", " §e» §2Regeneration I §7pour §25 secondes §7(quand tenu)", " §e» §2Regeneration II §7pour §210 secondes §7(quand cliqué)", "", "§3Cet item utilise §e30 §3de power quand cliqué."))));
		buttons.put(17, new DisplayButton(new BetterItem(Material.FERMENTED_SPIDER_EYE, 1, 0, "§8§lWither", Lists.newArrayList("", "§6Effets :", " §e» §7Aucun (quand tenu)", " §e» §2Wither II §7pour §25 secondes §7(quand cliqué)", "", "§3Cet item utilise §e35 §3de power quand cliqué."))));

	}
	
	@Override
	public void setup() {
		inventory.clear();
		for (Entry<Integer, Button> entry : buttons.entrySet())
			inventory.setItem(entry.getKey(), createItemStack(null, entry.getValue()));
	}

}