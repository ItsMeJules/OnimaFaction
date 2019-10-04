package net.onima.onimafaction.cooldowns;

import net.onima.onimaapi.cooldown.utils.Cooldown;
import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.utils.time.Time;

public class InsideStormCooldown extends Cooldown {

	public InsideStormCooldown() {
		super("inside_storm", (byte) 16, 2 * Time.SECOND);
	}
	
	@Override
	public boolean action(OfflineAPIPlayer offline) {
		return false;
	}
	
	@Override
	public String scoreboardDisplay(long timeLeft) {
		return null;
	}
	

}
