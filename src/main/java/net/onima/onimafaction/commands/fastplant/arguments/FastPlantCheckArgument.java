package net.onima.onimafaction.commands.fastplant.arguments;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.commands.BasicCommandArgument;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.gui.plants.FastPlantTargetMenu;
import net.onima.onimafaction.gui.plants.PlantTypeClaimMenu;
import net.onima.onimafaction.plants.FastPlant;

public class FastPlantCheckArgument extends BasicCommandArgument {

	public FastPlantCheckArgument() {
		super("check", OnimaPerm.ONIMAFACTION_FASTPLANT_CHECK_COMMAND);
		
		usage = new JSONMessage("§7/fastplant " + name + " (claim) ", "§d§oAffiche les informations sur le \n§d§ofast plant visé ou dans tout le claim.");
		playerOnly = true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 1, true))
			return false;
		
		Player player = (Player) sender;
		APIPlayer apiPlayer = APIPlayer.getPlayer(player);
		
		if (args.length < 2) {
			Location location = player.getTargetBlock(null, 5).getLocation();
			FastPlant plant = FastPlant.getByLocation(location);
			
			if (plant == null) {
				sender.sendMessage("§cLe bloc visé n'est pas une fast plant !");
				return false;
			}
			
			apiPlayer.openMenu(new FastPlantTargetMenu(plant));
		} else if (args[1].equalsIgnoreCase("claim")) {
			Claim claim = Claim.getClaimAt(player.getLocation());
			
			if (!claim.getFaction().isNormal()) {
				sender.sendMessage("§cSeulement utilisable dans un claim de faction de joueurs !");
				return false;
			}
			
			if (claim.getFastPlants().isEmpty()) {
				sender.sendMessage("§cCe claim ne contient pas de fast plant !");
				return false;
			}
			
			apiPlayer.openMenu(new PlantTypeClaimMenu(claim));
		}
		
		return true;
	}

}
