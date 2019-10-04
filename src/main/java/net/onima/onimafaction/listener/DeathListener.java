package net.onima.onimafaction.listener;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.saver.inventory.PlayerSaver;
import net.onima.onimaapi.utils.Balance;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.zone.type.Region;
import net.onima.onimafaction.events.FactionDTRChangeEvent.DTRChangeCause;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.players.Deathban;
import net.onima.onimafaction.players.FPlayer;

public class DeathListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		Player killer = player.getKiller();
		FPlayer fPlayer = FPlayer.getByPlayer(player);
		PlayerFaction faction = fPlayer.getFaction();
		Region region = fPlayer.getRegionOn();
		
		if (region.isDeathbannable()) {
			List<PlayerSaver> savers = fPlayer.getApiPlayer().getPlayerDataSaved();
			
			fPlayer.setDeathban(new Deathban(player.getUniqueId(), killer != null ? killer.getUniqueId() : null, player.getLocation(), region.getDeathbanMultiplier(), savers.get(savers.size() - 1).getMessage()));
		}
		
		if (faction != null) {
			if (region.hasDTRLoss()) {
				faction.setDTR(faction.getDTR() - ConfigurationService.DTR_LOSS, DTRChangeCause.DEATH);
				faction.setRegenCooldown(ConfigurationService.REGEN_COOLDOWN);
			}
			
			faction.broadcast("§7Membre mort : §e" + fPlayer.getRole().getRole() + player.getName() + ". §7DTR(" + faction.getDTRColour() + faction.getDTR() + "/§f" + faction.getMaxDTR() + "§7).");
		}
		
		Balance balance = fPlayer.getApiPlayer().getBalance();
		double amountLost = balance.getAmount();
		
		if (killer != null) {
			double reward = ConfigurationService.REWARD_PER_KILL * region.getDeathbanMultiplier();
			double newAmount = OfflineAPIPlayer.getByOfflinePlayer(killer).getBalance().addAmount(amountLost + reward);
			
			if (killer.isOnline())
				killer.sendMessage("§7Comme vous avez tué §e" + player.getName() + " §7vous gagnez §e" + reward + ConfigurationService.MONEY_SYMBOL + " §7plus le montant qu'il avait (§e" + amountLost + ConfigurationService.MONEY_SYMBOL + "§7). §7Vous avez maintenant §e" + newAmount + "§7.");
		}
		
		if (amountLost != 0)
			player.sendMessage("§7Vous avez perdu §e" + amountLost + ConfigurationService.MONEY_SYMBOL + " §7! Vous avez maintanant §e" + balance.setAmount(0) + ConfigurationService.MONEY_SYMBOL + "§7.");
	}

}
