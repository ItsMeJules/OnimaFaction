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
import net.onima.onimaapi.utils.time.Time.LongTime;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.Role;
import net.onima.onimafaction.players.FPlayer;

public class FactionNameArgument extends FactionArgument {
	
	public FactionNameArgument() {
		super("name", OnimaPerm.ONIMAFACTION_NAME_ARGUMENT, new String[] {"tag", "setname", "settag", "rename"});
		
		usage = new JSONMessage("§7/f " + name + " <name>", "§d§oRenomme la faction.");
		
		playerOnly = true;
		role = Role.COLEADER;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 2, true))
			return false;
		
		Player player = (Player) sender;
		FPlayer fPlayer = FPlayer.getByPlayer(player);
		PlayerFaction faction = null;
		
		if ((faction = fPlayer.getFaction()) == null) {
			player.spigot().sendMessage(new JSONMessage("§cVous avez besoin d'une faction pour la renommer !", "§a/f create ", true, "/f create ", ClickEvent.Action.SUGGEST_COMMAND).build());
			return false;
		}
		
		long toWait = faction.getRenameCooldown();
		String name = args[1];
		
		if (toWait > System.currentTimeMillis() && !player.hasPermission(OnimaPerm.ONIMAAPI_COOLDOWN_BYPASS.getPermission())) {
			player.sendMessage("§cVous devez attendre " + LongTime.setYMDWHMSFormat(toWait - System.currentTimeMillis()) + " avant de pouvoir renommer votre faction.");
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
		
		faction.setName(name);
		faction.setRenameCooldown(ConfigurationService.FACTION_RENAME_TIME + System.currentTimeMillis());
		Bukkit.broadcastMessage("§d§o" + fPlayer.getRole().getRole() + player.getName() + " §7a renommé sa faction en §d§o" + name + "§7.");
		
		return true;
	}

}
