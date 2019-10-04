package net.onima.onimafaction.armorclass.utils;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.onima.onimaapi.saver.Saver;
import net.onima.onimaapi.utils.OSound;

public abstract class Buff implements Saver {
	
	protected static Collection<Buff> buffs;
	
	static {
		buffs = new ArrayList<>();
	}

	protected String id;
	protected ItemStack itemStack;
	protected OSound useSound;
	
	public Buff(String id, ItemStack itemStack, OSound useSound) {
		this.id = id;
		this.itemStack = itemStack;
		this.useSound = useSound;
		save();
	}
	
	public abstract boolean action(Player player);
	
	public String getId() {
		return id;
	}
	
	public ItemStack getItemStack() {
		return itemStack;
	}
	
	public OSound getUseSound() {
		return useSound;
	}

	@Override
	public void save() {
		buffs.add(this);
	}

	@Override
	public void remove() {
		buffs.remove(this);
	}

	@Override
	public boolean isSaved() {
		return buffs.contains(this);
	}
	
	public static Buff fromName(String id) {
		return buffs.parallelStream().filter(buff -> buff.id.equalsIgnoreCase(id)).findFirst().orElse(null);
	}

}
