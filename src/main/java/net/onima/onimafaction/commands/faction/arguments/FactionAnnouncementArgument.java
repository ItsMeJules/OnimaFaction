package net.onima.onimafaction.commands.faction.arguments;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import net.md_5.bungee.api.chat.ClickEvent;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.Role;
import net.onima.onimafaction.players.FPlayer;

public class FactionAnnouncementArgument extends FactionArgument {

	public FactionAnnouncementArgument() {
		super("announcement", OnimaPerm.ONIMAFACTION_ANNOUNCEMENT_ARGUMENT, new String[] {"motd"});
		
		usage = new JSONMessage("§7/f " + name + " <announcement>", "§d§oModifie l'annonce (motd) de la faction.");
		
		playerOnly = true;
		role = Role.OFFICER;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 2, true))
			return false;
		
		Player player = (Player) sender;
		FPlayer fPlayer = FPlayer.getByPlayer(player);
		PlayerFaction faction = null;
		
		if ((faction = fPlayer.getFaction()) == null) {
			player.spigot().sendMessage(new JSONMessage("§cVous avez besoin d'une faction pour définir une annonce !", "§a/f create ", true, "/f create ", ClickEvent.Action.SUGGEST_COMMAND).build());
			return false;
		}
		
		String oldAnnouncement = faction.getAnnouncement();
		String announcement = "";
		
		switch (args[1]) {
		case "clear":
		case "remove":
		case "delete":
		case "none":
			announcement = null;
			break;
		default:
			announcement = StringUtils.join(args, ' ', 1, args.length);
			break;
		}
		
		if (oldAnnouncement == null && announcement == null) {
			player.sendMessage("§cVotre faction a déjà aucun motd !");
			return false;
		}
		
		if (oldAnnouncement != null && announcement != null && oldAnnouncement.equals(announcement)) {
			player.sendMessage("§cExplique moi pourquoi t'essaies de mettre EXACTEMENT le même motd ??");
			return false;
		}
		
		faction.setAnnouncement(announcement);
		
		if (announcement == null) {
			faction.broadcast(new JSONMessage("§d§o" + fPlayer.getRole().getRole() + player.getName() + " §7a supprimé l'motd de la faction.", "§aCliquez pour changer l'motd.", true, "/f motd ", ClickEvent.Action.SUGGEST_COMMAND));
			return true;
		}
		
		faction.broadcast(new JSONMessage("§d§o" + fPlayer.getRole().getRole() + player.getName() + " §7a modifié l'motd de la faction (§e" + oldAnnouncement + "§7) par §e" + announcement + "§7.", "§aCliquez pour changer l'motd.", true, "/f motd ", ClickEvent.Action.SUGGEST_COMMAND));
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) || args.length != 2)
			return Collections.emptyList();
		
		return Lists.newArrayList("clear");
	}

}
