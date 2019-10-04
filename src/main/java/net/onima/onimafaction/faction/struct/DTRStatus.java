package net.onima.onimafaction.faction.struct;

public enum DTRStatus {
	
	FULL("§2\u25C0"),
	REGENERATING("§6\u21ea"),
	FROZEN("§c\u25a0");
	
	private String symbol;

	private DTRStatus(String symbol) {
		this.symbol = symbol;
	}
	
	public String getSymbol() {
		return symbol;
	}

}
