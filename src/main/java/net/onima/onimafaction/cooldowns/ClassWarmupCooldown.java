package net.onima.onimafaction.cooldowns;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.EquipmentSetEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.onima.onimaapi.cooldown.utils.Cooldown;
import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.utils.time.Time;
import net.onima.onimaapi.utils.time.Time.LongTime;
import net.onima.onimafaction.armorclass.ArmorClass;
import net.onima.onimafaction.events.armorclass.ArmorClassEquipEvent;
import net.onima.onimafaction.events.armorclass.ArmorClassUnequipEvent.ArmorClassUnequipCause;
import net.onima.onimafaction.players.FPlayer;

public class ClassWarmupCooldown extends Cooldown implements Listener {
	
	public ClassWarmupCooldown() {
		super("class_warmup", (byte) 7, 10 * Time.SECOND);
	}

	@Override
	public String scoreboardDisplay(long timeLeft) {
		return "§5Kit warm-up §6: §c" + LongTime.setHMSFormat(timeLeft);
	}

	@Override
	public boolean action(OfflineAPIPlayer offline) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void onExpire(OfflineAPIPlayer offline) {
		if (offline.isOnline()) {
			FPlayer fPlayer = FPlayer.getPlayer(offline.getUUID());
			ArmorClass loading = fPlayer.getLoadingClass();
			
			loading.setLoading(false);
			loading.setActivated(true);
			loading.onEquip();
			Bukkit.getPluginManager().callEvent(new ArmorClassEquipEvent(loading, fPlayer));
		}
		
		super.onExpire(offline);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onArmorSet(EquipmentSetEvent event) {
		FPlayer fPlayer = FPlayer.getPlayer(event.getHumanEntity().getUniqueId());
		
		if (fPlayer == null) //When you load an offline player's inventory, this event triggers.
			return;
		
		ArmorClass equipped = fPlayer.getEquippedClass();
		
		if (equipped != null) {
			if (equipped.getClassType().isApplicable(fPlayer.getApiPlayer().toPlayer()))
				return;
			
			fPlayer.equipClass(null, ArmorClassUnequipCause.PLAYER);
		} else if ((equipped = fPlayer.getLoadingClass()) != null) {
			if (equipped.getClassType().isApplicable(fPlayer.getApiPlayer().toPlayer()))
				return;
			
			onCancel(fPlayer.getOfflineApiPlayer());
			equipped.setLoading(false);
		}
		
		for (ArmorClass armorClass : fPlayer.getPlayerClasses()) {
			if (armorClass.getClassType().isApplicable(fPlayer.getApiPlayer().toPlayer())) {
				fPlayer.equipClass(armorClass, null);
				onStart(fPlayer.getApiPlayer());
				break;
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onQuit(PlayerQuitEvent event) {
		FPlayer fPlayer = FPlayer.getPlayer(event.getPlayer().getUniqueId());
		ArmorClass armorClass = fPlayer.getEquippedClass();
		
		if (armorClass != null) {
			armorClass.setLoading(false);
			onCancel(fPlayer.getOfflineApiPlayer());
		}
	}
	
}
