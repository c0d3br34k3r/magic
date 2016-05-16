package magic;

public enum TextSymbol implements Symbol {
	
	TAP("{T}"), 
	UNTAP("{Q}"), 
	SNOW("{S}"),
	PHYREXIAN("{P}");

	private final String representation;

	TextSymbol(String representation) {
		this.representation = representation;
	}

	@Override public String toString() {
		return representation;
	}

}
