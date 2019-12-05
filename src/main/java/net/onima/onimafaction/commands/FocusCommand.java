package net.onima.onimafaction.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.onima.onimaapi.OnimaAPI;
import net.onima.onimaapi.caching.UUIDCache;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.events.FactionFocusEvent;
import net.onima.onimafaction.events.FactionUnfocusEvent;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.players.FPlayer;

public class FocusCommand implements CommandExecutor { //TODO ajouter le focus pour toute une faction.
	
	private JSONMessage focusJson;

	{
		focusJson = new JSONMessage("§7Utilisation : /focus <player>", "§d§oMet en rose le pseudo d'un joueur.");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!OnimaPerm.ONIMAFACTION_FOCUS_COMMAND.has(sender)) {
			sender.sendMessage(OnimaAPI.UNKNOWN_COMMAND);
			return false;
		}
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cSeulement les joueurs peuvent utiliser cette commande !");
			return false;
		}
		
		Player player = (Player) sender;
		
		FPlayer fPlayer = FPlayer.getPlayer(player);
		PlayerFaction faction = null;
		
		if ((faction = fPlayer.getFaction()) == null) {
			player.spigot().sendMessage(new JSONMessage("§cVous avez besoin d'une faction pour focus un joueur !", "§a/f create ", true, "/f create ", ClickEvent.Action.SUGGEST_COMMAND).build());
			return false;
		}
		
		if (args.length == 0) {
			player.spigot().sendMessage(focusJson.build());
			return false;
		}
		
		UUID uuid = UUIDCache.getUUID(args[0]);
	
		if (uuid == null) {
			player.sendMessage("§c" + args[0] + " ne s'est jamais connecté sur le serveur !");
			return false;
		}
		
		if (faction.getMembers().containsKey(uuid)) {
			sender.sendMessage("§cVous ne pouvez pas focus un joueur de votre faction !");
			return false;
		}
		
		for (String ally : faction.getAllies()) {
			if (((PlayerFaction) Faction.getFaction(ally)).getMembers().containsKey(uuid)) {
				sender.sendMessage("§cVous ne pouvez pas focus un joueur allié à votre faction !");
				return false;
			}
		}
		
		OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
		OfflinePlayer focused = faction.getFocused();
		
		if (focused == null)
			focus(fPlayer, faction, offline, args[0]);
		else {
			if (focused.getUniqueId().equals(offline.getUniqueId())) {
				faction.broadcast("§d§o" + fPlayer.getRole().getRole() + fPlayer.getApiPlayer().getName()  + " §7a unfocus " + Methods.getNameFromArg(focused, args[0]));
				faction.setFocused(null);
				Bukkit.getPluginManager().callEvent(new FactionUnfocusEvent(fPlayer, faction, focused));
			} else
				focus(fPlayer, faction, offline, args[0]);
		}
			
		return true;
	}

	private void focus(FPlayer fPlayer, PlayerFaction faction, OfflinePlayer focused, String arg) {
		faction.broadcast("§d§o" + fPlayer.getRole().getRole() + fPlayer.getApiPlayer().getName()  + " §7a focus " + Methods.getNameFromArg(focused, arg));
		faction.setFocused(focused);
		Bukkit.getPluginManager().callEvent(new FactionFocusEvent(fPlayer, faction, focused));
	}
	
}
