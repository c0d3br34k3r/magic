package magic;

public enum Layout {

	NORMAL("Normal"),
	
	LEVELER("Leveler"),
	
	SPLIT("Split") {

		@Override public String format(Characteristics characs1, Characteristics characs2) {
			return characs1.name() + " // " + characs2.name();
		}
	},
	FLIP("Flip"),
	
	DOUBLE_FACED("Double-faced");

	private final String name;

	private Layout(String name) {
		this.name = name;
	}

	/**
	 * Returns a title-case representation of this layout.
	 */
	@Override public String toString() {
		return name;
	}

	public String format(Characteristics characs1, Characteristics characs2) {
		return characs1.name();
	}

}
