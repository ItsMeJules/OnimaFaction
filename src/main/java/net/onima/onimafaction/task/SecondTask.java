package net.onima.onimafaction.task;

import org.bukkit.scheduler.BukkitRunnable;

import net.onima.onimafaction.armorclass.Bard;
import net.onima.onimafaction.players.FPlayer;

public class SecondTask extends BukkitRunnable {
	
	@Override
	public void run() {
		for (FPlayer fPlayer : FPlayer.getOnlineFPlayers()) {
			Bard bard = (Bard) fPlayer.getArmorClass(Bard.class);
			
			if (bard.isActivated() && bard.getPower() < Bard.MAX_POWER)
				bard.addPower(1);
		}
	}

}
