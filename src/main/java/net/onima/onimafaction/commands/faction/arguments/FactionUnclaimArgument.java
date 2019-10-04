package net.onima.onimafaction.commands.faction.arguments;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.zone.Cuboid;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.faction.struct.Role;
import net.onima.onimafaction.players.FPlayer;

public class FactionUnclaimArgument extends FactionArgument {
	
	public FactionUnclaimArgument() {
		super("unclaim", OnimaPerm.ONIMAFACTION_UNCLAIM_ARGUMENT);
		usage = new JSONMessage("§7/f " + name + " (all)", "§d§oUnclaim un territoire sur lequel vous vous trouvez.");
		
		playerOnly = true;
		role = Role.OFFICER;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 1, true))
			return false;
		
		Player player = (Player) sender;
		FPlayer fPlayer = FPlayer.getByPlayer(player);
		PlayerFaction faction = null;
		
		if ((faction = fPlayer.getFaction()) == null) {
			player.spigot().sendMessage(new JSONMessage("§cVous avez besoin d'une faction pour unclaim un territoire !", "§a/f create ", true, "/f create ", ClickEvent.Action.SUGGEST_COMMAND).build());
			return false;
		}
		
		Set<Claim> toUnClaim = new HashSet<>();
		
		if (faction.getClaims().size() == 0) {
			player.sendMessage("§cVotre faction n'a pas de claims !");
			return true;
		}
		
		if (args.length > 1 && args[1].equalsIgnoreCase("all"))
			toUnClaim.addAll(faction.getClaims());
		else {
			Claim claim = Claim.getClaimAt(player.getLocation());
			
			if (claim.getFaction().getName().equalsIgnoreCase(faction.getName()))
				toUnClaim.add(claim);
			else {
				player.sendMessage("§cVous pouvez seulement unclaim vos territoires !");
				return false;
			}
		}
		
		int claims = toUnClaim.size();
		boolean plur = claims > 1;
		
		JSONMessage jsonMessage = new JSONMessage(
				"§d§o" + fPlayer.getRole().getRole() + player.getName() + " §7a unclaim §d§o" + claims + " §7territoire" + (plur ? 's' : "") + "§7. Passez votre souris pour plus d'informations.",
				"§6Claim" + (plur ? 's' : "") + " supprimé" + (plur ? 's' : "") + " :\n");
		
		for (Claim claim : toUnClaim) {
			Cuboid cuboid = claim.toCuboid();
			Location middle = cuboid.getCenterLocation();
			double money = claim.getPrice() * 0.8;
			
			jsonMessage.appendHoverMessage(" §7- §e" + claim.getName() + ' ' + cuboid.getXLength() + 'x' + cuboid.getZLength() + "§7- §d§o" + middle.getBlockX() + " §c| §d§o" + middle.getBlockZ() + " §7(§e" + Methods.round("0.#", money) + ConfigurationService.MONEY_SYMBOL + "§7)\n");
			faction.removeClaim(claim, fPlayer);
			faction.addMoney(money);
		}
		
		StringUtils.left(jsonMessage.getHoverMessage(), jsonMessage.getHoverMessage().length() - 3);
		faction.broadcast(jsonMessage);
		return false;
	}

}
