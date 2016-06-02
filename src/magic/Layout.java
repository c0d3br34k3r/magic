package magic;

public enum Layout {

	NORMAL("Normal"),

	LEVELER("Leveler"),

	SPLIT("Split") {

		@Override public String format(Characteristics charcs1, Characteristics charcs2) {
			return charcs1.name() + " // " + charcs2.name();
		}

		@Override public boolean hasMain() {
			return true;
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

	public String format(Characteristics charcs1, Characteristics charcs2) {
		return charcs1.name();
	}

	public boolean hasMain() {
		return true;
	}

}
