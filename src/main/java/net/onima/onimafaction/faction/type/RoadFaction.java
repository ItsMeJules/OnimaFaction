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
import net.onima.onimafaction.players.FPlayer;

public class RoadFaction extends Faction {
	
	private static NorthRoad north;
	private static SouthRoad south;
	private static EastRoad east;
	private static WestRoad west;
	
	public RoadFaction(String name) {
		super(name);
		
		setFlags(new Flag[0]);
	}

	@Override
	public void sendShow(CommandSender sender) {
		sender.sendMessage(ConfigurationService.STAIGHT_LINE);
		sender.sendMessage(' ' + getDisplayName(sender));
		sender.sendMessage("  §aLes factions dites \"routes\" vous permettent de traverser le monde sans obstacle.");
		sender.sendMessage(ConfigurationService.STAIGHT_LINE);
	}
	
	@Override
	public boolean addClaim(Claim claim, FPlayer fPlayer) {
		claim.setPriority(3);
		return claims.add(claim);
	}
	
	@Override
	public void save() {}
	
	@Override
	public void remove() {}
	
	public static void init() {
		north = new NorthRoad();
		south = new SouthRoad();
		east = new EastRoad();
		west = new WestRoad();
	}
	
	public static class NorthRoad extends RoadFaction {

		public NorthRoad() {
			super("RouteNord");
			
			for (World world : Bukkit.getWorlds()) {
				Environment environment = world.getEnvironment();
				
				if (environment != Environment.THE_END)
					addClaim(new Claim(this, new Location(world, -ConfigurationService.ROAD_HALF_WIDTH.get(environment), 0, -ConfigurationService.SPAWN_RADIUS_ENV.get(environment)), new Location(world, ConfigurationService.ROAD_HALF_WIDTH.get(environment), 256, -ConfigurationService.ROAD_LENGTH_ENV.get(environment))), null);
			}
			
			north = this;
		}
		
		@Override
		public String getDisplayName(CommandSender sender) {
			return "§eRoute Nord";
		}
		
		public static NorthRoad get() {
			return north;
		}
		
	}
	
	public static class EastRoad extends RoadFaction {

		public EastRoad() {
			super("RouteEst");
			
			for (World world : Bukkit.getWorlds()) {
				Environment environment = world.getEnvironment();
				
				if (environment != Environment.THE_END)
					addClaim(new Claim(this, new Location(world, ConfigurationService.SPAWN_RADIUS_ENV.get(environment), 0, -ConfigurationService.ROAD_HALF_WIDTH.get(environment) ), new Location(world, ConfigurationService.ROAD_LENGTH_ENV.get(environment), 256, ConfigurationService.ROAD_HALF_WIDTH.get(environment))), null);
			}
			
			east = this;
		}
		
		@Override
		public String getDisplayName(CommandSender sender) {
			return "§eRoute Est";
		}
		
		public static EastRoad get() {
			return east;
		}
		
	}
 	
	public static class SouthRoad extends RoadFaction {

		public SouthRoad() {
			super("RouteSud");
			
			for (World world : Bukkit.getWorlds()) {
				Environment environment = world.getEnvironment();
				
				if (environment != Environment.THE_END)
					addClaim(new Claim(this, new Location(world, ConfigurationService.ROAD_HALF_WIDTH.get(environment), 0, ConfigurationService.SPAWN_RADIUS_ENV.get(environment)), new Location(world, -ConfigurationService.ROAD_HALF_WIDTH.get(environment), 256, ConfigurationService.ROAD_LENGTH_ENV.get(environment))), null);
			}
			
			south = this;
		}
		
		@Override
		public String getDisplayName(CommandSender sender) {
			return "§eRoute Sud";
		}
		
		public static SouthRoad get() {
			return south;
		}
		
	}
	
	public static class WestRoad extends RoadFaction {

		public WestRoad() {
			super("RouteOuest");
			
			for (World world : Bukkit.getWorlds()) {
				Environment environment = world.getEnvironment();
				
				if (environment != Environment.THE_END)
					addClaim(new Claim(this, new Location(world, -ConfigurationService.SPAWN_RADIUS_ENV.get(environment), 0, ConfigurationService.ROAD_HALF_WIDTH.get(environment) ), new Location(world, -ConfigurationService.ROAD_LENGTH_ENV.get(environment), 256, -ConfigurationService.ROAD_HALF_WIDTH.get(environment))), null);
			}
			
			west = this;
		}
		
		@Override
		public String getDisplayName(CommandSender sender) {
			return "§eRoute Ouest";
		}
		
		public static WestRoad get() {
			return west;
		}
		
	}
	
}