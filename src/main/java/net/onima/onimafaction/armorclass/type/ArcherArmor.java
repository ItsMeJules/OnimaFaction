package net.onima.onimafaction.armorclass.type;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.onima.onimafaction.armorclass.type.utils.ClassType;

public class ArcherArmor implements ClassType {

	@Override
	public ItemStack getHelmet() {
		return new ItemStack(Material.LEATHER_HELMET);
	}

	@Override
	public ItemStack getChestplate() {
		return new ItemStack(Material.LEATHER_CHESTPLATE);
	}

	@Override
	public ItemStack getLeggings() {
		return new ItemStack(Material.LEATHER_LEGGINGS);
	}

	@Override
	public ItemStack getBoots() {
		return new ItemStack(Material.LEATHER_BOOTS);
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
	@Override
	public boolean isApplicable(Player player) {
		PlayerInventory inventory = player.getInventory();
		
		ItemStack helmet = inventory.getHelmet();
		ItemStack chestplate = inventory.getChestplate();
		ItemStack leggings = inventory.getLeggings();
		ItemStack boots = inventory.getBoots();
		
		return helmet != null && getHelmet().getType() == helmet.getType() &&
			   chestplate != null && getChestplate().getType() == chestplate.getType() &&
			   leggings != null && getLeggings().getType() == leggings.getType() &&
			   boots != null && getBoots().getType() == boots.getType();
	}
	

}
