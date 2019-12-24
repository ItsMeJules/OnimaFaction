package net.onima.onimafaction.commands.faction.arguments.staff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import net.onima.onimaapi.caching.UUIDCache;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.events.FactionPlayerJoinEvent;
import net.onima.onimafaction.events.FactionPlayerJoinedEvent;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.players.FPlayer;
import net.onima.onimafaction.players.OfflineFPlayer;

public class FactionForceJoinArgument extends FactionArgument {

	public FactionForceJoinArgument() {
		super("forcejoin", OnimaPerm.ONIMAFACTION_FORCEJOIN_ARGUMENT);
		
		usage = new JSONMessage("§c/f " + name + " <faction> (player)", "§d§oRejoint de force une faction.");
		
		needFaction = false;
		playerOnly = true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 2, true))
			return false;
		
		Player player = (Player) sender;
		Faction maybeFaction = null;
		
		if ((maybeFaction = Faction.getFaction(args[1])) == null) {
			sender.sendMessage("§cImpossible de trouver la faction ou le joueur " + args[1]);
			return false;
		}
		
		if (!(maybeFaction instanceof PlayerFaction)) {
			sender.sendMessage("§cVous pouvez seulement rejoindre des factions de joueurs !");
			return false;
		}
		
		PlayerFaction faction = (PlayerFaction) maybeFaction;
		
		if (args.length > 2) {
			UUID uuid = UUIDCache.getUUID(args[2]);
			
			if (uuid == null) {
				player.sendMessage("§c" + args[2] + " ne s'est jamais connecté sur le serveur !");
				return false;
			}
			
			OfflineFPlayer.getPlayer(uuid, fPlayer -> {
				FactionPlayerJoinEvent event = new FactionPlayerJoinEvent(fPlayer, faction, true);
				
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled())
					return;
				
				if (fPlayer.hasFaction() && fPlayer.getFaction().getMembers().size() == 1) {
					fPlayer.getFaction().disband(null);
					Bukkit.broadcastMessage("§d§o" + fPlayer.getOfflineApiPlayer().getName() + " §7a dissout la faction §d§o" + faction.getName());
				}
				
				faction.addMember(fPlayer);
				Bukkit.getPluginManager().callEvent(new FactionPlayerJoinedEvent(fPlayer, faction, false));
				faction.broadcast("§d§o" + Methods.getRealName(sender) + " §7a forcé §d§o" + fPlayer.getOfflineApiPlayer().getName() + " §7à rejoindre la faction.");
				player.sendMessage("§d§oVous §7avez forcé §d§o" + Methods.getName(fPlayer.getOfflineApiPlayer(), true) + " §7a rejoindre la faction §d§o" + faction.getName() + "§7.");
			});
		}
		
		FPlayer fPlayer = FPlayer.getPlayer(player);
		FactionPlayerJoinEvent event = new FactionPlayerJoinEvent(fPlayer, faction, true);
		
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) return false;
		
		faction.addMember(fPlayer);
		Bukkit.getPluginManager().callEvent(new FactionPlayerJoinedEvent(fPlayer, faction, false));
		faction.broadcast("§d§o" + fPlayer.getOfflineApiPlayer().getName() + " §7a rejoint de force la faction !");
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length > 3 || args.length < 2) 
			return Collections.emptyList();
		
		List<String> completions = new ArrayList<>();
		
		if (args.length == 2)
			completions.addAll(PlayerFaction.getPlayersFaction().values().parallelStream().map(Faction::getName).filter(name -> StringUtil.startsWithIgnoreCase(name, args[1])).collect(Collectors.toList()));
		
		for (OfflinePlayer offline : Bukkit.getOfflinePlayers()) {
			String name = Methods.getRealName(offline);
			
			if (StringUtil.startsWithIgnoreCase(name, args[1]))
				completions.add(name);
		}
		
		return completions;
	}

}
