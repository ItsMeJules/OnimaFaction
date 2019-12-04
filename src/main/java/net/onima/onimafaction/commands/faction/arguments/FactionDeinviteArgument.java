package net.onima.onimafaction.commands.faction.arguments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import net.md_5.bungee.api.chat.ClickEvent;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.Role;
import net.onima.onimafaction.players.FPlayer;

public class FactionDeinviteArgument extends FactionArgument {
	
	public FactionDeinviteArgument() {
		super("deinvite", OnimaPerm.ONIMAFACTION_DEINVITE_ARGUMENT, new String[] {"uninvite", "revoke"});
		usage = new JSONMessage("§7/f " + name + " <player>", "§d§oAnnule une invitation dans votre faction.");
		
		playerOnly = true;
		role = Role.OFFICER;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 2, true))
			return false;
		
		Player player = (Player) sender;
		FPlayer fPlayer = FPlayer.getPlayer(player);
		PlayerFaction faction = null;
		
		if ((faction = fPlayer.getFaction()) == null) {
			player.spigot().sendMessage(new JSONMessage("§cVous avez besoin d'une faction pour annuler une invitation !", "§a/f create ", true, "/f create ", ClickEvent.Action.SUGGEST_COMMAND).build());
			return false;
		}
		
		if (faction.getInvitedPlayers().isEmpty()) {
			player.sendMessage("§cVotre faction n'a invité personne !");
			return false;
		}
		
		if (args[1].equalsIgnoreCase("all")) {
			faction.getInvitedPlayers().clear();
			faction.broadcast("§d§o" + fPlayer.getApiPlayer().getName() + " §7a supprimé toutes les invitations à rejoindre la faction !");
			return true;
		}
		
		if (!faction.getInvitedPlayers().remove(args[1])) {
			player.sendMessage("§c" + args[1] + " n'est pas invité dans votre faction !");
			return false;
		}
		
		faction.broadcast(new JSONMessage("§d§o" + fPlayer.getRole().getRole() + fPlayer.getApiPlayer().getName() + " §7a désinvité §d§o" + args[1] + " §7de la faction.", "§a/f invite " + args[1], true, "/f invite " + args[1]));
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!checks(sender, args, 2, false) || args.length == 2) {
			FPlayer fPlayer = FPlayer.getPlayer((Player) sender);
			PlayerFaction faction = null;
			
			if ((faction = fPlayer.getFaction()) != null) {
				List<String> completions = new ArrayList<>();
				
				if (StringUtil.startsWithIgnoreCase("all", args[1]))
					completions.add("all");
				
				for (String name : faction.getInvitedPlayers()) {
					if (StringUtil.startsWithIgnoreCase(name, args[1]))
						completions.add(name);
				}
				
				return completions;
			}
		}
		return Collections.emptyList();
	}
	
}
