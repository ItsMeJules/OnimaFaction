package net.onima.onimafaction.commands.faction.arguments;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.ClickEvent;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.utils.JSONMessage;
import net.onima.onimaapi.zone.struct.Flag;
import net.onima.onimaapi.zone.type.Region;
import net.onima.onimafaction.commands.faction.FactionArgument;
import net.onima.onimafaction.cooldowns.CombatTagCooldown;
import net.onima.onimafaction.cooldowns.FactionHomeCooldown;
import net.onima.onimafaction.cooldowns.PvPTimerCooldown;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.players.FPlayer;

public class FactionHomeArgument extends FactionArgument {

	public FactionHomeArgument() {
		super("home", OnimaPerm.ONIMAFACTION_HOME_ARGUMENT);
		usage = new JSONMessage("§7/f " + name, "§d§oVous téléporte à l'home de la faction.");
		
		playerOnly = true;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (checks(sender, args, 1, true))
			return false;
		
		Player player = (Player) sender;
		FPlayer fPlayer = FPlayer.getPlayer(player);
		PlayerFaction faction = null;
		
		if ((faction = fPlayer.getFaction()) == null) {
			player.spigot().sendMessage(new JSONMessage("§cVous avez besoin d'une faction pour vous téléporter à votre home !", "§a/f create ", true, "/f create ", ClickEvent.Action.SUGGEST_COMMAND).build());
			return false;
		}
		
		APIPlayer apiPlayer = fPlayer.getApiPlayer();
		
		if (apiPlayer.getTimeLeft(PvPTimerCooldown.class) > 0L) {
			player.sendMessage("§cVous ne pouvez pas vous téléporter à votre home en ayant votre pvp timer d'actif !");
			return false;
		}
		
		if (apiPlayer.getTimeLeft(CombatTagCooldown.class) > 0L) {
			player.sendMessage("§cVous ne pouvez pas vous téléporter à votre home en étant en coombat !");
			return false;
		}
		
		if (!faction.hasHome()) {
			player.sendMessage("§cVotre faction n'a pas de home !");
			return false;
		}
		
		Region regionOn = fPlayer.getRegionOn();
		boolean instantTeleport = false;
		
		if (regionOn instanceof Claim) {
			Faction factionAt = ((Claim) regionOn).getFaction();
			
			if (!factionAt.getName().equalsIgnoreCase(faction.getName())) {
				player.sendMessage("§cVous ne pouvez pas vous téléporter dans un territoire ennemie ! Si vous êtes bloqué, utilisez /f stuck.");
				return false;
			}
		}
		
		if (regionOn.hasFlag(Flag.COMBAT_SAFE))
			instantTeleport = true;
		
		if (instantTeleport) {
			player.teleport(faction.getHome());
			player.sendMessage("§aVous avez été téléporté avec succès au home de votre faction.");
		} else
			apiPlayer.startCooldown(FactionHomeCooldown.class);
		
		return true;
	}

}
