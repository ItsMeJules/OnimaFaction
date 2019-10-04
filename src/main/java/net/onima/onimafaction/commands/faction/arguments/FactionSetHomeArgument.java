package net.onima.onimafaction.commands.faction.arguments;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.zone.type.Region;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.faction.struct.Role;
import net.onima.onimafaction.players.FPlayer;

public class FactionSetHomeArgument extends FactionArgument {
	
	public FactionSetHomeArgument() {
		super("sethome", OnimaPerm.ONIMAFACTION_SETHOME_ARGUMENT);
		usage = new JSONMessage("§7/f " + name, "§d§oDéfini l'home de votre faction.");
		
		playerOnly = true;
		role = Role.COLEADER;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 1, true))
			return false;
		
		Player player = (Player) sender;
		FPlayer fPlayer = FPlayer.getByPlayer(player);
		PlayerFaction faction = null;
		
		if ((faction = fPlayer.getFaction()) == null) {
			player.spigot().sendMessage(new JSONMessage("§cVous avez besoin d'une faction pour pouvoir définir un home !", "§a/f create ", true, "/f create ", ClickEvent.Action.SUGGEST_COMMAND).build());
			return false;
		}
		
		Region region = fPlayer.getRegionOn();
		
		if (region instanceof Claim) {
			Claim claim = (Claim) region;
			
			if (claim.getFaction().getName().equalsIgnoreCase(faction.getName())) {
				faction.setHome(player.getLocation());
				faction.broadcast("§d§o" + fPlayer.getRole().getRole() + player.getName() + " §7a défini le home de la faction !");
				return true;
			}
		}
		
		player.sendMessage("§cVous devez être dans votre territoire pour définir votre home !");
		return false;
	}

}
