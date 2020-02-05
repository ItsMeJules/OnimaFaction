package net.onima.onimafaction.task;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.scheduler.BukkitRunnable;

import net.onima.onimafaction.armorclass.Bard;

public class BardPowerTask extends BukkitRunnable {
	
	private static Set<Bard> bards = new HashSet<>();
	
	@Override
	public void run() {
		Iterator<Bard> iterator = bards.iterator();
		
		while (iterator.hasNext()) {
			Bard bard = iterator.next();
			
			if (!bard.isActivated()) {
				iterator.remove();
				continue;
			}
			
			if (bard.getPower() < Bard.MAX_POWER)
				bard.addPower(1);
		}
	}

	public static Set<Bard> getBards() {
		return bards;
	}
	
}