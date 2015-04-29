package magic.impl;

import magic.Expansion;

/**
 * Provides some basic functionality of the {@link Expansion} class.
 */
public abstract class AbstractExpansion implements Expansion {

	/** 
	 * Returns this expansion's name.
	 */
	@Override public String toString() {
		return name();
	}

	/**
	 * Provides a natural ordering for {@code Expansion}s based on their release date.
	 */
	@Override public int compareTo(Expansion o) {
		int result = releaseDate().compareTo(o.releaseDate());
		if (result == 0) {
			result = name().compareTo(o.name());
		}
		return result;
	}

}
