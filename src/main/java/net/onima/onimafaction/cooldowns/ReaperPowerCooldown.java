package net.onima.onimafaction.cooldowns;

import net.onima.onimaapi.cooldown.utils.Cooldown;
import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.utils.time.Time;
import net.onima.onimaapi.utils.time.Time.LongTime;
import net.onima.onimafaction.armorclass.Reaper;
import net.onima.onimafaction.armorclass.Reaper.ReaperStage;
import net.onima.onimafaction.players.FPlayer;

public class ReaperPowerCooldown extends Cooldown {

	public ReaperPowerCooldown() {
		super("power_mode", (byte) 14, 7 * Time.SECOND);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String scoreboardDisplay(long timeLeft) {
		return "  §e» Mode §cPower (" + LongTime.setHMSFormat(timeLeft) + ")";
	}

	@Override
	public boolean action(OfflineAPIPlayer offline) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onExpire(OfflineAPIPlayer offline) {
		if (offline.isOnline()) { 
			Reaper reaper = (Reaper) FPlayer.getByUuid(offline.getUUID()).getArmorClass(Reaper.class);
			
			if (reaper.isActivated())
				reaper.start(ReaperStage.PASSIVE_MODE);
		}
		
		super.onExpire(offline);
	}
	
}
