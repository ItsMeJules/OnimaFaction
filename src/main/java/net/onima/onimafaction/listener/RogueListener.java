package net.onima.onimafaction.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import net.onima.onimafaction.armorclass.Rogue;
import net.onima.onimafaction.players.FPlayer;

public class RogueListener implements Listener {

	@EventHandler
	public void onEntityAttack(EntityDamageByEntityEvent event) { 
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			Player player = (Player) event.getEntity();
			Player attacker = (Player) event.getDamager();
			FPlayer fPlayer = FPlayer.getPlayer(player);
			Rogue rogue = (Rogue) fPlayer.getArmorClass(Rogue.class);
			
			if (rogue.isActivated()) {
				ItemStack hand = attacker.getItemInHand();
				
				if (hand != null && Rogue.ROGUE_BACKSTAB.getItemStack().isSimilar(hand)) {
					Rogue.ROGUE_BACKSTAB.action(attacker);
					attacker.sendMessage("§eVous §7avez poignardé §c" + fPlayer.getApiPlayer().getDisplayName());
					
					player.sendMessage("§c" + attacker.getName() + " §evous §7a poignardé.");
					Rogue.ROGUE_BACKSTAB.getUseSound().play(fPlayer.getApiPlayer());
					
					event.setDamage(event.getDamage() * Rogue.BACKSTAB_DAMAGE);
				}
			}
		}
	}
	
}
