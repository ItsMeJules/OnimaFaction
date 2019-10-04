package net.onima.onimafaction.events.battle_royale;

import java.util.List;

import org.bukkit.potion.PotionEffect;

import net.onima.onimafaction.timed.event.BattleRoyale.Phase;

public class BattleRoyalePhaseStartEvent extends BattleRoyalePhaseEvent {

	private List<PotionEffect> effects;
	
	public BattleRoyalePhaseStartEvent(Phase phase, List<PotionEffect> effects) {
		super(phase);
		this.effects = effects;
	}

	public List<PotionEffect> getEffects() {
		return effects;
	}

	public void setEffects(List<PotionEffect> effects) {
		this.effects = effects;
	}
	
	

}
