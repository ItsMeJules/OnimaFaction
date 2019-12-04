package net.onima.onimafaction.gui;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.enchantments.Enchantment;

import net.onima.onimaapi.gui.PacketMenu;
import net.onima.onimaapi.gui.buttons.DisplayButton;
import net.onima.onimaapi.gui.buttons.MenuOpenerButton;
import net.onima.onimaapi.gui.menu.ConfirmationMenu;
import net.onima.onimaapi.utils.BetterItem;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.faction.EggAdvantage;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.EggAdvantageType;
import net.onima.onimafaction.players.FPlayer;

public class EggAdvantageMenu extends PacketMenu {

	private FPlayer fPlayer;
	private Location location;

	public EggAdvantageMenu(FPlayer fPlayer, Location location) {
		super("egg_advantage_menu", "§9Choisissez un avantage.", MIN_SIZE, false);
		
		this.fPlayer = fPlayer;
		this.location = location;
	}

	@Override
	public void registerItems() {
		List<EggAdvantage> eggs = fPlayer.getFaction().getAllEggAdvantages();
		PlayerFaction faction = fPlayer.getFaction();
		
		for (EggAdvantageType type : EggAdvantageType.values()) {
			BetterItem item = new BetterItem(type.getMaterial(), 1);
			
			for (EggAdvantage egg : eggs) {
				if (egg.getType() == type) {
					
					if (egg.getAmount() >= 1) {
						item.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
						item.setAmount(egg.getAmount());
					} else
						item.setAmount(1);
					
					final int nextLevel = egg.getAmount() + 1;
					DisplayButton button = new DisplayButton(item.setName(type.getName()).setLore(Methods.replacePlaceholder(type.getLore(), "%level%", (egg.getAmount() == 0 ? 0 : Methods.toRomanNumber(egg.getAmount())) + " §7- §eProchain niveau §c" + (nextLevel > type.getMaxLevel() ? "aucun" : Methods.toRomanNumber(egg.getAmount() + 1)),
							"%price%", nextLevel > type.getMaxLevel() ? "aucun" : type.getInitialPrice() * (nextLevel) + String.valueOf(ConfigurationService.MONEY_SYMBOL))));
							
					ConfirmationMenu menu = new ConfirmationMenu("§3Installer cet oeuf ?", (bool) -> {
						double price = type.getInitialPrice() * nextLevel;
						
						if (faction.getMoney() < price) {
							close(fPlayer.getApiPlayer(), true);
							fPlayer.getApiPlayer().sendMessage("§cVotre faction n'a pas assez d'argent !");
							return false;
						}
						
						if (nextLevel > type.getMaxLevel()) {
							EggAdvantageMenu.this.open(fPlayer.getApiPlayer());
							fPlayer.getApiPlayer().sendMessage("§cNiveau maximum atteint !");
							return false;
						}
						
						if (bool) {
							if (egg.addEgg(location)) {
								faction.broadcast("§d" + fPlayer.getApiPlayer().getName() + " §7a ajouté un boost général : " + type.getName() + " niveau " + Methods.toRomanNumber(egg.getAmount()) + " §7pour §e" + price + ConfigurationService.MONEY_SYMBOL + "§7.");
								faction.removeMoney(price);
							} else
								fPlayer.getApiPlayer().sendMessage("§cIl faut un block d'émeraude en-dessous de l'oeuf !");
						}
						
						close(fPlayer.getApiPlayer(), true);
						return true;
					}, false, button);

					
					buttons.put(buttons.size(), new MenuOpenerButton(item, menu));
				}
			}
		}
	}

}
