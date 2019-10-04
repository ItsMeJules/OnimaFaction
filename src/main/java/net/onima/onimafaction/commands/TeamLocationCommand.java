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
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cSeulement les joueurs peuvent utiliser cette commande !");
			return false;
		}
		
		Player player = (Player) sender;
		APIPlayer apiPlayer = APIPlayer.getByPlayer(player);
		
		if (!apiPlayer.getRank().getRankType().hasPermission(OnimaPerm.ONIMAFACTION_TEAMLOCATION_COMMAND)) {
			player.sendMessage(OnimaPerm.ONIMAFACTION_TEAMLOCATION_COMMAND.getMissingMessage());
			return false;
		}
		
		FPlayer fPlayer = FPlayer.getByUuid(apiPlayer.getUUID());
		Location location = player.getLocation();
		PlayerFaction faction = null;
		
		if ((faction = fPlayer.getFaction()) == null) {
			player.spigot().sendMessage(new JSONMessage("§cVous avez besoin d'une faction pour envoyer votre location !", "§a/f create ", true, "/f create ", ClickEvent.Action.SUGGEST_COMMAND).build());
			return false;
		}
		
		String message = "§e[" + location.getBlockX() + ' ' + location.getBlockY() + ' ' + location.getBlockZ() + "] " + apiPlayer.getFacingDirection();
		Collection<FPlayer> toSend = faction.getOnlineMembers(null);
		FactionChatEvent event = new FactionChatEvent(player, faction, toSend.stream().map(member -> member.getApiPlayer().toPlayer()).collect(Collectors.toList()), Chat.FACTION, message);
		
		new FactionChatMessage(player, message)
		.faction(faction)
		.chat(Chat.FACTION)
		.role(fPlayer.getRole())
		.build().send(event.getReaders());
		
		Bukkit.getPluginManager().callEvent(event);
		return true;
	}

}
