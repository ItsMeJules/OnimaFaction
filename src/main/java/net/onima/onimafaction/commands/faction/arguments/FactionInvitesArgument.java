package net.onima.onimafaction.commands.faction.arguments;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.players.FPlayer;

public class FactionInvitesArgument extends FactionArgument {
	
	public FactionInvitesArgument() {
		super("invites", OnimaPerm.ONIMAFACTION_INVITES_ARGUMENT);
	
		usage = new JSONMessage("§7/f " + name, "§d§oMontre toutes les invitations.");
		
		playerOnly = true;
		needFaction = false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 1, true))
			return false;
		
		Player player = (Player) sender;
		List<String> received = new ArrayList<>(10);
		
		for (PlayerFaction faction : PlayerFaction.getPlayersFaction().values()) {
			if (faction.getInvitedPlayers().contains(sender.getName()))
				received.add(faction.getName());
		}
		
		FPlayer fPlayer = FPlayer.getByPlayer(player);
		PlayerFaction faction = null;
		
		if ((faction = fPlayer.getFaction()) != null) {
			List<String> invited = faction.getInvitedPlayers();
			int index = 0;
			
			if (!invited.isEmpty()) {
				ComponentBuilder builder = new ComponentBuilder("§d§oVotre §7faction a invité : §7");
				
				for (String name : invited) {
					index++;
					builder.append(name + (index == invited.size() ? "" : ", "));
					builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f deinvite " + invited));
					builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§c/f deinvite " + invited).create()));
				}
				
				player.spigot().sendMessage(builder.create());
			} else
				player.sendMessage("§cVotre faction n'a invité personne !");
		}
		
		if (!received.isEmpty()) {
			ComponentBuilder builder = new ComponentBuilder("§d§oVous §7avez été invité par : §7");
			int index = 0;
			
			for (String who : received) {
				index++;
				builder.append(name + (index == received.size() ? "" : ", "));
				builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/f join " + who));
				builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§a/f join " + who).create()));
			}
			
			player.spigot().sendMessage(builder.create());
			return true;
		} else 
			player.sendMessage("§cPtdr personne t'a invité dans sa faction.");
		
		return false;
	}

}
