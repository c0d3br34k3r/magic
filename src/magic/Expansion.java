package magic;

import org.joda.time.LocalDate;

import com.google.common.collect.ListMultimap;

/**
 * An object containing the attributes of expansion. It does not, however,
 * contain the cards within the expansion. Implementations of {@code Expansion}
 * should be immutable.
 * <p>
 * Because each instance of {@code Expansion} should be unique, it may be useful
 * to have {@code equals} and {@code hashCode} default to their identity-based
 * implementations in {@link Object}.
 */
public final class Expansion implements Comparable<Expansion> {

	private final String name;
	private final ListMultimap<WholeCard, Printing> cards;
	private final String code;
	private final LocalDate releaseDate;
	private final Release type;
	private final BorderColor borderColor;
	private final boolean hasCollectorNumbers;
	private final boolean isPhysical;
	private final boolean hasBooster;
	
	private Expansion() {
		
	}
	
	public String name() {
		return name;
	}

	public ListMultimap<WholeCard, Printing> cards() {
		return cards;
	}

	public String code() {
		return code;
	}

	public LocalDate releaseDate() {
		return releaseDate;
	}

	public Release type() {
		return type;
	}

	public BorderColor borderColor() {
		return borderColor;
	}

	public boolean hasCollectorNumbers() {
		return hasCollectorNumbers;
	}

	public boolean isPhysical() {
		return isPhysical;
	}

	public boolean hasBooster() {
		return hasBooster;
	}

	/**
	 * Returns this expansion's name.
	 */
	@Override public String toString() {
		return name();
	}

	/**
	 * Provides a natural ordering for {@code Expansion}s based on their release
	 * date.
	 */
	@Override public int compareTo(Expansion o) {
		int result = releaseDate().compareTo(o.releaseDate());
		if (result == 0) {
			result = name().compareTo(o.name());
		}
		return result;
	}

	/**
	 * The two possible colors of a card's border.
	 */
	public enum BorderColor {
		BLACK("Black"),
		WHITE("White");

		private final String name;

		private BorderColor(String name) {
			this.name = name;
		}

		@Override public String toString() {
			return name;
		}
	}

	/**
	 * All types of releases.
	 */
	public enum Release {

		/**
		 * Used for Core Set releases.
		 * <p>
		 * Examples:
		 * <ul>
		 * <li>Limited Alpha Edition</li>
		 * <li>Ninth Edition</li>
		 * <li>Magic 2012</li>
		 * </ul>
		 */
		CORE_SET("Core Set"),

		/**
		 * Used for non-core set releases that contribute to the standard
		 * environment. Expansions from Ice Age forward are always part of a
		 * block.
		 * <p>
		 * Examples:
		 * <ul>
		 * <li>The Dark</li>
		 * <li>Urza's Saga</li>
		 * <li>Zendikar</li>
		 * </ul>
		 */
		EXPANSION("Expansion"),

		/**
		 * Used for booster expansions of reprints.
		 * <p>
		 * Examples:
		 * <ul>
		 * <li>Chronicles</li>
		 * <li>Modern Masters</li>
		 * </ul>
		 */
		REPRINT("Reprint"),

		/**
		 * Used to denote the "expansion" containing the six cards that were
		 * originally printed for promotions.
		 * <p>
		 * These cards are:
		 * <ul>
		 * <li>Arena</li>
		 * <li>Giant Badger</li>
		 * <li>Mana Crypt</li>
		 * <li>Nalathni Dragon</li>
		 * <li>Sewers of Estark</li>
		 * <li>Windseeker Centaur</li>
		 * </ul>
		 */
		PROMOTIONAL("Promotional"),

		/**
		 * Used for releases of starter-level cards.
		 * 
		 * <p>
		 * Examples:
		 * <ul>
		 * <li>Portal</li>
		 * <li>Portal Three Kingdoms</li>
		 * <li>Starter 2000</li>
		 * </ul>
		 */
		STARTER("Starter"),

		/**
		 * Used for releases of special preconstructed decks.
		 * <p>
		 * Examples:
		 * <ul>
		 * <li>Battle Royale Box Set</li>
		 * <li>Deckmasters</li>
		 * <li>Modern Event Deck 2014</li>
		 * </ul>
		 */
		BOX("Box"),

		/**
		 * Used for Duel Decks releases.
		 * <p>
		 * Examples:
		 * <ul>
		 * <li>Duel Decks: Elves vs. Goblins</li>
		 * <li>Duel Decks: Phyrexia vs. the Coalition</li>
		 * <li>Duel Decks: Sorin vs. Tibalt</li>
		 * </ul>
		 * 
		 */
		DUEL_DECKS("Duel Decks"),

		/**
		 * Used for Masters Edition releases. Masters Edition expansions are
		 * online only.
		 * <p>
		 * Examples:
		 * <ul>
		 * <li>Masters Edition</li>
		 * <li>Masters Edition II</li>
		 * <li>Vintage Masters</li>
		 * </ul>
		 */
		MASTERS_EDITION("Masters Edition"),

		/**
		 * Used for From the Vault releases.
		 * <p>
		 * Examples:
		 * <ul>
		 * <li>From the Vault: Dragons</li>
		 * <li>From the Vault: Legends</li>
		 * <li>From the Vault: Twenty</li>
		 * </ul>
		 */
		FROM_THE_VAULT("From the Vault"),

		/**
		 * Used for the (discontinued) Premium Deck Series releases.
		 * <p>
		 * Examples (the only ones):
		 * <ul>
		 * <li>Premium Deck Series: Slivers</li>
		 * <li>Premium Deck Series: Fire and Lightning</li>
		 * <li>Premium Deck Series: Graveborn</li>
		 * </ul>
		 */
		PREMIUM_DECK_SERIES("Premium Deck Series"),

		/**
		 * Used for Planechase releases.
		 * <p>
		 * Examples:
		 * <ul>
		 * <li>Planechase</li>
		 * <li>Planechase 2012 Edition</li>
		 * </ul>
		 */
		PLANECHASE("Planechase"),

		/**
		 * Used for the Archenemy release.
		 */
		ARCHENEMY("Archenemy"),

		/**
		 * Used for releases of Commander products.
		 * <p>
		 * Examples:
		 * <ul>
		 * <li>Magic: The Gathering-Commander</li>
		 * <li>Commander's Arsenal</li>
		 * <li>Commander 2013 Edition</li>
		 * </ul>
		 */
		COMMANDER("Commander"),

		/**
		 * Used for the Conspiracy release.
		 */
		CONSPIRACY("Conspiracy"),;

		private final String name;

		private Release(String name) {
			this.name = name;

		}

		/**
		 * Returns a title-case representation of this release type.
		 */
		@Override public String toString() {
			return name;
		}
	}
	
	public static class Builder {
		private String name;
		private ListMultimap<WholeCard, Printing> cards;
		private String code;
		private LocalDate releaseDate;
		private Release type;
		private BorderColor borderColor;
		private boolean hasCollectorNumbers;
		private boolean isPhysical;
		private boolean hasBooster;
	}

}
