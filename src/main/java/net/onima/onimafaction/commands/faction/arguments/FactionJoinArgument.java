package net.onima.onimafaction.commands.faction.arguments;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import net.md_5.bungee.api.chat.BaseComponent;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.events.FactionPlayerJoinEvent;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.players.FPlayer;

public class FactionJoinArgument extends FactionArgument {

	public FactionJoinArgument() {
		super("join", OnimaPerm.ONIMAFACTION_JOIN_ARGUMENT, new String[] {"accept", "j"});
		usage = new JSONMessage("§7/f " + name + " <faction | player>", "§dRejoint une faction.");
		
		playerOnly = true;
		needFaction = false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 2, true))
			return false;
		
		Player player = (Player) sender;
		FPlayer fPlayer = FPlayer.getByPlayer(player);
		
		if (fPlayer.getFaction() != null) {
			player.spigot().sendMessage(new JSONMessage("§cVous avez déjà une faction, quittez la si vous voulez en rejoindre une autre.", "§a/f leave", true, "/f leave").build());
			return false;
		}
		
		Faction faction = null;
		
		if ((faction = Faction.getFaction(args[1])) == null) {
			player.sendMessage("§cImpossible de trouver la faction ou le joueur " + args[1]);
			return false;
		}
		
		if (!(faction instanceof PlayerFaction)) {
			player.sendMessage("§cVous pouvez seulement rejoindre les factions de jouers !");
			return false;
		}
		
		PlayerFaction pFac = (PlayerFaction) faction;
		
		if (!pFac.isOpen() && !pFac.getInvitedPlayers().contains(player.getName())) {
			player.sendMessage("§cVous avez besoin d'une invitation pour rejoindre la faction " + pFac.getName());
			pFac.broadcast("§d§o" + player.getName() + " §7§oa essayé de rejoindre votre faction.");
			return false;
		}
		
		FactionPlayerJoinEvent event = new FactionPlayerJoinEvent(fPlayer, faction, false);
		
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) return false;
		
		if (pFac.addMember(fPlayer)) {
			BaseComponent[] message = new JSONMessage("§d§o" + player.getName() + " §7a rejoint la faction.", "§7Cliquez ici pour kick §d§o" + player.getName(), true, "/f kick " + player.getName()).build();
			
			for (FPlayer member : pFac.getOnlineMembers(null)) {
				if (fPlayer.equals(member)) continue;
				member.getApiPlayer().sendMessage(message);
			}
		}
		
		pFac.getInvitedPlayers().remove(player.getName());
		player.sendMessage("§d§oVous §7avez rejoint la faction §d§o" + faction.getName());
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player) || args.length != 2)
			return Collections.emptyList();
		
		return Faction.getFactions().parallelStream().filter(faction -> faction instanceof PlayerFaction).filter(faction -> ((PlayerFaction) faction).getInvitedPlayers().contains(sender.getName())).map(Faction::getName).filter(name -> StringUtil.startsWithIgnoreCase(name, args[1])).collect(Collectors.toList());
	}

}
