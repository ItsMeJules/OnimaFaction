package net.onima.onimafaction.commands.faction.arguments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import net.md_5.bungee.api.chat.ClickEvent;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.events.FactionRelationCreateEvent;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.Relation;
import net.onima.onimafaction.faction.struct.Role;
import net.onima.onimafaction.players.FPlayer;

public class FactionAllyArgument extends FactionArgument {

	public FactionAllyArgument() {
		super("ally", OnimaPerm.ONIMAFACTION_ALLIANCE_ARGUMENT, new String[] {"alliance"});
		usage = new JSONMessage("§7/f " + name + " <faction | player>", "§d§oVous allie à une faction.");
		
		role = Role.OFFICER;
		playerOnly = true;
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
			player.spigot().sendMessage(new JSONMessage("§cVous avez besoin d'une faction pour vous allier à d'autres factions !", "§a/f create ", true, "/f create ", ClickEvent.Action.SUGGEST_COMMAND).build());
			return false;
		}
		
		if (ConfigurationService.FACTION_MAX_ALLY < 1) {
			player.sendMessage("§cIl n'y a pas d'ally possible durant cette map.");
			return false;
		}
		
		if (faction.getAllies().size() >= ConfigurationService.FACTION_MAX_ALLY) {
			player.sendMessage("§cVotre faction a atteint le nombre maximum d'alliance !");
			return false;
		}
		
		Faction maybeFaction = null;
		
		if ((maybeFaction = Faction.getFaction(args[1])) == null) {
			player.sendMessage("§cImpossible de trouver la faction ou le joueur " + args[1]);
			return false;
		}
		
		if (!(faction instanceof PlayerFaction)) {
			player.sendMessage("§cVous pouvez seulement vous allier avec les factions de joueurs !");
			return false;
		}
		
		PlayerFaction targetFaction = (PlayerFaction) faction;
		Collection<String> allies = targetFaction.getAllies();
		
		if (allies.size() >= ConfigurationService.FACTION_MAX_ALLY) {
			player.sendMessage("§c" + targetFaction.getName() + " a atteint le nombre maximum d'alliance !");
			return false;
		}
		
		if (allies.contains(faction.getName())) {
			player.sendMessage("§cVous êtes déjà allié à cette faction !");
			return false;
		}
		
		if (targetFaction.getRequestedRelations().containsKey(faction.getName())) {
			FactionRelationCreateEvent event = new FactionRelationCreateEvent(faction, targetFaction, Relation.ALLY);
			Bukkit.getPluginManager().callEvent(event);
			
			if (event.isCancelled())
				return true;
			
			faction.removeRequestedRelation(targetFaction);
			targetFaction.removeRequestedRelation(faction);
			
			faction.addRelation(targetFaction, Relation.ALLY);
			targetFaction.addRelation(faction, Relation.ALLY);
			
			targetFaction.broadcast(new JSONMessage("§d§o" + faction.getName() + " §7a accepté votre demande d'alliance.", "§7Cliquez ici pour annuler cette alliance avec §d§o" + faction.getName() + "§7.", true, "/f unally " + faction.getName()));
			faction.broadcast(new JSONMessage("§d§oVotre §7faction est maintenant allié à §d§o" + targetFaction.getName(), "§7Cliquez ici pour annuler cette alliance avec §d§o" + targetFaction.getName() + "§7.", true, "/f unally " + targetFaction.getName()));
		}
		
		if (faction.getRequestedRelations().putIfAbsent(targetFaction.getName(), Relation.ALLY) == null) {
			faction.broadcast(new JSONMessage("§d§o" + fPlayer.getApiPlayer().getName() + " §7a envoyé une demande d'alliance à §d§o" + targetFaction.getName(), "§7Cliquez ici pour annuler cette alliance avec §d§o" + targetFaction.getName() + "§7.", true, "/f unally " + targetFaction.getName()));
			targetFaction.broadcast(new JSONMessage("§d§o" + faction.getName() + " §7souhaite être votre allié. §7§oCliquez sur le message pour accepter.", "§7Cliquez ici pour vous allier avec §d§o" + faction.getName() + "§7.", true, "/f ally " + faction.getName()));
			return true;
		}
		
		player.sendMessage("§d§oVous §7avez déjà demandé à §d§o" + targetFaction.getName() + " §7d'être son allié.");
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 2, false))
			return Collections.emptyList();
		
		Player player = (Player) sender;
		FPlayer fPlayer = FPlayer.getPlayer(player);
		
		if (fPlayer.hasFaction()) {
			PlayerFaction faction = fPlayer.getFaction();
			List<String> completions = new ArrayList<>();
			
			completions.addAll(Faction.getFactions().parallelStream().filter(fac -> fac instanceof PlayerFaction).filter(fac -> ((PlayerFaction) fac).getRelation(faction) != Relation.ALLY).map(Faction::getName).filter(name -> !name.equalsIgnoreCase(faction.getName())).filter(name -> StringUtil.startsWithIgnoreCase(name, args[1])).collect(Collectors.toList()));
			
			return completions;
		}
		return Collections.emptyList();
	}

}
