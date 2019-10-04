package net.onima.onimafaction.commands.faction.arguments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import net.md_5.bungee.api.chat.ClickEvent;
import net.onima.onimaapi.players.OfflineAPIPlayer;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.Role;
import net.onima.onimafaction.players.FPlayer;
import net.onima.onimafaction.players.OfflineFPlayer;

public class FactionInviteArgument extends FactionArgument {

	public FactionInviteArgument() {
		super("invite", OnimaPerm.ONIMAFACTION_INVITE_ARGUMENT, new String[] {"inv"});
		usage = new JSONMessage("§7/f " + name + " <player>", "§d§oInvite un joueur à rejoindre votre faction.");
		
		playerOnly = true;
		role = Role.OFFICER;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 2, true))
			return false;
		
		Player player = (Player) sender;
		FPlayer fPlayer = FPlayer.getByPlayer(player);
		PlayerFaction faction = null;
		
		if ((faction = fPlayer.getFaction()) == null) {
			player.spigot().sendMessage(new JSONMessage("§cVous avez besoin d'une faction pour inviter des joueurs !", "§a/f create ", true, "/f create ", ClickEvent.Action.SUGGEST_COMMAND).build());
			return false;
		}

		String name = args[1];
		
		if (faction.getMember(name) != null) {
			player.sendMessage("§c" + name + " est déjà dans votre faction.");
			return false;
		}
		
		if (faction.getMembers().size() >= ConfigurationService.FACTION_MAX_MEMBERS) {
			player.sendMessage("§cVotre pouvez être au maximum " + ConfigurationService.FACTION_MAX_MEMBERS + " joueurs dans votre faction !");
			return false;
		}
		
		if (faction.isRaidable() && OnimaFaction.getInstance().getFactionServerEvent() == null) {
			player.sendMessage("§cVous ne pouvez pas inviter de joueur lorsque votre faction est raidable !");
			return false;
		}
		
		if (faction.getInvitedPlayers().contains(name)) {
			player.sendMessage("§c" + name + " est déjà invité !");
			return true;
		}
		
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
		
		if (!offlinePlayer.hasPlayedBefore()) {
			player.sendMessage("§c" + name + " ne s'est jamais connecté sur le serveur !");
			return false;
		}
		
		OfflineFPlayer offlineFPlayer = OfflineFPlayer.getByOfflinePlayer(offlinePlayer);
		
		if (offlineFPlayer.hasFaction()) {
			player.sendMessage("§c" + name + " est déjà dans une faction !");
			return false;
		}
		
		faction.getInvitedPlayers().add(offlinePlayer.getName());
		
		if (offlinePlayer.isOnline()) {
			JSONMessage message = new JSONMessage("§d§o" + fPlayer.getRole().getRole() + player.getName() + " §7vous a invité à rejoindre §d§o" + faction.getName(), true, "/f join " + faction.getName());
			
			message.setHoverMessage("§aCliquez ici pour rejoindre " + faction.getName());
			((Player) offlinePlayer).spigot().sendMessage(message.build());
		}
		
		JSONMessage message = new JSONMessage("§d§o" + fPlayer.getRole().getRole() + player.getName() + " §7a invité §d§o" + offlinePlayer.getName() + " §7à rejoindre la faction.", "§aCliquez ici pour annuler l'invitation.");
		
		message.setClickAction(ClickEvent.Action.RUN_COMMAND);
		message.setClickString("/f deinvite " + offlinePlayer.getName());
		
		faction.broadcast(message);
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length != 2 && !(sender instanceof Player))
			return Collections.emptyList();
		
		Player player = (Player) sender;
		FPlayer fPlayer = FPlayer.getByPlayer(player);
		
		if (!fPlayer.hasFaction())
			return Collections.emptyList();
		
		PlayerFaction faction = fPlayer.getFaction();
		List<String> completions = new ArrayList<>();
		
		for (OfflineFPlayer offline : OfflineFPlayer.getDisconnectedOfflineFPlayers()) {
			OfflineAPIPlayer offlineApi = offline.getOfflineApiPlayer();
			
			if ((!offline.hasFaction() || !faction.getMembers().contains(offlineApi.getUUID()) 
					|| !faction.getInvitedPlayers().contains(offlineApi.getName())) && StringUtil.startsWithIgnoreCase(offlineApi.getName(), args[1]))
				completions.add(offlineApi.getName());
		}
	
		return completions;
	}

}
