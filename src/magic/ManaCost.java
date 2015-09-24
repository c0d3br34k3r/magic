package magic;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.Multiset;

/**
 * An immutable object representing a mana cost. A {@code ManaCost} has two
 * parts: an {@code int} representing the amount of colorless mana, and a
 * {@link Multiset} containing all other mana symbols.
 * <p>
 * This design choice was made to avoid having 16 separate colorless mana
 * symbols. This has the effect of making it hard to differentiate the zero mana
 * cost from the empty or null mana cost internally, as both have a colorless
 * value of {@code 0} and an empty collection of symbols. However, in every
 * other mana cost, the zero colorless mana symbol <code>{0}</code> is never
 * seen (i.e, a card that costs one white mana and no colorless mana simply
 * costs <code>{W}</code>, not <code>{0}{W}</code>). This implementation treats
 * <code>{0}</code> not as a symbol within a mana cost, but as a special marker
 * mana cost.
 * <p>
 * These two special cases are constants within the class, but they can also be
 * obtained naturally through the static factory methods.
 * <p>
 * While it may seem strange at first, [screw it, switching to first-person] I
 * believe that this is the most natural way to represent mana costs, and I put
 * quite a bit of thought into it. I don't think of my handling of the two
 * special mana costs as a workaround for my implementation, but rather I see
 * the way we normally think about colorless mana as a misrepresentation of how
 * it really is. I think the only other viable way to do this is to have an
 * {@link Optional} of {@link Integer}, representing the colorless mana as more
 * of a symbol rather than a value. This would differentiate between
 * <code>{0}</code> and nothing at all, but I think it is a less useful
 * representation.
 * <p>
 * {@code ManaCost}s impose a special iteration order on the {@link ManaSymbol}s
 * thanks to the guaranteed iteration order of {@link ImmutableMultiset}.
 * {@link ManaSymbol}s are always in the exact order that they appear on the
 * actual Magic card. (WOTC has detailed their system for the order of symbols
 * on multicolored cards.) With the release of Khans of Tarkir, however, this is
 * no longer true. "Wedge" multicolored cards with a clan watermark use a new
 * order for the symbols, leading with that clan's dominant color.
 * 
 * @see ManaSymbol
 */
public abstract class ManaCost {

	/**
	 * The empty mana cost. This mana cost is seen (or rather, is not seen at
	 * all) on Lands and a few cards such as Ancestral Vision.
	 * <p>
	 * This special mana cost uses the default identity-based {@code equals} and
	 * {@code hashCode} implementations. It is impossible to create a second
	 * instance of {@code EMPTY}.
	 * <p>
	 * The {@link #toString()} value of the {@code ManaCost} is the empty
	 * {@code String}, {@code ""}.
	 */
	public static final ManaCost EMPTY = new SpecialManaCost("");

	/**
	 * The mana cost represented by the zero colorless mana symbol
	 * <code>{0}</code>. The zero mana symbol only appears when no other symbols
	 * are present.
	 * <p>
	 * This special mana cost uses the default identity-based {@code equals} and
	 * {@code hashCode} implementations. It is impossible to create a second
	 * instance of {@code ZERO}.
	 */
	public static final ManaCost ZERO = new SpecialManaCost("{0}");

	/**
	 * Returns a new {@code ManaCost} with the given {@code Symbol}s and no
	 * colorless mana. If the array of symbols is empty, the empty mana cost is
	 * returned.
	 */
	public static ManaCost of(ManaSymbol... symbols) {
		return of(Arrays.asList(symbols));
	}

	/**
	 * Returns a new {@code ManaCost} with the given {@code Symbol}s and no
	 * colorless mana. If the {@link Collection} of symbols is empty, the empty
	 * mana cost is returned.
	 */
	public static ManaCost of(Collection<ManaSymbol> symbols) {
		if (symbols.size() == 0) {
			return EMPTY;
		}
		return of(0, symbols);
	}

	/**
	 * Returns a new {@code ManaCost} with the given amount of colorless mana
	 * and other mana symbols. If the array of symbols is empty and the amount
	 * of colorless is zero, the zero mana cost is returned.
	 */
	public static ManaCost of(int colorless, ManaSymbol... symbols) {
		return of(colorless, Arrays.asList(symbols));
	}

	/**
	 * Returns a new {@code ManaCost} with the given amount of colorless mana
	 * and other mana symbols. If the {@link Collection} of symbols is empty and
	 * the amount of colorless is zero, the zero mana cost is returned.
	 */
	public static ManaCost of(int colorless, Collection<ManaSymbol> symbols) {
		if (colorless == 0 && symbols.isEmpty()) {
			return ZERO;
		}
		return new StandardManaCost(colorless, symbols);
	}

