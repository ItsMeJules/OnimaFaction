package net.onima.onimafaction.commands.faction.arguments.staff;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.Faction;

public class FactionDtrLossArgument extends FactionArgument {

	public FactionDtrLossArgument() {
		super("dtrloss", OnimaPerm.ONIMAFACTION_DTRLOSS_ARGUMENT);
		
		usage = new JSONMessage("§c/f " + name + " <faction> (true | false)", "§d§oPermet de définir si l'on perdu du dtr quand on meurt dans une faction.");
		needFaction = false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 2, true))
			return false;
		
		Faction faction = null;
		Boolean value = args.length == 3 ? Boolean.valueOf(args[2]) : false;
		
		if ((faction = Faction.getFaction(args[1])) == null) {
			sender.sendMessage("§cImpossible de trouver la faction ou le joueur " + args[1]);
			return false;
		}
		
		JSONMessage json = new JSONMessage("§d§oVeuillez §7passer votre souris pour savoir quels claims ont été changés.",
				"§7La propriété des claims suivants a été modifé :\n");
		
		faction.getClaims().parallelStream().forEach(claim -> {
			Location middle = claim.toCuboid().getCenterLocation();
			
			claim.setDTRLoss(value == null ? !claim.hasDTRLoss() : value);
			json.appendHoverMessage(" §7- §e" + claim.getName() + " §e(" + (claim.hasDTRLoss() ? "§cPerte de DTR" : "§bPas de perte de DTR") + "§e)  §7- §d§o" + middle.getBlockX() + " §c| §d§o" + middle.getBlockZ());
		});
		
		Methods.sendJSON(sender, json.build());
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length != 2 && !OnimaPerm.ONIMAFACTION_DTRLOSS_ARGUMENT.has(sender))
			return Collections.emptyList();
		
		return Faction.getFactions().parallelStream().map(Faction::getName).filter(name -> StringUtil.startsWithIgnoreCase(args[1], name)).collect(Collectors.toList());
	}

}
