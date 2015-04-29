package magic.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import magic.Card;

/**
 * Simple immutable implementation of {@link magic.Link}.
 */
public final class SimpleLink extends AbstractLink {

	private final Card card;
	private final Layout layout;
	private final boolean isFirstHalf;

	/**
	 * Creates a {@code SimpleLink} with the specified card, layout, and
	 * first-half-ness.
	 */
	public static SimpleLink create(Card card, Layout layout, boolean isFirstHalf) {
		return new SimpleLink(card, layout, isFirstHalf);
	}

	private SimpleLink(Card linked, Layout layout, boolean isFirstHalf) {
		this.card = checkNotNull(linked);
		this.layout = checkNotNull(layout);
		this.isFirstHalf = isFirstHalf;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override public Card get() {
		return card;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override public Layout layout() {
		return layout;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override public boolean isFirstHalf() {
		return isFirstHalf;
	}
	
}
