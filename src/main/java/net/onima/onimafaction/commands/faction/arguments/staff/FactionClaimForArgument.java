package net.onima.onimafaction.commands.faction.arguments.staff;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import net.onima.onimaapi.items.Wand;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.zone.Cuboid;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.players.FPlayer;

public class FactionClaimForArgument extends FactionArgument {

	public FactionClaimForArgument() {
		super("claimfor", OnimaPerm.ONIMAFACTION_CLAIMFOR_ARGUMENT);
		
		usage = new JSONMessage("§c/f " + name + " <faction>", "§d§oPermet de claim pour une faction la zone sélectionnée.");
		
		playerOnly = true;
		needFaction = false;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 2, true))
			return false;
		
		Player player = (Player) sender;
		Faction faction = null;
		
		if ((faction = Faction.getFaction(args[1])) == null) {
			sender.sendMessage("§cImpossible de trouver la faction ou le joueur " + args[1]);
			return false;
		}
		
		FPlayer fPlayer = FPlayer.getPlayer(player);
		Wand wand = fPlayer.getApiPlayer().getWand();
		
		if (!wand.hasAllLocationsSet()) {
			sender.sendMessage("§cVous devez sélectionner une zone !");
			sender.sendMessage("  §d§oLocation §7manquante : §d§on°" + wand.getLocation1() == null ? "1" : "2");
			return false;
		}
		
		Location loc1 = wand.getLocation1(), loc2 = wand.getLocation2();
		
		loc1.setY(ConfigurationService.CLAIM_MAX_HEIGHT);
		loc2.setY(ConfigurationService.CLAIM_MIN_HEIGHT);
		
		Claim claim = null;
		
		if (faction.addClaim(claim = new Claim(faction, Methods.getRealName(sender), loc1, loc2), fPlayer)) {
			player.sendMessage("§d§oVous §7avez bien claim pour la faction §d§o" + faction.getName());
			
			if (faction instanceof PlayerFaction) {
				Cuboid cuboid = claim.toCuboid();
				Location middle = cuboid.getCenterLocation();
				
				((PlayerFaction) faction).broadcast(new JSONMessage("§d§o" + claim.getCreator() +" §7a claim un territoire pour la faction. Passez votre souris pour plus d'informations.",
						"§e" + claim.getName() + ' ' + cuboid.getXLength() + 'x' + cuboid.getZLength() + "§7- §d§o" + middle.getBlockX() + " §c| §d§o" + middle.getBlockZ() + " §7(§e" + claim.getPrice() + ConfigurationService.MONEY_SYMBOL + "§7)"));
			}
			return true;
		} 
		
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length != 2 || !(sender instanceof Player))
			return Collections.emptyList();
		
		return Faction.getFactions().parallelStream().filter(faction -> StringUtil.startsWithIgnoreCase(faction.getName(), args[1])).map(Faction::getName).collect(Collectors.toList());
	}

}
