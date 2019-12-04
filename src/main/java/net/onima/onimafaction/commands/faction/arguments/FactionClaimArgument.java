package net.onima.onimafaction.commands.faction.arguments;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import net.md_5.bungee.api.chat.ClickEvent;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.faction.struct.Role;
import net.onima.onimafaction.players.FPlayer;

public class FactionClaimArgument extends FactionArgument {

	public FactionClaimArgument() {
		super("claim", OnimaPerm.ONIMAFACTION_CLAIM_ARGUMENT);
		usage = new JSONMessage("§7/f " + name, "§d§oVous donne une baguette de claim.");
		
		playerOnly = true;
		role = Role.OFFICER;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 1, true))
			return false;
		
		Player player = (Player) sender;
		FPlayer fPlayer = FPlayer.getPlayer(player);
		PlayerFaction faction = null;
		
		if ((faction = fPlayer.getFaction()) == null) {
			player.spigot().sendMessage(new JSONMessage("§cVous avez besoin d'une faction pour claim !", "§a/f create ", true, "/f create ", ClickEvent.Action.SUGGEST_COMMAND).build());
			return false;
		}
		
		if (OnimaFaction.getInstance().getFactionServerEvent() != null) {
			player.sendMessage("§cVous ne pouvez pas claim durant un event où les factions sont raidables, de toute façon c'est inutile :)");
			return false;
		}
		
		if (faction.isRaidable()) {
			player.sendMessage("§cVous ne pouvez pas claim tant que votre faction est raidable !");
			return false;
		}
		
		Inventory inventory = player.getInventory();
		
		if (inventory.contains(Claim.CLAIMING_WAND)) {
			player.sendMessage("§cVous avez déjà une baguette de claim dans votre inventaire !");
			return false;
		}
		
		if (!inventory.addItem(Claim.CLAIMING_WAND).isEmpty()) {
			player.sendMessage("§cVidez votre inventaire pour recevoir une baguette de claim !");
			return false;
		}
		
		player.sendMessage("§d§oVous §7avez reçu une " + ConfigurationService.CLAIMING_WAND_NAME.toLowerCase() + "§7. Ouvrez votre inventaire et lisez la description pour comprendre comment ça fonctionne. Alternativement, vous pouvez utiliser /f claimchunk pour claim un terrritoire de 16x16.");
		return true;
	}

}
