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
	TRIBAL("Tribal", false),
	
	/**
	 * The Instant card type.
	 */
	INSTANT("Instant", false),
	
	/**
	 * The Sorcery card type.
	 */
	SORCERY("Sorcery", false),
	
	/**
	 * The Enchantment card type.
	 */
	ENCHANTMENT("Enchantment", true),
	
	/**
	 * The Artifact card type.
	 */
	ARTIFACT("Artifact", true),
	
	/**
	 * The Land card type.
	 */
	LAND("Land", true),
	
	/**
	 * The Creature card type.
	 */
	CREATURE("Creature", true),
	
	/**
	 * The Planeswalker card type.
	 */
	PLANESWALKER("Planeswalker", true),
	
	/**
	 * The Conspiracy card type.
	 */
	CONSPIRACY("Conspiracy", false);

	private final String name;
	private final boolean isPermanent;

	private Type(String name, boolean isPermanent) {
		this.name = name;
		this.isPermanent = isPermanent;
	}
	
	public boolean isPermanent() {
		return isPermanent;
	}

	/**
	 * Returns a title-case representation of this type.
	 */
	@Override public String toString() {
		return name;
	}

}
