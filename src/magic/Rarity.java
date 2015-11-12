package magic;

/**
 * All rarities at which a Magic card may be printed. Each {@link Printing}
 * specifies one {@code Rarity}.
 * 
 * @see Printing
 */
public enum Rarity {

	/**
	 * The rarity used by Basic Lands ({@code L}).
	 */
	BASIC_LAND("Basic Land", 'L'),
	/**
	 * The rarity used by Common cards ({@code C}).
	 */
	COMMON("Common", 'C'),
	/**
	 * The rarity used by Uncommon cards ({@code U}).
	 */
	UNCOMMON("Uncommon", 'U'),
	/**
	 * The rarity used by Rare cards ({@code R}).
	 */
	RARE("Rare", 'R'),
	/**
	 * The rarity used by Mythic Rare cards ({@code M}).
	 */
	MYTHIC_RARE("Mythic Rare", 'M'),
	/**
	 * The rarity used by Special cards ({@code S}), most notably the Time
	 * Spiral "Timeshifted" cards.
	 */
	SPECIAL("Special", 'S');

	private final String name;
	private final char code;

	Rarity(String name, char code) {
		this.name = name;
		this.code = code;
	}

	/**
	 * Returns a title-case representation of this rarity.
	 */
	@Override public String toString() {
		return name;
	}

	/**
	 * Returns the one-letter code of this rarity.
	 */
	public char code() {
		return code;
	}

}
