package net.onima.onimafaction.commands.faction.arguments.staff;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.Faction;

public class FactionDeathbanArgument extends FactionArgument {

	public FactionDeathbanArgument() {
		super("deathban", OnimaPerm.ONIMAFACTION_DEATHBAN_ARGUMENT, new String[] {"setdeathban"});
		
		usage = new JSONMessage("§c/f " + name + " <faction> (true | false)", "§d§oDéfini une faction deathbannable ou non.");
		
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
			
			claim.setDeathban(value == null ? !claim.isDeathbannable() : value);
			json.appendHoverMessage(" §7- §e" + claim.getName() + " §e(" + (claim.isDeathbannable() ? "§cDeathban" : "§bNon-Deathban") + "§e)  §7- §d§o" + middle.getBlockX() + " §c| §d§o" + middle.getBlockZ());
		});
		
		Methods.sendJSON(sender, json.build());
		return true;
	}

}
