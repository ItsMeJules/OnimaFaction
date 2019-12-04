package net.onima.onimafaction.faction.type;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.command.CommandSender;

import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.zone.struct.Flag;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.claim.Claim;

public class SafeZoneFaction extends Faction {
	
	private static SafeZoneFaction safeZone;
	
	public SafeZoneFaction() {
		super("Spawn");
		
		setFlags(Flag.COMBAT_SAFE, Flag.COMBAT_TAG_DENY_ENTRY, Flag.DENY_ENDERPEARL, Flag.NO_BARDING, Flag.PVP_TIMER_PAUSE, Flag.SAFE_DISCONNECT);
		
		for (World world : Bukkit.getWorlds()) {
			Environment environment = world.getEnvironment();
			
			if (environment == Environment.THE_END)
				continue;
			
			int radius = ConfigurationService.SPAWN_RADIUS_ENV.get(environment) - 1;
			Claim claim = new Claim(this, new Location(world, -radius, 0, -radius), new Location(world, radius, 256, radius));
			
			claim.setDeathban(false);
			claim.setDTRLoss(false);
			claim.setPriority(5);
			claims.add(claim);
		}
		
		
		safeZone = this;
	}
	
	@Override
	public String getDisplayName(CommandSender sender) {
		return ConfigurationService.SAFEZONE_NAME;
	}
	
	@Override
	public void sendShow(CommandSender sender) {
		sender.sendMessage(ConfigurationService.STAIGHT_LINE);
		sender.sendMessage(' ' + getDisplayName(sender));
		for (Claim claim : claims) {
			Location middle = claim.toCuboid().getCenterLocation();
			sender.sendMessage("  §eLocation : §c(" + ConfigurationService.ENVIRONMENT_NAME.get(middle.getWorld().getEnvironment()) + ", " + middle.getBlockX() + " | " + middle.getBlockZ() + ')');
		}
		sender.sendMessage(ConfigurationService.STAIGHT_LINE);
	}
	
	@Override
	public void save() {}
	
	@Override
	public void remove() {}
	
	public static void init() {
		safeZone = new SafeZoneFaction();
	}
	
	public static SafeZoneFaction get() {
		return safeZone;
	}
	
}
