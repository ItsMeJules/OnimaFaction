package net.onima.onimafaction.commands.faction.arguments.staff;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.zone.Cuboid;
import net.onima.onimaapi.zone.type.Region;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.players.FPlayer;

public class FactionForceUnclaimArgument extends FactionArgument {

	public FactionForceUnclaimArgument() {
		super("forceunclaim", OnimaPerm.ONIMAFACTION_FORCEUNCLAIM_ARGUMENT);
		
		usage = new JSONMessage("§c/f " + name, "§d§oUnclaim de force le territoire sur lequel vous vous trouvez.");
		
		playerOnly = true;
		needFaction = false;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 1, true))
			return false;
		
		FPlayer fPlayer = FPlayer.getPlayer((Player) sender);
		Region region = fPlayer.getRegionOn();
		
		if (!(region instanceof Claim)) {
			sender.sendMessage("§cVous ne vous trouvez sur aucun claim !");
			return false;
		}
		
		Claim claim = (Claim) region;
		Faction faction = claim.getFaction();
		
		if (faction.removeClaim(claim, fPlayer)) {
			sender.sendMessage("§d§oVous §7avez unclaim de force un territoire de la faction §d§o" + faction.getName() + "§7.");
			
			if (faction instanceof PlayerFaction) {
				Cuboid cuboid = claim.toCuboid();
				Location middle = cuboid.getCenterLocation();
				
				((PlayerFaction) faction).broadcast(new JSONMessage("§d§o" + Methods.getRealName(sender) + " §7a unclaim un de vos territoire. Passez votre souris pour plus d'informations.", "§e" + claim.getName() + ' ' + cuboid.getXLength() + 'x' + cuboid.getZLength() + "§7- §d§o" + middle.getBlockX() + " §c| §d§o" + middle.getBlockZ() + " §7(§e0" + ConfigurationService.MONEY_SYMBOL + "§7)"));
			}
			return true;
		}
		
		return false;
	}

}
