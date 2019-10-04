package net.onima.onimafaction.cooldowns;

import net.onima.onimaapi.cooldown.utils.Cooldown;
import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.utils.time.Time;

public class ReaperModeCooldown extends Cooldown {

	public ReaperModeCooldown() {
		super("reaper_mode", (byte) 15, 40 * Time.SECOND);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String scoreboardDisplay(long timeLeft) {
		return null;
	}

	@Override
	public boolean action(OfflineAPIPlayer offline) {
		return false;
	}
	
}
