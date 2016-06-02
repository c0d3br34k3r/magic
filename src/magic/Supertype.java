package magic;

import java.util.Set;

/**
 * All supertypes that may appear on a Magic card. Each {@link FullCharacteristics} contains a
 * {@link Set} of {@code Supertype}s.
 * <p>
 * The order of these values is such that, given any subset of them that appears
 * on an existing card, they will be in the order listed on that card. That
 * said, only one card in existence has multiple supertypes.
 * 
 * @see FullCharacteristics
 */
public enum Supertype {

	/**
	 * The Basic supertype.
	 */
	BASIC("Basic"),

	/**
	 * The Legendary supertype.
	 */
	LEGENDARY("Legendary"),

	/**
	 * The Snow supertype.
	 */
	SNOW("Snow"),

	/**
	 * The World supertype.
	 */
	WORLD("World");

	private final String name;

	private Supertype(String name) {
		this.name = name;
	}

	/**
	 * Returns a title-case representation of this supertype.
	 */
	@Override public String toString() {
		return name;
	}

}
