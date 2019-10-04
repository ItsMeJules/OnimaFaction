package net.onima.onimafaction.commands.faction.arguments.staff;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.faction.Faction;

public class FactionLockArgument extends FactionArgument {

	public FactionLockArgument() {
		super("lock", OnimaPerm.ONIMAFACTION_LOCK_ARGUMENT);
		
		usage = new JSONMessage("§c/f " + name, "§d§oPermet de vérouiller plusieurs actions avec les factions.");
		
		needFaction = false;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 1, true))
			return false;
		
		Faction.lock = !Faction.lock;
		Bukkit.broadcastMessage("§cLa plupart des actions avec les factions sont " + (Faction.lock ? "§cbloquées" : "§adébloquées") + " §c!");
		return false;
	}

}
