package net.onima.onimafaction.faction.struct;

public enum Chat {
	
	GLOBAL("Global"),
	STAFF("Staff"),
	FACTION("Faction"),
	ALLIANCE("Alliance");
	
	private String chat;
	
	private Chat(String chat) {
		this.chat = chat;
	}
	
	public String getChat() {
		return chat;
	}
	
	public Chat nextChat() {
		switch(this) {
		case GLOBAL:
			return FACTION;
		case FACTION:
			return ALLIANCE;
		default:
			return GLOBAL;
		}
	}
	
	public static Chat fromString(String name) {
		switch(name) {
		case "global":
		case "public":
		case "pc":
		case "p":
		case "gc":
		case "g":
			return GLOBAL;
		case "staff":
		case "s":
		case "sc":
			return STAFF;
		case "faction":
		case "f":
		case "fc": 
			return FACTION;
		case "alliance":
		case "ally":
		case "a":
		case "ac":
			return ALLIANCE;
		default:
			return null;
		}
	}

}
