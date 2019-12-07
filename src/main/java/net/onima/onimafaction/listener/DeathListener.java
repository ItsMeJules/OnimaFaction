package net.onima.onimafaction.listener;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.saver.inventory.PlayerSaver;
import net.onima.onimaapi.utils.Balance;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.zone.type.Region;
import net.onima.onimafaction.events.FactionDTRChangeEvent.DTRChangeCause;
import net.onima.onimafaction.faction.EggAdvantage;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.DTRStatus;
import net.onima.onimafaction.faction.struct.EggAdvantageType;
import net.onima.onimafaction.players.Deathban;
import net.onima.onimafaction.players.FPlayer;
import net.onima.onimafaction.task.RegenerationEntryTask;

public class DeathListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		Player killer = player.getKiller();
		FPlayer fPlayer = FPlayer.getPlayer(player);
		PlayerFaction faction = fPlayer.getFaction();
		Region region = fPlayer.getRegionOn();
		
		if (region.isDeathbannable()) {
			List<PlayerSaver> savers = fPlayer.getApiPlayer().getPlayerDataSaved();
			
			fPlayer.setDeathban(new Deathban(player.getUniqueId(), killer != null ? killer.getUniqueId() : null, player.getLocation(), region.getDeathbanMultiplier(), savers.get(savers.size() - 1).getMessage()));
		}
		
		if (faction != null) {
			double modifier = 0;
			
			if (region.hasDTRLoss()) {
				float dtr = ConfigurationService.DTR_LOSS;
				
				if (fPlayer.hasFaction()) {
					EggAdvantage egg = faction.getEggAdvantage(EggAdvantageType.DTR_PD_IN_CLAIMS);
					
					if (egg.getAmount() > 0 && region.getDisplayName(player).equalsIgnoreCase(fPlayer.getFaction().getName())) {
						modifier = egg.getAmount() * egg.getType().getChanger();
						dtr *= modifier;
					}
				}

				if (faction.getDTRStatut() == DTRStatus.FULL)
					RegenerationEntryTask.get().insert(faction);
				
				faction.setDTR(faction.getDTR() - dtr, DTRChangeCause.DEATH);
				faction.setRegenCooldown(ConfigurationService.REGEN_COOLDOWN);
			}
			
			faction.broadcast("§7Membre mort : §e" + fPlayer.getRole().getRole() + fPlayer.getApiPlayer().getName() + ". §7DTR(" + faction.getDTRColour() + faction.getDTR() + "§7/§f" + faction.getMaxDTR() + "§7) " + (modifier != 0 ? "§7[§6Bonus : §cx" + modifier + "§7] " : "") + ".");
		}
		
		Balance balance = fPlayer.getApiPlayer().getBalance();
		double amountLost = balance.getAmount();
		
		if (killer != null) {
			double reward = ConfigurationService.REWARD_PER_KILL * region.getDeathbanMultiplier();
			EggAdvantage egg = faction.getEggAdvantage(EggAdvantageType.KILL_MONEY_MULTP);
			double modifier = 0;
			
			if (egg.getAmount() > 0) {
				modifier = 1 + egg.getAmount() * egg.getType().getChanger();
				reward *= modifier;
			}
			
			double newAmount = APIPlayer.getPlayer(killer).getBalance().addAmount(amountLost + reward);
			
			if (killer.isOnline())
				killer.sendMessage("§7Comme vous avez tué §e" + fPlayer.getApiPlayer().getName() + " §7vous gagnez §e" + reward + ConfigurationService.MONEY_SYMBOL + (modifier != 0 ? "§7[§c§lx" + modifier + "§7]" : "") + " §7plus le montant qu'il avait (§e" + amountLost + ConfigurationService.MONEY_SYMBOL + "§7). §7Vous avez maintenant §e" + newAmount + "§7.");
		}
		
		if (amountLost != 0)
			player.sendMessage("§7Vous avez perdu §e" + amountLost + ConfigurationService.MONEY_SYMBOL + " §7! Vous avez maintanant §e" + balance.setAmount(0) + ConfigurationService.MONEY_SYMBOL + "§7.");
	}

}
