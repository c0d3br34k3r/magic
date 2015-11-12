package magic;

/**
 * All two-part cards have a {@code Link} that contains additional information
 * about the linking card, and provides access to linked card. Implementations
 * of {@code Link} should be immutable.
 */
public final class Link {

	private final Card card;
	private final boolean isFirstHalf;

	Link(Card linked, boolean isFirstHalf) {
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
		return (isFirstHalf ? "first" : "second") + " half; other half is " + card.name();
	}

}