	/**
	 * Returns a new {@code ManaCost} as specified by the input {@link String}.
	 * The input can contain any number of mana symbols in the format specified
	 * by {@link ManaSymbol#toString()}, with no separators. Symbols do not have
	 * to be in the order in which they would appear on a card.
	 * 
	 * @throws IllegalArgumentException
	 *             if input contains <code>{0}</code> in combination with other
	 *             symbols, or if more than one colorless symbol is given, or if
	 *             the symbols are not formatted properly
	 */
	public static ManaCost parse(String input) {
		switch (input) {
			case "":
				return EMPTY;
			case "{0}":
				return ZERO;
			default:
		}
		ImmutableSortedMultiset.Builder<ManaSymbol> builder = ImmutableSortedMultiset.naturalOrder();
		boolean colorlessSymbolEncountered = false;
		int begin = 0;
		do {
			if (input.charAt(begin) != '{') {
				throw parseException(
						"expected '{' at position %d in \"%s\"", begin, input);
			}
			int end = input.indexOf('}', begin + 1);
			if (end == -1) {
				throw parseException(
						"no closing '}' in \"%s\"", input);
			}
			String part = input.substring(begin, end + 1);
			ManaSymbol symbol = ManaSymbol.parse(part);
			if (symbol != null) {
				builder.add(symbol);
			} else {
				int parsed;
				try {
					parsed = Integer.parseInt(input.substring(begin + 1, end));
				} catch (NumberFormatException e) {
					throw parseException(
							"invalid symbol \"%s\" in \"%s\"", part, input);
				}
				if (colorlessSymbolEncountered) {
					throw parseException(
							"multiple colorless symbols in \"%s\"", input);
				}
				if (parsed == 0) {
					throw parseException(
							"{0} used with other symbols in \"%s\"", input);
				}
				colorlessSymbolEncountered = true;
				builder.addCopies(ManaSymbol.GENERIC, parsed);
			}
			begin = end + 1;
		} while (begin < input.length());
		return new StandardManaCost(builder.build());
	}
	
	private static IllegalArgumentException parseException(String format, Object... args) {
		throw new IllegalArgumentException(String.format(format, args));
	}

	private ManaCost() {}

	/**
	 * The combined colors of all {@link ManaSymbol}s in this {@code ManaCost}.
	 */
	public abstract ImmutableSet<Color> colors();

	/**
	 * The value of the colorless symbol in this mana cost, or 0 if no colorless
	 * symbol is present.
	 */
	public abstract int generic();

	/**
	 * A {@link Multiset} containing all symbols other than constant colorless
	 * mana symbols in the order they would appear on a Magic card.
	 */
	public abstract ImmutableMultiset<ManaSymbol> symbols();

	/**
	 * The converted mana cost of this {@code ManaCost}.
	 */
	public abstract int converted();

	public boolean containsAnyOf(ManaSymbol.Group group) {
		for (ManaSymbol symbol : symbols().elementSet()) {
			if (symbol.group() == group) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether this mana cost is payable with a certain set of colors of
	 * mana. Equivalent to calling {@link ManaSymbol#payableWith(Set)} on each
	 * unique
	 */
	public boolean payableWith(Set<Color> mana) {
		return ManaSymbol.payableWith(symbols().elementSet(), mana);
	}

	/**
	 * Returns the number of symbols in this mana cost that are of the given
	 * color.
	 */
	public int countColor(Color color) {
		int count = 0;
		for (Multiset.Entry<ManaSymbol> entry : symbols().entrySet()) {
			if (entry.getElement().colors().contains(color)) {
				count += entry.getCount();
			}
		}
		return count;
	}

	/**
	 * Returns whether this mana cost is the empty mana cost.
	 */
	public boolean isEmpty() {
		return this == EMPTY;
	}

	/**
	 * Returns whether this mana cost is the zero mana cost.
	 * 
	 */
	public boolean isZero() {
		return this == ZERO;
	}

	/**
	 * Returns the {@link String} representation of this mana cost: a series of
	 * mana symbols, including constant colorless mana symbols (specified by
	 * {@link ManaSymbol#toString()}) in the order that they would appear on an
	 * actual card.
	 */
	@Override public abstract String toString();

	private static class StandardManaCost extends ManaCost {

		private final ImmutableMultiset<ManaSymbol> symbols;

		// Cached values
		private final int converted;
		private final ImmutableSet<Color> colors;

		private StandardManaCost(int colorless,
				Collection<ManaSymbol> symbols) {
			this(condense(colorless, symbols));
		}

		private static ImmutableMultiset<ManaSymbol> condense(int colorless, Collection<ManaSymbol> symbols) {
			return ImmutableSortedMultiset.<ManaSymbol> naturalOrder()
					.addCopies(ManaSymbol.GENERIC, colorless)
					.addAll(symbols)
					.build();
		}

		private StandardManaCost(ImmutableMultiset<ManaSymbol> symbols) {
			this.symbols = symbols;
			int converted = 0;
			EnumSet<Color> colors = EnumSet.noneOf(Color.class);
			for (Multiset.Entry<ManaSymbol> entry : this.symbols.entrySet()) {
				converted += entry.getElement().converted() * entry.getCount();
				colors.addAll(entry.getElement().colors());
			}
			this.converted = converted;
			this.colors = Color.INTERNER.intern(colors);
		}

		@Override public ImmutableSet<Color> colors() {
			return colors;
		}

		@Override public int generic() {
			return symbols.count(ManaSymbol.GENERIC);
		}

		@Override public ImmutableMultiset<ManaSymbol> symbols() {
			return symbols;
		}

		@Override public int converted() {
			return converted;
		}

		@Override public int hashCode() {
			return symbols.hashCode();
		}

		@Override public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof StandardManaCost)) {
				return false;
			}
			StandardManaCost other = (StandardManaCost) obj;
			return symbols.equals(other.symbols);
		}

		@Override public String toString() {
			StringBuilder builder = new StringBuilder();
			for (Multiset.Entry<ManaSymbol> entry : symbols.entrySet()) {
				entry.getElement().formatTo(builder, entry.getCount());
			}
			return builder.toString();
		}
	}

	private static class SpecialManaCost extends ManaCost {

		private final String representation;

		private SpecialManaCost(String representation) {
			this.representation = representation;
		}

		@Override public ImmutableSet<Color> colors() {
			return ImmutableSet.of();
		}

		@Override public int generic() {
			return 0;
		}

		@Override public ImmutableMultiset<ManaSymbol> symbols() {
			return ImmutableMultiset.of();
		}

		@Override public int converted() {
			return 0;
		}

		@Override public String toString() {
			return representation;
		}
	}

}
