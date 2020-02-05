package net.onima.onimafaction.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.onima.onimaapi.event.disguise.PlayerDisguiseEvent;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.players.utils.PlayerOption;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.zone.struct.Flag;
import net.onima.onimafaction.players.FPlayer;

public class DisguiseListener implements Listener {

	@EventHandler
	public void onDisguise(PlayerDisguiseEvent event) {
		Player player = event.getPlayer();
		FPlayer fPlayer = FPlayer.getPlayer(player);
		
		if (fPlayer.getRegionOn().hasFlag(Flag.COMBAT_SAFE))
			return;
		
		for (Entity entity : player.getNearbyEntities(ConfigurationService.DISGUISE_WARN_RADIUS, ConfigurationService.DISGUISE_WARN_RADIUS, ConfigurationService.DISGUISE_WARN_RADIUS)) {
			if (!(entity instanceof Player))
				continue;
			
			if (fPlayer.hasFaction()) {
				if (fPlayer.getFaction().getRelation((Player) entity).isEnemy()) {
					warn(player);
					break;
				}
			} else {
				warn(player);
				break;
			}
		}
	}
	
	private void warn(Player player) {
		JSONMessage msg = new JSONMessage("§e" + player.getName() + "§cs'est /disguise en ayant des joueurs ennemie à lui dans un rayon de §e" + ConfigurationService.DISGUISE_WARN_RADIUS + " blocks.",
				"§aCliquez pour vous téléporter.", true, "/tp " + player.getName());
		
		for (APIPlayer apiPlayer : APIPlayer.getOnlineAPIPlayers()) {
			if (apiPlayer.getRank().getRankType().hasPermisssion(OnimaPerm.ONIMAAPI_DISGUISE_COMMAND_LIST)
					&& apiPlayer.getOptions().getBoolean(PlayerOption.GlobalOptions.DISGUISE_MESSAGES_WARN))
				apiPlayer.sendMessage(msg);
		}
	}
	
}
