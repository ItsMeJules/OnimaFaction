package net.onima.onimafaction.cooldowns;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import net.onima.onimaapi.cooldown.utils.Cooldown;
import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.utils.time.Time;
import net.onima.onimaapi.utils.time.Time.LongTime;
import net.onima.onimafaction.armorclass.Reaper;
import net.onima.onimafaction.armorclass.Reaper.ReaperStage;
import net.onima.onimafaction.players.FPlayer;

public class ReaperStealthCooldown extends Cooldown implements Listener {
	
	public ReaperStealthCooldown() {
		super("reaper_stealth", (byte) 13, 7 * Time.SECOND);
	}

	@Override
	public String scoreboardDisplay(long timeLeft) {
		return "  §e» Mode §5Stealth §c(" + LongTime.setHMSFormat(timeLeft) + ")";
	}

	@Override
	public boolean action(OfflineAPIPlayer offline) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void onExpire(OfflineAPIPlayer offline) {
		if (offline.isOnline()) {
			Reaper reaper = (Reaper) FPlayer.getPlayer(offline.getUUID()).getArmorClass(Reaper.class);
			
			if (reaper.isActivated())
				reaper.start(ReaperStage.POWER_MODE);
		}
		
		super.onExpire(offline);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getAction().toString().contains("RIGHT") && event.hasItem() && Reaper.REAPER_BUFF.getItemStack().isSimilar(event.getItem())) {
			Reaper reaper = (Reaper) FPlayer.getPlayer(event.getPlayer()).getArmorClass(Reaper.class);
			
			if (reaper.isActivated())
				reaper.start(ReaperStage.STEALTH_MODE);
		}
	}

}
