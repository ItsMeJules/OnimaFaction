package net.onima.onimafaction.cooldowns;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import net.onima.onimaapi.cooldown.utils.Cooldown;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.utils.time.Time;
import net.onima.onimaapi.utils.time.Time.LongTime;
import net.onima.onimafaction.armorclass.Bard;
import net.onima.onimafaction.armorclass.utils.BardItem;
import net.onima.onimafaction.players.FPlayer;

public class BardItemCooldown extends Cooldown implements Listener {
	
	public BardItemCooldown() {
		super("bard_item", (byte) 8, 10 * Time.SECOND);
	}
	
	@Override
	public String scoreboardDisplay(long timeLeft) {
		return "  §e» Barding §6: §c" + LongTime.setHMSFormat(timeLeft);
	}
	
	@Override
	public boolean action(OfflineAPIPlayer offline) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void onExpire(OfflineAPIPlayer offline) {
		if (offline.isOnline())
			((APIPlayer) offline).sendMessage("§aVous pouvez de nouveau bard !");
	
		super.onExpire(offline);
	}
	
	@EventHandler
	public void onItemHeld(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		long remaining;
		
		Bard bard = (Bard) FPlayer.getPlayer(player).getArmorClass(Bard.class);
		
		if (bard.isActivated()) {
			if ((remaining = getTimeLeft(player.getUniqueId())) > 0L) {
				player.sendMessage("§cVous devez attendre " + LongTime.setHMSFormat(remaining) + " avant de pouvoir bard de nouveau !");
				return;
			}
			
			ItemStack item = player.getInventory().getItem(event.getNewSlot());
			
			if (item != null) {
				BardItem bardItem = BardItem.fromItemStack(item);
				
				if (bardItem != null) 
					bardItem.use(bard, false, false);
			}
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (!event.hasItem()) return;
		
		Action action = event.getAction();
		
		if (!action.toString().contains("RIGHT")) return;
		
		long remaining;
		Player player = event.getPlayer();
		
		Bard bard = (Bard) FPlayer.getPlayer(player).getArmorClass(Bard.class);
		
		if (bard.isActivated()) {
			if ((remaining = getTimeLeft(player.getUniqueId())) > 0L) {
				player.sendMessage("§cVous devez attendre " + LongTime.setHMSFormat(remaining) + " avant de pouvoir bard de nouveau !");
				return;
			}

			BardItem bardItem = BardItem.fromItemStack(event.getItem());
			
			if (bardItem != null && bardItem.use(bard, true, true) == BardItem.BardItemUseFinality.SUCCESS)
				onStart(bard.getFPlayer().getOfflineApiPlayer());
		}
	}

}
