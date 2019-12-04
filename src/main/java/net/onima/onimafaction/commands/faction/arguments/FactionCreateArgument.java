package net.onima.onimafaction.commands.faction.arguments;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.events.FactionCreateEvent;
import net.onima.onimafaction.events.FactionPreCreateEvent;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.players.FPlayer;

public class FactionCreateArgument extends FactionArgument {

	public FactionCreateArgument() {
		super("create", OnimaPerm.ONIMAFACTION_CREATE_ARGUMENT, new String[] {"make"});
		usage = new JSONMessage("§7/f " + name + " <name>", "§d§oCréée une faction");
		playerOnly = true;
		needFaction = false;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 2, true))
			return false;
		
		Player player = (Player) sender;
		FPlayer fPlayer = FPlayer.getPlayer(player.getUniqueId());
		String name = args[1];
		
		if (fPlayer.hasFaction()) {
			String command = "/f leave " + fPlayer.getFaction().getName();
			JSONMessage message = new JSONMessage("§cVous avez déjà une faction ! Si vous voulez en créer une autre vous devez quitter celle-ci.", "§c" + command, true, command);
		
			message.setClickAction(ClickEvent.Action.RUN_COMMAND);
			message.setClickString(command);
			player.spigot().sendMessage(message.build());
			return false;
		}
		
		if (Faction.getFaction(name) != null) {
			player.sendMessage("§cUne faction existe déjà avec le nom : " + name);
			return false;
		}
		
		if (ConfigurationService.BLOCKED_FACTION_NAMES.contains(name)) {
			player.sendMessage("§cVous ne pouvez pas appeler votre faction " + name + " !");
			return false;
		}
		
		if (name.length() > ConfigurationService.FACTION_NAME_MAX_LENGTH || name.length() < ConfigurationService.FACTION_NAME_MIN_LENGTH) {
			player.sendMessage("§cUn nom de faction est compris entre " + ConfigurationService.FACTION_NAME_MAX_LENGTH + " et " + ConfigurationService.FACTION_NAME_MIN_LENGTH + '.');
			return false;
		}
		
		if (!StringUtils.isAlphanumeric(name)) {
			player.sendMessage("§cVotre nom de faction doit être alpha numérique !");
			return false;
		}
		
		FactionPreCreateEvent event = new FactionPreCreateEvent(name, player);
		
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) return true;
		
		Bukkit.broadcastMessage(fPlayer.getApiPlayer().getColoredName(true) + " §7a créé la faction §d§o" + event.getName());
		Bukkit.getPluginManager().callEvent(new FactionCreateEvent(new PlayerFaction(event.getName(), fPlayer), player));
		return true;
	}

}
