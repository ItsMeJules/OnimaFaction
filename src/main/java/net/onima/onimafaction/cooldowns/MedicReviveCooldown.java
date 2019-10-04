package net.onima.onimafaction.cooldowns;

import net.onima.onimaapi.cooldown.utils.Cooldown;
import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.utils.time.Time;
import net.onima.onimaapi.utils.time.Time.LongTime;

public class MedicReviveCooldown extends Cooldown {

	public MedicReviveCooldown() {
		super("medic_revive", (byte) 12, 6 * Time.HOUR);
	}

	@Override
	public String scoreboardDisplay(long timeLeft) {
		return "§7Medic revive §6: §c" + LongTime.setHMSFormat(timeLeft);
	}

	@Override
	public boolean action(OfflineAPIPlayer offline) {
		// TODO Auto-generated method stub
		return false;
	}

	
	
}
