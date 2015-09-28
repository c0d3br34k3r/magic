package magic;

import java.util.Set;

/**
 * All card types that may appear on a Magic card. Each {@link Card} has a
 * {@link Set} of {@code Type}s.
 * <p>
 * The order of these values is such that, given any subset of them that appears
 * on an existing card, they will be in the order listed on that card.
 * 
 * @see Card
 */
public enum Type {

	/**
	 * The Tribal card type.
	 */
	TRIBAL("Tribal"),
	
	/**
	 * The Instant card type.
	 */
	INSTANT("Instant"),
	
	/**
	 * The Sorcery card type.
	 */
	SORCERY("Sorcery"),
	
	/**
	 * The Enchantment card type.
	 */
	ENCHANTMENT("Enchantment"),
	
	/**
	 * The Artifact card type.
	 */
	ARTIFACT("Artifact"),
	
	/**
	 * The Land card type.
	 */
	LAND("Land"),
	
	/**
	 * The Creature card type.
	 */
	CREATURE("Creature"),
	
	/**
	 * The Planeswalker card type.
	 */
	PLANESWALKER("Planeswalker"),
	
	/**
	 * The Conspiracy card type.
	 */
	CONSPIRACY("Conspiracy");

	private final String name;

	private Type(String name) {
		this.name = name;
	}

	/**
	 * Returns a title-case representation of this type.
	 */
	@Override public String toString() {
		return name;
	}

}
