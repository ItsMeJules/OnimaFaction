package net.onima.onimafaction.manager;

import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;

import net.onima.onimafaction.OnimaFaction;
import net.onima.onimafaction.commands.EOTWCommand;
import net.onima.onimafaction.commands.FocusCommand;
import net.onima.onimafaction.commands.LogoutCommand;
import net.onima.onimafaction.commands.PvPCommand;
import net.onima.onimafaction.commands.RaidableCommand;
import net.onima.onimafaction.commands.ReviveCommand;
import net.onima.onimafaction.commands.SOTWCommand;
import net.onima.onimafaction.commands.StaffChatCommand;
import net.onima.onimafaction.commands.TeamLocationCommand;
import net.onima.onimafaction.commands.faction.FactionExecutor;
import net.onima.onimafaction.commands.fastplant.FastPlantExecutor;
import net.onima.onimafaction.commands.lives.LiveExecutor;

public class CommandManager {
	
	private OnimaFaction plugin;
	private FactionExecutor factionExecutor;
	
	public CommandManager(OnimaFaction plugin) {
		this.plugin = plugin;
	}
	
	public void registerCommands() {
		plugin.getCommand("faction").setExecutor(factionExecutor = new FactionExecutor());
		plugin.getCommand("focus").setExecutor(new FocusCommand());
		plugin.getCommand("teamlocation").setExecutor(new TeamLocationCommand());
		plugin.getCommand("eotw").setExecutor(new EOTWCommand());
		plugin.getCommand("sotw").setExecutor(new SOTWCommand());
		plugin.getCommand("raidable").setExecutor(new RaidableCommand());
		plugin.getCommand("revive").setExecutor(new ReviveCommand());
		plugin.getCommand("logout").setExecutor(new LogoutCommand());
		plugin.getCommand("lives").setExecutor(new LiveExecutor());
		plugin.getCommand("staffchat").setExecutor(new StaffChatCommand());
		plugin.getCommand("pvp").setExecutor(new PvPCommand());
		plugin.getCommand("fastplant").setExecutor(new FastPlantExecutor());
	}
	
	public boolean forceCommand(CommandSender sender, String command, String[] args) {
		PluginCommand pluginCommand = plugin.getCommand(command);
		
		return pluginCommand.getExecutor().onCommand(sender, pluginCommand, command, args);
	}
	
	public FactionExecutor getFactionExecutor() {
		return factionExecutor;
	}

}
