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
import net.onima.onimafaction.armorclass.Reaper;

public class ReaperMenu extends PacketMenu implements PacketStaticMenu {

	public ReaperMenu() {
		super("reaperclass", "§7Classe " + Reaper.NAME_COLOR + Reaper.NAME + ".", MIN_SIZE * 2, true);
	}

	@Override
	public void registerItems() {
		buttons.put(0, new DisplayButton(new ItemStack(Material.GOLD_HELMET)));
		buttons.put(1, new DisplayButton(new ItemStack(Material.IRON_CHESTPLATE)));
		buttons.put(2, new DisplayButton(new ItemStack(Material.GOLD_LEGGINGS)));
		buttons.put(3, new DisplayButton(new ItemStack(Material.IRON_BOOTS)));
		buttons.put(4, new DisplayButton(new BetterItem(Material.QUARTZ, 1, 0, "§eChangeur de mode", Lists.newArrayList("", "§7Vous pouvez changer de mode", "§7en cliquant sur un quartz.", "", "§7Vous passerez d'abord en mode §5furtif", "§7puis en mode §cpuissance §7pour enfin", "§7retourner en mode §apassif§7, votre mode de base."))));
		buttons.put(5, new DisplayButton(new BetterItem(Material.PAPER, 1, 0, "§7Il ne peut y avoir que §c2 reaper §7par §efaction§7.")));
		buttons.put(6, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 14, "§6")));
		buttons.put(7, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 14, "§6")));
		buttons.put(8, new BackButton(PacketMenu.getMenu("armorclass")));
		buttons.put(9, new DisplayButton(new BetterItem(Material.BREWING_STAND_ITEM, 1, 0, "§2Effets", Lists.newArrayList("", "§7Effets :", "§f - §2Speed II", "§f - §2Fire Resistance I", "§f - §2Resistance II", "", "§7Ces effets sont applicables seulement", "§7quand la classe est équipée."))));
		buttons.put(10, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 14, "§6")));
		buttons.put(11, new DisplayButton(new BetterItem(Material.GLASS, 1, 0, "§7Mode : §5Furtif", Lists.newArrayList("", "§7Quand vous passez en mode §5furtif", "§7vous devenez totalement invisible (armure aussi)", "", "§7Vous bénéficiez des effets suivants :", " §e» §2Speed III §7pour §27 secondes", " §e» §2Absorption I §7pour §27 secondes", " §e» §2Resistance II §7pour §27 secondes", "", "§7Si vous prenez des dégâts", "§7tous les joueurs dans un rayon de", "§725 blocks en seront alertés.", "", "§cAttention, si vous attaquez un joueur,", "§cvous passerez de force en mode §lpuissance§c."))));
		buttons.put(12, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 14, "§6")));
		buttons.put(13, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 14, "§6")));
		buttons.put(14, new DisplayButton(new BetterItem(Material.BLAZE_POWDER, 1, 0, "§7Mode : §cPuissance", Lists.newArrayList("", "§7Quand vous passez en mode §cpuissance", "§7vous faites beaucoup de dégâts", "", "§7Vous bénéficiez des effets suivants :", " §e» §2Speed I §7pour §27 secondes", " §e» §2Absorption III §7pour §27 secondes", " §e» §2Strength II §7pour §27 secondes", " §e» §2Resistance I §7pour §27 secondes", "", "§7Une fois ce mode fini vous retournerez", "§7automatiquement en mode §apassif§7."))));
		buttons.put(15, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 14, "§6")));
		buttons.put(16, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 14, "§6")));
		buttons.put(17, new DisplayButton(new BetterItem(Material.STAINED_GLASS_PANE, 1, 14, "§6")));
	}
	
	@Override
	public void setup() {
		inventory.clear();
		for (Entry<Integer, Button> entry : buttons.entrySet())
			inventory.setItem(entry.getKey(), createItemStack(null, entry.getValue()));
	}

}
