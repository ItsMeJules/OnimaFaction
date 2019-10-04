package net.onima.onimafaction.armorclass.type.utils;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface ClassType {
	
	public ItemStack getHelmet();
	public ItemStack getChestplate();
	public ItemStack getLeggings();
	public ItemStack getBoots();
	public boolean isEnabled();
	public boolean isApplicable(Player player);
	
}
