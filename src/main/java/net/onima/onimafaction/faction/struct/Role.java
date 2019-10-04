package net.onima.onimafaction.faction.struct;

public enum Role {
	
	LEADER("Leader", "***", 4),
	COLEADER("Co-Leader", "**", 3),
	OFFICER("Officier", "*", 2),
	MEMBER("Membre", "+", 1), 
	NO_ROLE("NoRole", "", 5);
	
	private String name, role;
	private int value;

	private Role(String name, String role, int value) {
		this.name = name;
		this.role = role;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}

	public String getRole() {
		return role;
	}

	public int getValue() {
		return value;
	}
	
	public boolean isAtLeast(Role role) {
		return role.value <= value;
	}
	
	public boolean isAtMost(Role role) {
		return role.value >= value;
	}
	
	public static Role fromValue(int value) {
		switch (value) {
		case 4:
			return LEADER;
		case 3:
			return COLEADER;
		case 2:
			return OFFICER;
		case 1:
			return MEMBER;
		default:
			return NO_ROLE;
		}
	}
	
	public static Role fromString(String name) {
		switch (name) {
		case "Leader":
		case "LEADER":
			return LEADER;
		case "Co-Leader":
		case "COLEADER":
			return COLEADER;
		case "Officier":
		case "OFFICER":
			return OFFICER;
		case "Membre":
		case "MEMBER":
			return MEMBER;
		case "NoRole":
		case "NO_ROLE":
			return Role.NO_ROLE;
		default:
			return null;
		}
	}

	public static String stripRole(String name) {
		return name.replace("*", "").replace("+", "");
	}
	
}
