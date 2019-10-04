package net.onima.onimafaction.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.events.FactionFocusEvent;
import net.onima.onimafaction.events.FactionUnfocusEvent;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.players.FPlayer;

public class FocusCommand implements CommandExecutor {
	
	private JSONMessage focusJson;

	{
		focusJson = new JSONMessage("§7Utilisation : /focus <player>", "§d§oMet en rose le pseudo d'un joueur.");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cSeulement les joueurs peuvent utiliser cette commande !");
			return false;
		}
		
		Player player = (Player) sender;
		
		if (!APIPlayer.getByPlayer(player).getRank().getRankType().hasPermission(OnimaPerm.ONIMAFACTION_FOCUS_COMMAND)) {
			player.sendMessage(OnimaPerm.ONIMAFACTION_FOCUS_COMMAND.getMissingMessage());
			return false;
		}
		
		FPlayer fPlayer = FPlayer.getByPlayer(player);
		PlayerFaction faction = null;
		
		if ((faction = fPlayer.getFaction()) == null) {
			player.spigot().sendMessage(new JSONMessage("§cVous avez besoin d'une faction pour focus un joueur !", "§a/f create ", true, "/f create ", ClickEvent.Action.SUGGEST_COMMAND).build());
			return false;
		}
		
		if (args.length == 0) {
			player.spigot().sendMessage(focusJson.build());
			return false;
		}
		
		OfflinePlayer offline = Bukkit.getOfflinePlayer(args[0]);
	
		if (!offline.hasPlayedBefore() || !offline.isOnline()) {
			player.sendMessage("§c" + args[0] + " ne s'est jamais connecté sur le serveur !");
			return false;
		}
		
		OfflinePlayer focused = faction.getFocused();
		
		if (focused == null)
			focus(fPlayer, faction, offline);
		else {
			if (focused.equals(offline)) {
				faction.broadcast("§d§o" + fPlayer.getRole().getRole() + player.getName()  + " §7a unfocus " + offline.getName());
				faction.setFocused(null);
				Bukkit.getPluginManager().callEvent(new FactionUnfocusEvent(fPlayer, faction, focused));
			} else
				focus(fPlayer, faction, offline);
		}
			
		return true;
	}

	private void focus(FPlayer fPlayer, PlayerFaction faction, OfflinePlayer focused) {
		faction.broadcast("§d§o" + fPlayer.getRole().getRole() + fPlayer.getApiPlayer().getName()  + " §7a focus " + focused.getName());
		faction.setFocused(focused);
		Bukkit.getPluginManager().callEvent(new FactionFocusEvent(fPlayer, faction, focused));
	}
	
}
