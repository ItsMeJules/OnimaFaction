package net.onima.onimafaction.commands.faction.arguments;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import com.google.common.collect.Lists;

import net.md_5.bungee.api.chat.ClickEvent;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.events.FactionRelationRemoveEvent;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.Relation;
import net.onima.onimafaction.faction.struct.Role;
import net.onima.onimafaction.players.FPlayer;

public class FactionUnallyArgument extends FactionArgument {

	public FactionUnallyArgument() {
		super("unally", OnimaPerm.ONIMAFACTION_UNALLIANCE_ARGUMENT, new String[] {"revokeally", "neutral", "enemy"});
		usage = new JSONMessage("§7/f " + name + " <faction | player>", "§d§oVous enlève l'alliance avec une faction.");
		
		playerOnly = true;
		role = Role.OFFICER;
	}

	@SuppressWarnings("unused")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 2, true))
			return false;
		
		Player player = (Player) sender;
		FPlayer fPlayer = FPlayer.getPlayer(player);
		PlayerFaction faction = null;
		
		if ((faction = fPlayer.getFaction()) == null) {
			player.spigot().sendMessage(new JSONMessage("§cVous avez besoin d'une faction pour enlever vos alliances à d'autres factions !", "§a/f create ", true, "/f create ", ClickEvent.Action.SUGGEST_COMMAND).build());
			return false;
		}
		
		if (ConfigurationService.FACTION_MAX_ALLY < 1) {
			player.sendMessage("§cIl n'y a pas d'alliance possible durant cette map.");
			return false;
		}
		
		if (faction.getAllies().isEmpty()) {
			player.sendMessage("§cVotre faction n'a pas d'allié !");
			return false;
		}
		
		if (args[1].equalsIgnoreCase("all")) {
			String msg = "§d§o" + faction.getName() + "§7 a décidé de ne plus être allié avec vous !";
			for (String allyName : faction.getAllies()) {
				PlayerFaction ally = (PlayerFaction) Faction.getFaction(allyName);
				FactionRelationRemoveEvent event = new FactionRelationRemoveEvent(faction, ally, Relation.ALLY);
				Bukkit.getPluginManager().callEvent(event);
				
				if (event.isCancelled()) continue;
				
				ally.removeRelation(faction);
				faction.removeRelation(ally);
				ally.broadcast(msg);
			}
			faction.broadcast("§d§o" + fPlayer.getRole().getRole() + fPlayer.getApiPlayer().getName() + " §7a supprimé toutes les alliances de la faction !");
			return true;
		}
		
		Faction maybeFaction = null;
		
		if ((maybeFaction = Faction.getFaction(args[1])) == null) {
			player.sendMessage("§cLa faction " + args[1] + " n'existe pas !");
			return false;
		}
		
		if (!(faction instanceof PlayerFaction)) {
			player.sendMessage("§cVous pouvez seulement enlever vos alliances avec des factions de joueurs !");
			return false;
		}
		
		PlayerFaction targetFaction = (PlayerFaction) maybeFaction;
		
		if (!faction.getRelations().containsKey(targetFaction.getName())) {
			player.sendMessage("§cVotre faction n'est pas allié à la faction " + args[1]);
			return false;
		}
		
		FactionRelationRemoveEvent event = new FactionRelationRemoveEvent(faction, targetFaction, Relation.ALLY);
		Bukkit.getPluginManager().callEvent(event);
		
		if (event.isCancelled()) return true;
		
		targetFaction.removeRelation(faction);
		faction.removeRelation(targetFaction);
		targetFaction.broadcast("§d§o" + faction.getName() + "§7 a décidé de ne plus être allié avec vous !");
		faction.broadcast("§d§o" + fPlayer.getRole().getRole() + fPlayer.getApiPlayer().getName() + " §7a supprimé l'alliance avec §d§o" + targetFaction.getName() + "§7.");
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player) || args.length != 2)
			return Collections.emptyList();
		
		Player player = (Player) sender;
		FPlayer fPlayer = FPlayer.getPlayer(player);
		PlayerFaction faction = null;
		
		if ((faction = fPlayer.getFaction()) == null)
			return Collections.emptyList();
		
		List<String> completions = Lists.newArrayList(faction.getAllies());
		
		if (StringUtil.startsWithIgnoreCase("all", args[1]))
			completions.add("all");
		
		return completions.stream().filter(name -> StringUtil.startsWithIgnoreCase(name, args[1])).collect(Collectors.toList());
	}
	
}
