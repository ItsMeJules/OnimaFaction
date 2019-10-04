package net.onima.onimafaction.faction.struct;

import org.bukkit.ChatColor;

public enum Relation {
	
	MEMBER(ChatColor.DARK_GREEN),
	ALLY(ChatColor.BLUE),
	ENEMY(ChatColor.YELLOW);
	
	private ChatColor color;

	private Relation(ChatColor color) {
		this.color = color;
	}
	
	public ChatColor getColor() {
		return color;
	}
	
	public boolean isMember() {
		return this == MEMBER;
	}
	
	public boolean isAlly() {
		return this == ALLY;
	}
	
	public boolean isEnemy() {
		return this == ENEMY;
	}
	
}
