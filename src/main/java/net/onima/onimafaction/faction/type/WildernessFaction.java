package net.onima.onimafaction.faction.type;

import org.bukkit.command.CommandSender;

import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.claim.WildernessClaim;

public class WildernessFaction extends Faction {
	
	private static WildernessFaction wilderness;
	private static WildernessClaim claim;
	
	public WildernessFaction() {
		super("Nature");
		setFlags(null, null);
	}
	
	@Override
	public String getDisplayName(CommandSender sender) {
		return ConfigurationService.WILDERNESS_NAME;
	}
	
	@Override
	public void sendShow(CommandSender sender) {
		sender.sendMessage(ConfigurationService.STAIGHT_LINE);
		sender.sendMessage(' ' + getDisplayName(sender));
		sender.sendMessage(ConfigurationService.STAIGHT_LINE);
	}
	
	public static void init() {
		wilderness = new WildernessFaction();
		claim = new WildernessClaim();
	}
	
	public static WildernessFaction get() {
		return wilderness;
	}
	
	public static WildernessClaim claim() {
		return claim;
	}
	
}
