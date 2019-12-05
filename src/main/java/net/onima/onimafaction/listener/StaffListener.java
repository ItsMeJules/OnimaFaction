package net.onima.onimafaction.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.onima.onimaapi.OnimaAPI;
import net.onima.onimaapi.commands.SocialSpyCommand;
import net.onima.onimaapi.event.chat.PrivateMessageEvent;
import net.onima.onimaapi.players.APIPlayer;
import net.onima.onimaapi.players.utils.PlayerOption;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.rank.RankType;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimaapi.utils.PrivateMessage;
import net.onima.onimafaction.commands.faction.arguments.staff.FactionChatSpyArgument;
import net.onima.onimafaction.events.FactionChatEvent;
import net.onima.onimafaction.events.FactionDisbandEvent;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.struct.Chat;
import net.onima.onimafaction.players.FPlayer;
import net.onima.onimafaction.players.OfflineFPlayer;

public class StaffListener implements Listener {
	
	@EventHandler(ignoreCancelled = true)
	public void onFactionDisband(FactionDisbandEvent event) {
		for (OfflineFPlayer offline : OfflineFPlayer.getDisconnectedOfflineFPlayers()) 
			offline.getFactionSpying().remove(event.getFaction().getName());
	}
	
	@EventHandler
	public void onPrivateMessage(PrivateMessageEvent event) {
		CommandSender sender = event.getSender();
		CommandSender receiver = event.getReceiver();
		List<CommandSender> spies = APIPlayer.getOnlineAPIPlayers().stream()
				.filter(apiPlayer -> apiPlayer.getOptions().getBoolean(PlayerOption.GlobalOptions.SOCIAL_SPY))
				.map(APIPlayer::toPlayer)
				.filter(spy -> !spy.equals(receiver))
				.filter(spy -> !spy.equals(sender))
				.collect(Collectors.toList());
		String message = event.getMessage();
		
		if (SocialSpyCommand.isConsoleSpying())
			spies.add(Bukkit.getConsoleSender());
		
		RankType senderRank = RankType.CONSOLE, receiverRank = RankType.CONSOLE;
		
		if (sender instanceof Player) {
			APIPlayer apiPlayer = APIPlayer.getPlayer((Player) sender);

			if (apiPlayer.getDisguiseManager().isDisguised())
				senderRank = apiPlayer.getDisguiseManager().getRankType();
			else
				senderRank = apiPlayer.getRank().getRankType();
		}
		
		if (receiver instanceof Player) {
			APIPlayer apiPlayer = APIPlayer.getPlayer((Player) receiver);
			
			if (apiPlayer.getDisguiseManager().isDisguised())
				receiverRank = apiPlayer.getDisguiseManager().getRankType();
			else
				receiverRank = apiPlayer.getRank().getRankType();	
		}
		
		String format = PrivateMessage.spyFormat(message, Methods.getName(sender, true), Methods.getName(receiver, true), senderRank, receiverRank);
		
		if (!OnimaPerm.CHAT_FILTER_BYPASS.has(event.getSender()) && OnimaAPI.getInstance().getChatManager().shouldFilter(message)) {
			if (spies.removeIf(spy -> OnimaPerm.CHAT_FILTER_BYPASS.has(spy)))
				OnimaAPI.broadcast("§c§l[Filtré] " + format, OnimaPerm.CHAT_FILTER_BYPASS);
		}
		
		for (CommandSender spy : spies)
			spy.sendMessage("§7§o(Spy) " + format);
	}
	
	@EventHandler
	public void onFactionChatSpy(FactionChatEvent event) {
		if (event.getChat() == Chat.STAFF)
			return;
		
		Player sender = (Player) event.getSender();
		PlayerFaction faction = event.getFaction();
		String message = event.getMessage();
		String format = "§7(" + event.getChat().getChat() + "§7) " + " §e§o[§d§o" + (faction == null ? ConfigurationService.NO_FACTION_CHARACTER : faction.getName()) + "§e§o] " + APIPlayer.getPlayer(sender).getDisplayName(true) + "§r: " + message;
		
		if (event.getChat() == Chat.GLOBAL) {
			if (!OnimaPerm.CHAT_FILTER_BYPASS.has(event.getSender()) && OnimaAPI.getInstance().getChatManager().shouldFilter(message))
				OnimaAPI.broadcast("§c§l[Filtré] " + format, OnimaPerm.CHAT_FILTER_BYPASS);
			
			return;
		}
		
		Collection<CommandSender> readers = event.getReaders();
		Collection<CommandSender> mightSpy = new ArrayList<>(Bukkit.getOnlinePlayers());
		
		mightSpy.add(Bukkit.getConsoleSender());
		mightSpy.removeAll(readers);
		
		if (!OnimaPerm.CHAT_FILTER_BYPASS.has(event.getSender()) && OnimaAPI.getInstance().getChatManager().shouldFilter(message)) {
			if (mightSpy.removeIf(spy -> OnimaPerm.CHAT_FILTER_BYPASS.has(spy)))
				OnimaAPI.broadcast("§c§l[Filtré] " + format, OnimaPerm.CHAT_FILTER_BYPASS);
		}

		for (CommandSender spier : mightSpy) {
			List<String> spying = spier instanceof ConsoleCommandSender ? FactionChatSpyArgument.getConsoleSpies() : FPlayer.getPlayer((Player) spier).getFactionSpying();
			
			if (spying.contains("all") || spying.contains(faction.getName()))
				spier.sendMessage("§7§o(Spy) " + format);
		}
	}
	
}
