package net.onima.onimafaction.armorclass.type;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.armorclass.type.utils.ClassType;

public class MineurArmor implements ClassType {

	@Override
	public ItemStack getHelmet() {
		return new ItemStack(Material.IRON_HELMET);
	}

	@Override
	public ItemStack getChestplate() {
		return new ItemStack(Material.IRON_CHESTPLATE);
	}

	@Override
	public ItemStack getLeggings() {
		return new ItemStack(Material.IRON_LEGGINGS);
	}

	@Override
	public ItemStack getBoots() {
		return new ItemStack(Material.IRON_BOOTS);
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
	@Override
	public boolean isApplicable(Player player) {
		PlayerInventory inventory = player.getInventory();
		
		return Methods.isSimilar(getHelmet(), inventory.getHelmet()) &&
			   Methods.isSimilar(getChestplate(), inventory.getChestplate()) &&
			   Methods.isSimilar(getLeggings(), inventory.getLeggings()) &&
			   Methods.isSimilar(getBoots(), inventory.getBoots());
	}
	
}
