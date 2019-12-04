package net.onima.onimafaction.commands.faction.arguments;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.events.FactionPlayerLeaveEvent.LeaveReason;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.faction.struct.Role;
import net.onima.onimafaction.players.FPlayer;

public class FactionLeaveArgument extends FactionArgument {

	public FactionLeaveArgument() {
		super("leave", OnimaPerm.ONIMAFACTION_LEAVE_ARGUMENT, new String[] {"quit"});
		usage = new JSONMessage("§7/f " + name, "§d§oQuitte votre faction.");
	
		playerOnly = true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 1, true))
			return false;
		
		Player player = (Player) sender;
		FPlayer fPlayer = FPlayer.getPlayer(player);
		PlayerFaction faction = null;
		
		if ((faction = fPlayer.getFaction()) == null) {
			player.spigot().sendMessage(new JSONMessage("§cVous avez besoin d'une faction pour pouvoir la quitter !", "§a/f create ", true, "/f create ", ClickEvent.Action.SUGGEST_COMMAND).build());
			return false;
		}
		
		if (!faction.isPermanent()) {
			if (faction.getMembers().size() == 1) {
				faction.disband(player);
				Bukkit.broadcastMessage(fPlayer.getApiPlayer().getColoredName(true) + " §7a dissout la faction §d§o" + faction.getName());
				return true;
			}
			
			if (fPlayer.getRole() == Role.LEADER) {
				player.spigot().sendMessage(new JSONMessage("§cAvant de quitter votre faction, veuillez désigner un nouveau leader.", "§a/f leader ", true, "/f leader", ClickEvent.Action.SUGGEST_COMMAND).build());
				return false;
			}
		}
		
		if (Claim.getClaimAt(player.getLocation()).getFaction().getName().equalsIgnoreCase(faction.getName())) {
			player.sendMessage("§cVous ne pouvez pas quitter votre faction tant que vous êtes dans un des claims de cette dernière !");
			return false;
		}
		
		player.sendMessage("§d§oVous §7avez quitté votre faction !");
		faction.removeMember(fPlayer, LeaveReason.LEAVE, null);
		faction.broadcast("§d§o" + fPlayer.getApiPlayer().getName() + " §7a quitté la faction !");
		return true;
	}

}
