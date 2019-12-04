package net.onima.onimafaction.manager;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.listener.ArmorClassListener;
import net.onima.onimafaction.listener.BardListener;
import net.onima.onimafaction.listener.ClaimListener;
import net.onima.onimafaction.listener.CombatLoggerListener;
import net.onima.onimafaction.listener.DeathListener;
import net.onima.onimafaction.listener.DeathbanJoinListener;
import net.onima.onimafaction.listener.EggAdvantageListener;
import net.onima.onimafaction.listener.EntityListener;
import net.onima.onimafaction.listener.EnvironementListener;
import net.onima.onimafaction.listener.MineurListener;
import net.onima.onimafaction.listener.ModListener;
import net.onima.onimafaction.listener.ProtectionListener;
import net.onima.onimafaction.listener.ReaperListener;
import net.onima.onimafaction.listener.RegionBorderListener;
import net.onima.onimafaction.listener.RegionListener;
import net.onima.onimafaction.listener.RogueListener;
import net.onima.onimafaction.listener.SpawnerListener;
import net.onima.onimafaction.listener.StaffListener;
import net.onima.onimafaction.listener.SubclaimSignListener;
import net.onima.onimafaction.listener.WorlChangerListener;
import net.onima.onimafaction.listener.fixes.PhaseFixListener;
import net.onima.onimafaction.listener.fixes.PortalTrapFixListener;

/**
 * This class handles all the bukkit's listeners.
 */
public class ListenerManager {
	
	private OnimaFaction plugin;
	private PluginManager pm;
	
	public ListenerManager(OnimaFaction plugin) {
		this.plugin = plugin;
		this.pm = Bukkit.getPluginManager();
	}
	
	public void registerListener() {
		pm.registerEvents(new RegionListener(), plugin);
		pm.registerEvents(new ClaimListener(), plugin);
		pm.registerEvents(new EnvironementListener(), plugin);
		pm.registerEvents(new EntityListener(), plugin);
		pm.registerEvents(new StaffListener(), plugin);
		pm.registerEvents(new ArmorClassListener(), plugin);
		pm.registerEvents(new MineurListener(), plugin);
		pm.registerEvents(new BardListener(), plugin);
		pm.registerEvents(new ReaperListener(), plugin);
		pm.registerEvents(new RogueListener(), plugin);
		pm.registerEvents(new ModListener(), plugin);
		pm.registerEvents(new RegionBorderListener(), plugin);
		pm.registerEvents(new ProtectionListener(), plugin);
		pm.registerEvents(new SubclaimSignListener(), plugin);
		pm.registerEvents(new SpawnerListener(), plugin);
		pm.registerEvents(new DeathListener(), plugin);
		pm.registerEvents(new PhaseFixListener(), plugin);
		pm.registerEvents(new PortalTrapFixListener(), plugin);
		pm.registerEvents(new DeathbanJoinListener(), plugin);
		pm.registerEvents(new WorlChangerListener(), plugin);
		pm.registerEvents(new EggAdvantageListener(), plugin);
		pm.registerEvents(new CombatLoggerListener(), plugin);
	}
	
}
