package net.onima.onimafaction.commands.faction.arguments;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.Role;
import net.onima.onimafaction.players.FPlayer;

public class FactionOpenArgument extends FactionArgument {

	public FactionOpenArgument() {
		super("open", OnimaPerm.ONIMAFACTION_OPEN_ARGUMENT);
		usage = new JSONMessage("§7/f " + name, "§d§oOuvre votre faction. Tous les joueurs peuvent la rejoindre.");
		
		playerOnly = true;
		role = Role.COLEADER;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 1, true))
			return false;
		
		Player player = (Player) sender;
		PlayerFaction faction = null;
		
		if ((faction = FPlayer.getByPlayer(player).getFaction()) == null) {
			player.spigot().sendMessage(new JSONMessage("§cVous avez besoin d'une faction pour pouvoir ouvrir cette dernière !", "§a/f create ", true, "/f create ", ClickEvent.Action.SUGGEST_COMMAND).build());
			return false;
		}
		
		if (faction.isRaidable() && OnimaFaction.getInstance().getFactionServerEvent() == null) {
			player.sendMessage("§cVous ne pouvez pas ouvrir votre faction tant que celle-ci est raidable !");
			return false;
		}
		
		faction.setOpen(!faction.isOpen());
		faction.broadcast(new JSONMessage("§d§o" + player.getName() + " §7a " + (faction.isOpen() ? "§aouvert" : "§cfermé") + " §7la faction !" , "/f open", true, "/f open"));
		return true;
	}

}
