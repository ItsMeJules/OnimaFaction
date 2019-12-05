package net.onima.onimafaction.commands;

import java.util.Collection;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.onima.onimaapi.OnimaAPI;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.events.FactionChatEvent;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.Chat;
import net.onima.onimafaction.players.FPlayer;
import net.onima.onimafaction.utils.FactionChatMessage;

public class TeamLocationCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!OnimaPerm.ONIMAFACTION_TEAMLOCATION_COMMAND.has(sender)) {
			sender.sendMessage(OnimaAPI.UNKNOWN_COMMAND);
			return false;
		}
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cSeulement les joueurs peuvent utiliser cette commande !");
			return false;
		}
		
		Player player = (Player) sender;
		APIPlayer apiPlayer = APIPlayer.getPlayer(player);
		
		FPlayer fPlayer = FPlayer.getPlayer(apiPlayer.getUUID());
		Location location = player.getLocation();
		PlayerFaction faction = null;
		
		if ((faction = fPlayer.getFaction()) == null) {
			player.spigot().sendMessage(new JSONMessage("§cVous avez besoin d'une faction pour envoyer votre location !", "§a/f create ", true, "/f create ", ClickEvent.Action.SUGGEST_COMMAND).build());
			return false;
		}
		
		String message = "§e[" + location.getBlockX() + ' ' + location.getBlockY() + ' ' + location.getBlockZ() + "] " + apiPlayer.getFacingDirection();
		Collection<FPlayer> toSend = faction.getOnlineMembers(null);
		FactionChatEvent event = new FactionChatEvent(player, faction, toSend.stream().map(member -> member.getApiPlayer().toPlayer()).collect(Collectors.toList()), Chat.FACTION, message);
		Bukkit.getPluginManager().callEvent(event);
		
		if (event.isCancelled()) 
			return false;
		
		new FactionChatMessage(sender.getName(), message)
		.faction(faction)
		.chat(event.getChat())
		.role(fPlayer.getRole())
		.build().send(event.getReaders());
		
		return true;
	}

}
