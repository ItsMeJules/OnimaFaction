package net.onima.onimafaction.armorclass;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.armorclass.type.utils.ClassType;
import net.onima.onimafaction.players.FPlayer;

public abstract class ArmorClass {
	
	protected static Integer MAXIMUM_DURATION_EFFECT;
	
	static {
		MAXIMUM_DURATION_EFFECT = 8 * 60 * 20;
	}
	
	protected FPlayer fPlayer;
	protected boolean activated, loading;
	protected Collection<PotionEffect> effectsTookFromClass;
	protected ClassType classType;

	{
		effectsTookFromClass = new ArrayList<>();
	}
	
	public ArmorClass(FPlayer fPlayer) {
		this.fPlayer = fPlayer;
	}
	
	public abstract String getNiceName();
	public abstract Collection<PotionEffect> getEffects();
	public abstract boolean onLoad();
	
	public void onEquip() {
		Player player = fPlayer.getApiPlayer().toPlayer();
		
		for (PotionEffect effect : getEffects()) {
			if (player.hasPotionEffect(effect.getType()))
				effectsTookFromClass.add(Methods.getPotionEffectByType(player.getActivePotionEffects(), effect.getType()));
			
			player.addPotionEffect(effect, true);
		}
	}
	
	public boolean onUnequip() {
		Player player = fPlayer.getApiPlayer().toPlayer();
		
		for (PotionEffect effect : getEffects())
			player.removePotionEffect(effect.getType());
		
		player.addPotionEffects(effectsTookFromClass);
		effectsTookFromClass.clear();
		return true;
	}
	
	public FPlayer getFPlayer() {
		return fPlayer;
	}
	
	public ClassType getClassType() {
		return classType;
	}
	
	public boolean isActivated() {
		return activated;
	}
	
	public void setActivated(boolean activated) {
		this.activated = activated;
	}
	
	public boolean isLoading() {
		return loading;
	}
	
	public void setLoading(boolean loading) {
		this.loading = loading;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((classType == null) ? 0 : classType.hashCode());
		
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		ArmorClass other = (ArmorClass) obj;
		
		if (classType == null) {
			if (other.classType != null)
				return false;
		} else if (!classType.equals(other.classType))
			return false;
		
		return true;
	}
	
	
	
}
