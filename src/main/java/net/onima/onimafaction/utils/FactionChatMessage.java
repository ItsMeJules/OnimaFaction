package net.onima.onimafaction.utils;

import java.util.Arrays;
import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import net.onima.onimaapi.rank.RankType;
import net.onima.onimaapi.utils.ChatMessage;
import net.onima.onimaapi.utils.ConfigurationService;
import net.onima.onimaapi.utils.Methods;
import net.onima.onimafaction.faction.Faction;
import net.onima.onimafaction.faction.struct.Chat;
import net.onima.onimafaction.faction.struct.Relation;
import net.onima.onimafaction.faction.struct.Role;

public class FactionChatMessage extends ChatMessage {
	
	private Faction faction;
	private Chat chat;
	private Role role;
	private BaseComponent[] enemyComponents, allyComponents, factionComponents;

	public FactionChatMessage(Player player, String message) {
		super(player, message);
	}
	
	public FactionChatMessage(Player player) {
		super(player);
	}

	public FactionChatMessage faction(Faction faction) {
		this.faction = faction;
		return this;
	}
	
	public FactionChatMessage chat(Chat chat) {
		this.chat = chat;
		return this;
	}
	
	public FactionChatMessage role(Role role) {
		this.role = role;
		return this;
	}
	
	@Override
	public FactionChatMessage prefixMessage(String message) {
		super.prefixMessage(message);
		return this;
	}
	
	@Override
	public FactionChatMessage message(String message) {
		super.message(message);
		return this;
	}
	
	@Override
	public FactionChatMessage nameColor(ChatColor nameColor) {
		super.nameColor(nameColor);
		return this;
	}

	@Override
	public FactionChatMessage nameColor(String nameColor) {
		super.nameColor(nameColor);
		return this;
	}
	
	@Override
	public FactionChatMessage chatColor(ChatColor chatColor) {
		super.chatColor(chatColor);
		return this;
	}
	
	@Override
	public FactionChatMessage chatColor(String chatColor) {
		super.chatColor(chatColor);
		return this;
	}
	
	@Override
	public FactionChatMessage rank(RankType rank) {
		super.rank(rank);
		return this;
	}
	
	@Override
	public FactionChatMessage rankClickCommand(String rankClickCommand) {
		super.rankClickCommand(rankClickCommand);
		return this;
	}
	
	@Override
	public FactionChatMessage build() {
		if (faction == null) { 
			ComponentBuilder builder = new ComponentBuilder(ConfigurationService.NO_FACTION_CHARACTER + ' ');
			
			builder.append(rank.getPrefix());
			
			builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(rank.getDescription()).create()));
			builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, rankClickCommand));
			
			builder.append(rank == RankType.DEFAULT ? "" : " ", FormatRetention.NONE);
			builder.append(nameColor + player.getName() + "§r");
			builder.append(": ");
			builder.append(chatColor + message.toString());
			
			components = builder.create();
		} else {
			if (chat == Chat.GLOBAL) {
				for (Relation relation : Relation.values()) {
					ComponentBuilder builder = new ComponentBuilder("§e[" + relation.getColor() + faction.getName() + "§e]§r ");
					boolean jsonPre = rank != RankType.DEFAULT || rank != RankType.BOT;
					
					builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(rank.getDescription()).create()));
					builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, rankClickCommand));
					
					builder.retain(FormatRetention.NONE);
					
					builder.append(rank.getPrefix());
					
					if (jsonPre) {
						builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(rank.getDescription()).create()));
						builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, rankClickCommand));
					}
					
					builder.append(rank == RankType.DEFAULT ? "" : " ", FormatRetention.NONE);
					builder.append(nameColor + player.getName() + "§r");

					if (!jsonPre) {
						builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(rank.getDescription()).create()));
						builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, rankClickCommand));
					}
					
					builder.retain(FormatRetention.NONE);
					
					builder.append(": ");
					builder.append(chatColor + message.toString());
					
					switch (relation) {
					case ALLY:
						allyComponents = builder.create();
						break;
					case ENEMY:
						enemyComponents = builder.create();
						break;
					case MEMBER:
						factionComponents = builder.create();
						break;
					default:
						break;
					}
				}	
			} else if (chat == Chat.ALLIANCE) {
				ComponentBuilder builder = new ComponentBuilder(Relation.ALLY.getColor() + "(" + chat.getChat() + ") ");
				
				builder.append(Relation.ALLY.getColor() + "[" + faction.getName() + "] ");
				builder.append(Relation.ALLY.getColor() + role.getRole());
				builder.append(Relation.ALLY.getColor() + player.getName() + "§r");
				builder.append(": ");
				builder.append("§e" + message.toString());
				
				components = builder.create();
			} else if (chat == Chat.FACTION) {
				ComponentBuilder builder = new ComponentBuilder(Relation.MEMBER.getColor() + "(" + chat.getChat() + ") ");
				
				builder.append(Relation.MEMBER.getColor() + role.getRole());
				builder.append(Relation.MEMBER.getColor() + player.getName() + "§r");
				builder.append(": ");
				builder.append("§e" + message.toString());
				
				components = builder.create();
			}
		}
		return this;
	}
	
	public void send(Collection<CommandSender> senders) {
		if (faction == null || chat != Chat.GLOBAL) {
			for (CommandSender sender : senders)
				Methods.sendJSON(sender, components);
		} else {
			for (CommandSender sender : senders) {
				switch (faction.getRelation(player)) {
				case ALLY:
					Methods.sendJSON(sender, allyComponents);
					break;
				case ENEMY:
					Methods.sendJSON(sender, enemyComponents);
					break;
				case MEMBER:
					Methods.sendJSON(sender, factionComponents);
					break;
				default:
					break;
				}
			}
		}
	}
	
	public void send(Player... players) {
		send(Arrays.asList(players));
	}
	
}
