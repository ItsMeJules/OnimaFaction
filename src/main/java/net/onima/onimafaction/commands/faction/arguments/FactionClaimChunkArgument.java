package net.onima.onimafaction.commands.faction.arguments;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.faction.claim.ClaimSelection;
import net.onima.onimafaction.faction.struct.Role;
import net.onima.onimafaction.players.FPlayer;

public class FactionClaimChunkArgument extends FactionArgument {

	public FactionClaimChunkArgument() {
		super("claimchunk", OnimaPerm.ONIMAFACTION_CLAIMCHUNK_ARGUMENT, new String[] {"chunkclaim", "normalclaim", "oldclaim"});
		usage = new JSONMessage("§7/f " + name, "§d§oPermet de claim un chunk (16x16).");
		
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
			player.spigot().sendMessage(new JSONMessage("§cVous avez besoin d'une faction pour pouvoir claim un chunk !", "§a/f create ", true, "/f create ", ClickEvent.Action.SUGGEST_COMMAND).build());
			return false;
		}
		
		if (faction.isRaidable() && OnimaFaction.getInstance().getFactionServerEvent() == null) {
			player.sendMessage("§cVous ne pouvez pas claim de chunk tant que votre faction est raidable !");
			return false;
		} else if (OnimaFaction.getInstance().getFactionServerEvent() != null) {
			player.sendMessage("§cVous ne pouvez pas claim de chunk lors d'un event où les factions sont raidables !");
			return false;
		}
		
		Location loc = player.getLocation();
		Location location1 = loc.clone().add(8, ConfigurationService.CLAIM_MAX_HEIGHT, 7);
		Location location2 = loc.clone().add(-8, ConfigurationService.CLAIM_MIN_HEIGHT, -8);
		
		if (Claim.canSelectHere(player, location1, true) && Claim.canSelectHere(player, location2, true))
			return Claim.tryToBuyClaim(player, new ClaimSelection(location1, location2));
		
		return false;
	}

}
