package net.onima.onimafaction.commands.faction.arguments;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.onima.onimaapi.gui.PacketMenu;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.struct.Role;

public class FactionGUIArgument extends FactionArgument {

	public FactionGUIArgument() {
		super("gui", OnimaPerm.ONIMAFACTION_GUI_ARGUMENT, new String[] {"menu", "manage"});
		usage = new JSONMessage("§7/f " + name, "§d§oOuvre un menu pour gérer les commandes de faction.");
	
		playerOnly = true;
		role = Role.OFFICER;
		needFaction = false;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 1, true))
			return false;
		
		Player player = (Player) sender;
		APIPlayer apiPlayer = APIPlayer.getPlayer(player);
		
		apiPlayer.openMenu(PacketMenu.getMenu("faction_gui"));
		return true;
	}

}
