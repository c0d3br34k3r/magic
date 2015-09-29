package magic;

/**
 * All two-part cards have a {@code Link} that contains additional information
 * about the linking card, and provides access to linked card. Implementations
 * of {@code Link} should be immutable.
 */
public final class Link {

	private final Card card;
	private final boolean isFirstHalf;

	/**
	 * Creates a {@code SimpleLink} with the specified card and first-half-ness.
	 */
	static Link create(Card card, boolean isFirstHalf) {
		return new Link(card, isFirstHalf);
	}

	private Link(Card linked, boolean isFirstHalf) {
		this.card = linked;
		this.isFirstHalf = isFirstHalf;
	}

	/**
	 * The other half of the linking card. The {@code Link} on the linked card
	 * is guaranteed to be nonnull.
	 */
	public Card get() {
		return card;
	}

	/**
	 * Returns {@code true} if the linking card is the "first" of the two
	 * halves.
	 */
	public boolean isFirstHalf() {
		return isFirstHalf;
	}

	@Override public String toString() {
		return (isFirstHalf ? "->" : "<-") + card;
	}

}
