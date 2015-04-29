package magic.impl;

import magic.Link;

/**
 * Provides some basic functionality of the {@link Link} class.
 */
public abstract class AbstractLink implements Link {

	/**
	 * Returns a {@link String} in the format
	 * {@code [Layout] card [location]; [other location] is [linked card].}
	 * <p>
	 * Example: if the linking card is Reality of the split card Illusion &
	 * Reality, this link would read:
	 * {@code Split card Left; Right is Illusion.}
	 */
	@Override public String toString() {
		return layout() +
				" card " +
				location() +
				"; " +
				get().link().location() +
				" is " +
				get().name();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override public String location() {
		Layout layout = layout();
		return isFirstHalf()
				? layout.firstHalfName()
				: layout.secondHalfName();
	}

}
