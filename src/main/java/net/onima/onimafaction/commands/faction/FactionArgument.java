package net.onima.onimafaction.commands.faction;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.commands.BasicCommandArgument;
import net.onima.onimafaction.faction.struct.Role;
import net.onima.onimafaction.players.FPlayer;

public abstract class FactionArgument extends BasicCommandArgument {
	
	protected Role role = Role.MEMBER;
	protected boolean needFaction, needMoney;
	
	{
		role = Role.MEMBER;
		needFaction = true;
	}

	public FactionArgument(String name, OnimaPerm permission) {
		super(name, permission);
	}
	
	public FactionArgument(String name, OnimaPerm permission, String[] aliases) {
		super(name, permission, aliases);
	}

	public boolean checks(CommandSender sender, String[] args, int neededArgs, boolean sendMessage) {
		if (super.checks(sender, args, neededArgs, sendMessage))
			return true;
		else if (!hasGoodRole(sender)) {
			if (sendMessage)
				sender.sendMessage("§cVous devez être au moins " + role.getName().toLowerCase() + " pour pouvoir utiliser cette commande !");
			return true;
		}
		
		return false;
	}
	
	public boolean hasGoodRole(CommandSender sender) {
		if (sender instanceof Player)
			return FPlayer.getPlayer(((Player) sender).getUniqueId()).getRole().isAtLeast(role);
		else
			return true;
	}

}
