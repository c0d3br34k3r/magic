package magic;

public enum TextSymbol implements Symbol {

	TAP("T"),
	UNTAP("Q"),
	SNOW("S"),
	PHYREXIAN("P");

	private final String symbol;
	private final String representation;

	TextSymbol(String symbol) {
		this.symbol = symbol;
		this.representation = '{' + symbol + '}';
	}

	@Override public String toString() {
		return representation;
	}

	public String symbol() {
		return symbol;
	}

}
