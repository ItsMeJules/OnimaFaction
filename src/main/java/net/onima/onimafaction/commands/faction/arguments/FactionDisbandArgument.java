package net.onima.onimafaction.commands.faction.arguments;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.Role;
import net.onima.onimafaction.players.FPlayer;

public class FactionDisbandArgument extends FactionArgument {

	public FactionDisbandArgument() {
		super("disband", OnimaPerm.ONIMAFACTION_DISBAND_ARGUMENT, new String[] {"remove"});
		usage = new JSONMessage("§7/f " + name, "§d§oDissout votre faction.");
		
		role = Role.LEADER;
		playerOnly = true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 1, true))
			return false;
		
		Player player = (Player) sender;
		PlayerFaction faction = null;
		
		if ((faction = FPlayer.getPlayer(player).getFaction()) == null) {
			player.spigot().sendMessage(new JSONMessage("§cVous avez besoin d'une faction pour pouvoir la dissoudre !", "§a/f create ", true, "/f create ", ClickEvent.Action.SUGGEST_COMMAND).build());
			return false;
		}
		
		if (faction.isRaidable() && OnimaFaction.getInstance().getFactionServerEvent() != null) {
			player.sendMessage("§cVous ne pouvez pas dissoudre votre faction tant que celle-ci est raidable ! Ce serait trop facile...");
			return false;
		}
		
		Bukkit.broadcastMessage("§d§o" + APIPlayer.getPlayer(player).getColoredName(true) + " §7a dissout la faction §d§o" + faction.getName());
		faction.disband(player);
		return true;
	}

}
