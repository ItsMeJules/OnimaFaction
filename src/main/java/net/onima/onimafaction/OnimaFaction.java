package net.onima.onimafaction;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.onima.onimaapi.OnimaAPI;
import net.onima.onimaapi.cooldown.utils.Cooldown;
import net.onima.onimaapi.gui.PacketMenu;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.time.Time;
import net.onima.onimafaction.cooldowns.ArcherJumpCooldown;
import net.onima.onimafaction.cooldowns.ArcherMarkCooldown;
import net.onima.onimafaction.cooldowns.ArcherSpeedCooldown;
import net.onima.onimafaction.cooldowns.BardItemCooldown;
import net.onima.onimafaction.cooldowns.ClassWarmupCooldown;
import net.onima.onimafaction.cooldowns.CombatTagCooldown;
import net.onima.onimafaction.cooldowns.EnderPearlCooldown;
import net.onima.onimafaction.cooldowns.FactionHomeCooldown;
import net.onima.onimafaction.cooldowns.FactionStuckCooldown;
import net.onima.onimafaction.cooldowns.InsideStormCooldown;
import net.onima.onimafaction.cooldowns.LogoutCooldown;
import net.onima.onimafaction.cooldowns.MedicReviveCooldown;
import net.onima.onimafaction.cooldowns.PvPTimerCooldown;
import net.onima.onimafaction.cooldowns.ReaperModeCooldown;
import net.onima.onimafaction.cooldowns.ReaperPowerCooldown;
import net.onima.onimafaction.cooldowns.ReaperStealthCooldown;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.type.RoadFaction;
import net.onima.onimafaction.faction.type.SafeZoneFaction;
import net.onima.onimafaction.faction.type.WarZoneFaction;
import net.onima.onimafaction.faction.type.WildernessFaction;
import net.onima.onimafaction.gui.FactionGUIMenu;
import net.onima.onimafaction.gui.armorclass.ArcherMenu;
import net.onima.onimafaction.gui.armorclass.ArmorClassMenu;
import net.onima.onimafaction.gui.armorclass.BardMenu;
import net.onima.onimafaction.gui.armorclass.MineurMenu;
import net.onima.onimafaction.gui.armorclass.ReaperMenu;
import net.onima.onimafaction.gui.armorclass.RogueMenu;
import net.onima.onimafaction.manager.CommandManager;
import net.onima.onimafaction.manager.ListenerManager;
import net.onima.onimafaction.players.FPlayer;
import net.onima.onimafaction.players.OfflineFPlayer;
import net.onima.onimafaction.task.BardPowerTask;
import net.onima.onimafaction.timed.FactionServerEvent;
import net.onima.onimafaction.timed.event.BattleRoyale;
import net.onima.onimafaction.timed.event.EOTW;
import net.onima.onimafaction.timed.event.SOTW;
import net.onima.onimafaction.workload.manager.WorkloadManager;

public class OnimaFaction extends JavaPlugin {
	
	private static OnimaFaction instance;
	
	private ListenerManager listenerManager;
	private CommandManager commandManager;
	private EOTW eotw;
	private SOTW sotw;
	private BattleRoyale battleRoyale;
	private FactionServerEvent factionServerEvent;
	private WorkloadManager workloadManager;
	
	@Override
	public void onEnable() {
		if (!Bukkit.getOnlineMode()) {
			getPluginLoader().disablePlugin(this);
			return;
		}
		
		long started = System.currentTimeMillis();
		OnimaAPI.sendConsoleMessage("====================§6[§3ACTIVATION§6]§r====================", ConfigurationService.ONIMAFACTION_PREFIX);
		instance = this;
		registerManager();
		OnimaAPI.sendConsoleMessage("====================§6[§3ACTIVE EN (" + (System.currentTimeMillis() - started) + "ms)§6]§r====================", ConfigurationService.ONIMAFACTION_PREFIX);
	}
	
