package net.onima.onimafaction.listener;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.TravelAgent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import net.onima.onimaapi.OnimaAPI;
import net.onima.onimaapi.rank.OnimaPerm;
import net.onima.onimaapi.rank.RankType;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.zone.type.Region;
import net.onima.onimafaction.events.FactionChatEvent;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.PlayerFaction;
import net.onima.onimafaction.faction.claim.Claim;
import net.onima.onimafaction.faction.claim.WildernessClaim;
import net.onima.onimafaction.faction.struct.Chat;
import net.onima.onimafaction.faction.struct.Relation;
import net.onima.onimafaction.players.FPlayer;
import net.onima.onimafaction.utils.FactionChatMessage;

public class EntityListener implements Listener {
	
	@EventHandler(ignoreCancelled = true)
	public void onEntityTarget(EntityTargetEvent event) {
		switch (event.getReason()) {
		case CLOSEST_PLAYER:
		case RANDOM_TARGET:
			Entity entity = event.getTarget();
			
			if (event.getEntity() instanceof Monster && entity instanceof Player) {
				if (Claim.getClaimAt(entity.getLocation()).getFaction().isSafeZone())
					event.setCancelled(true);
			}
			break;
		default:
			break;
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		SpawnReason reason = event.getSpawnReason();
		Location location = event.getLocation();
		Faction factionAt = Claim.getClaimAt(location).getFaction();
		
		if (reason == SpawnReason.SLIME_SPLIT) return;
		if ((factionAt.isSafeZone() || factionAt.isRoad()) && reason == SpawnReason.SPAWNER) return;
		
		if (factionAt instanceof PlayerFaction && !((PlayerFaction) factionAt).isRaidable() && event.getEntity() instanceof Monster) {
			switch (reason) {
			case SPAWNER:
			case EGG:
			case CUSTOM:
			case BUILD_WITHER:
			case BUILD_IRONGOLEM:
			case BUILD_SNOWMAN:
				return;
			default:
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerPortal(PlayerPortalEvent event) {
		if (event.getCause() == TeleportCause.NETHER_PORTAL) {
			Location from = event.getFrom();
			Location to = event.getTo();
			Player player = event.getPlayer();
			Faction fromFaction = Claim.getClaimAt(from).getFaction();
			
			if (fromFaction.isSafeZone()) {
				event.useTravelAgent(false);
				event.setTo(to.getWorld().getSpawnLocation());
				player.sendMessage("§d§oVous §7avez été téléporté au spawn.");
				return;
			}
			
			if (event.useTravelAgent() && ConfigurationService.CLAIMABLE_WORLD.contains(to.getWorld().getName())) {
				TravelAgent agent = event.getPortalTravelAgent();
				
				if (!agent.getCanCreatePortal()) return;
				if (agent.findPortal(to) != null) return;
				
				Region region = Claim.getClaimAndRegionAt(from);
				
				if (!(region instanceof WildernessClaim)) {
					PlayerFaction playerFaction = FPlayer.getPlayer(player).getFaction();
					
					if (!region.getDisplayName(player).equalsIgnoreCase(playerFaction.getDisplayName(player))) {
						player.sendMessage("§cCe portail aurait été créé dans le territoire de " + region.getDisplayName(player) + ", téléportation annulée...");
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onMemberConnect(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		FPlayer fPlayer = FPlayer.getPlayer(player);
		PlayerFaction faction = fPlayer.getFaction();
		
		event.setJoinMessage(null);
		
		if (faction != null)
			faction.broadcast(Relation.MEMBER.getColor() + "§o" + fPlayer.getRole().getRole() + fPlayer.getApiPlayer().getName() + " §eest §aconnecté.");
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onMemberDisconnect(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		FPlayer fPlayer = FPlayer.getPlayer(player);
		PlayerFaction faction = fPlayer.getFaction();
		
		event.setQuitMessage(null);
		
		if (faction != null)
			faction.broadcast(Relation.MEMBER.getColor() + "§o" + fPlayer.getRole().getRole() + fPlayer.getApiPlayer().getName() + " §eest §cdéconnecté.");
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		FPlayer fPlayer = FPlayer.getPlayer(player);
		String message = event.getMessage();
		Chat chat = fPlayer.getChat();
		
		event.setCancelled(true);
		
		if (message.startsWith("@") || chat == Chat.STAFF) {
			message = message.startsWith("@") ? message.substring(1) : message;
			
			OnimaAPI.broadcast("§a[" + fPlayer.getApiPlayer().getColoredName(true) + "§a] §e» §b" + message, OnimaPerm.ONIMAFACTION_STAFFCHAT_COMMAND);
			return;
		}
		
		PlayerFaction faction = fPlayer.getFaction();
		Set<CommandSender> toSend = new HashSet<>();
		
		if (chat != Chat.GLOBAL) {
			if (message.startsWith("!")) {
				message = message.substring(1);
				toSend.addAll(Bukkit.getOnlinePlayers());
			}
			
			if (chat == Chat.ALLIANCE) {
				toSend.addAll(faction.getOnlineMembers(null).stream().map(reader -> reader.getApiPlayer().toPlayer()).collect(Collectors.toList()));
				
				for (String allyName : faction.getAllies()) {
					PlayerFaction ally = (PlayerFaction) Faction.getFaction(allyName);
					
					toSend.addAll(ally.getOnlineMembers(null).stream().map(reader -> reader.getApiPlayer().toPlayer()).collect(Collectors.toList()));
				}
			} else if (chat == Chat.FACTION)
				toSend.addAll(faction.getOnlineMembers(null).stream().map(reader -> reader.getApiPlayer().toPlayer()).collect(Collectors.toList()));
		} else
			toSend.addAll(Bukkit.getOnlinePlayers());
		
		toSend.add(Bukkit.getConsoleSender());
		
		FactionChatEvent chatEvent = new FactionChatEvent(player, faction, toSend, chat, message);
		Bukkit.getPluginManager().callEvent(chatEvent);
		
		if (chatEvent.isCancelled())
			return;
		
		RankType rank = chatEvent.getRankType();
		
		new FactionChatMessage(player.getName())
		.faction(faction)
		.rank(rank)
		.canUseColor(OnimaPerm.ONIMAAPI_COLORED_CHAT.has(player))
		.rankClickCommand("/menu ranks")
		.message(message)
		.chat(chat)
		.role(fPlayer.getRole())
		.chatColor(rank.getSpeakingColor())
		.nameColor(rank.getNameColor())
		.build().send(toSend);
	}
	
}
