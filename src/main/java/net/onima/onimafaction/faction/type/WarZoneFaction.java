package net.onima.onimafaction.faction.type;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.zone.struct.Flag;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.players.FPlayer;

public class WarZoneFaction extends Faction {
	
	private static WarZoneFaction warZone;
	
	public WarZoneFaction() {
		super("Octogone");
		
		setFlags(new Flag[0]);
		
		for (World world : Bukkit.getWorlds())
			addClaim(new Claim(this, new Location(world, -300, 0, -300), new Location(world, 300, 256, 300)), null);
		
		warZone = this;
	}
	
	@Override
	public String getDisplayName(CommandSender sender) {
		return ConfigurationService.WARZONE_NAME;
	}

	@Override
	public void sendShow(CommandSender sender) {
		sender.sendMessage(ConfigurationService.STAIGHT_LINE);
		sender.sendMessage(' ' + getDisplayName(sender));
		for (Claim claim : claims)
			sender.sendMessage("  §eLocation : §c(" + ConfigurationService.ENVIRONMENT_NAME.get(claim.toCuboid().getWorld().getEnvironment()) + ", " + 300 + " | " + -300 + ')');
		sender.sendMessage(ConfigurationService.STAIGHT_LINE);
	}
	
	@Override
	public boolean addClaim(Claim claim, FPlayer fPlayer) {
		claim.setPriority(1);
		return claims.add(claim);
	}
	
	@Override
	public void save() {}
	
	@Override
	public void remove() {}
	
	public static void init() {
		warZone = new WarZoneFaction();
	}
	
	public static WarZoneFaction get() {
		return warZone;
	}
	
}