	private void registerManager() {
		(listenerManager = new ListenerManager(this)).registerListener();
		(commandManager = new CommandManager(this)).registerCommands();
		(workloadManager = new WorkloadManager(OnimaAPI.getDistributor())).registerWorkloads();
		new BardPowerTask().runTaskTimerAsynchronously(this, 40L, 20L);
//		new FastPlantTask().runTaskTimer(this, 40L, 20L);
		
		eotw = new EOTW(4 * Time.HOUR, 30 * Time.MINUTE);
		sotw = new SOTW(3 * Time.HOUR, 15 * Time.MINUTE);
		battleRoyale = new BattleRoyale();
		
		ArmorClassMenu menu = new ArmorClassMenu();
		
		PacketMenu.getStaticMenus().add(menu);
		PacketMenu.getStaticMenus().add(new ArcherMenu());
		PacketMenu.getStaticMenus().add(new BardMenu());
		PacketMenu.getStaticMenus().add(new MineurMenu());
		PacketMenu.getStaticMenus().add(new ReaperMenu());
		PacketMenu.getStaticMenus().add(new RogueMenu());
		PacketMenu.getStaticMenus().add(new FactionGUIMenu());
		
		menu.registerClasses();
		
		Cooldown.register(new ArcherJumpCooldown());
		Cooldown.register(new ArcherMarkCooldown());
		Cooldown.register(new ArcherSpeedCooldown());
		Cooldown.register(new BardItemCooldown());
		Cooldown.register(new ClassWarmupCooldown());
		Cooldown.register(new CombatTagCooldown());
		Cooldown.register(new EnderPearlCooldown());
		Cooldown.register(new FactionHomeCooldown());
		Cooldown.register(new FactionStuckCooldown());
		Cooldown.register(new InsideStormCooldown());
		Cooldown.register(new LogoutCooldown());
		Cooldown.register(new MedicReviveCooldown());
		Cooldown.register(new PvPTimerCooldown());
		Cooldown.register(new ReaperModeCooldown());
		Cooldown.register(new ReaperPowerCooldown());
		Cooldown.register(new ReaperStealthCooldown());
		
		PlayerFaction.loadDeathbans();
		WildernessFaction.init();
		SafeZoneFaction.init();
		RoadFaction.init();
		WarZoneFaction.init();
	}

	@Override
	public void onDisable() {
		OnimaAPI.sendConsoleMessage("====================§6[§cDESACTIVATION§6]§r====================", ConfigurationService.ONIMAFACTION_PREFIX);
	}
	
	public static OnimaFaction getInstance() {
		return instance;
	}
	
	public ListenerManager getListenerManager() {
		return listenerManager;
	}
	
	public CommandManager getCommandManager() {
		return commandManager;
	}

	public EOTW getEOTW() {
		return eotw;
	}
	
	public SOTW getSOTW() {
		return sotw;
	}

	public BattleRoyale getBattleRoyale() {
		return battleRoyale;
	}
	
	public FactionServerEvent getFactionServerEvent() {
		return factionServerEvent;
	}
	
	public void setFactionServerEvent(FactionServerEvent factionServerEvent) {
		this.factionServerEvent = factionServerEvent;
	}
	
	public WorkloadManager getWorkloadManager() {
		return workloadManager;
	}
	
	public static String getDisplay(OfflineFPlayer offlineFPlayer, OfflineFPlayer viewer) {
		PlayerFaction faction = offlineFPlayer.getFaction();
		
		if (faction == null)
			return ConfigurationService.NO_FACTION_CHARACTER;
		
		return faction.getRelation(viewer.getFaction()).getColor() + faction.getName();
	}
	
	public static boolean hasNearbyEnemies(FPlayer fPlayer, double distance) {
		PlayerFaction faction = fPlayer.getFaction();
		Player player = fPlayer.getApiPlayer().toPlayer();
		
		for (Entity entity : player.getNearbyEntities(distance, distance, distance)) {
			if (!(entity instanceof Player))
				continue;
			
			Player target = (Player) entity;
			
			if (!target.canSee(player) && !player.canSee(target))
				continue;
			
			if (faction == null || FPlayer.getPlayer(target).getFaction() != faction)
				return true;
		}
		
		return false;
	}

}