package magic;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.Multiset;

/**
 * An immutable object representing a mana cost.
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
	 * The string representation of this mana cost is the empty {@code String},
	 * {@code ""}.
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
	 * generic mana. If the {@link Collection} of symbols is empty, the empty
	 * mana cost is returned.
	 */
	public static ManaCost of(Collection<ManaSymbol> symbols) {
		if (symbols.size() == 0) {
			return EMPTY;
		}
		return of(0, symbols);
	}

	/**
	 * Returns a new {@code ManaCost} with the given amount of generic mana and
	 * other mana symbols. If the array of symbols is empty and the amount of
	 * generic is zero, the zero mana cost is returned.
	 */
	public static ManaCost of(int generic, ManaSymbol... symbols) {
		return of(generic, Arrays.asList(symbols));
	}

	/**
	 * Returns a new {@code ManaCost} with the given amount of generic mana and
	 * other mana symbols. If the {@link Collection} of symbols is empty and the
	 * amount of generic is zero, the zero mana cost is returned.
	 */
	public static ManaCost of(int generic, Collection<ManaSymbol> symbols) {
		if (generic == 0 && symbols.isEmpty()) {
			return ZERO;
		}
		return new StandardManaCost(
				ImmutableSortedMultiset.<ManaSymbol> naturalOrder()
						.addCopies(ManaSymbol.GENERIC, generic)
						.addAll(symbols)
						.build());
	}

	private static final CharMatcher NUMERIC = CharMatcher.inRange('0', '9');

	/**
	 * Returns a new {@code ManaCost} as specified by the input {@link String}.
	 * The input can contain any number of mana symbols in the format specified
	 * by {@link ManaSymbol#toString()}, with no separators. Symbols do not have
	 * to be in the order in which they would appear on a card.
	 * 
	 * @throws IllegalArgumentException
	 *             if input contains <code>{0}</code> in combination with other
	 *             symbols, or if more than one generic symbol is given, or if
	 *             the symbols are not formatted properly
	 */
	public static ManaCost parse(String input) throws ManaCostFormatException {
		switch (input) {
			case "":
				return EMPTY;
			case "{0}":
				return ZERO;
			default:
		}
		ImmutableSortedMultiset.Builder<ManaSymbol> builder =
				ImmutableSortedMultiset.naturalOrder();
		boolean genericSymbolFound = false;
		int begin = 0;
		do {
			if (input.charAt(begin) != '{') {
				throw new ManaCostFormatException(
						"expected '{' at position %d in \"%s\"", begin, input);
			}
			int end = input.indexOf('}', begin + 1);
			if (end == -1) {
				throw new ManaCostFormatException(
						"no closing '}' in \"%s\"", input);
			}
			String inner = input.substring(begin + 1, end);
			if (NUMERIC.matchesAllOf(inner)) {
				if (genericSymbolFound) {
					throw new ManaCostFormatException(
							"multiple generic symbols in \"%s\"", input);
				}
				int amount = Integer.parseInt(inner);
				if (amount == 0) {
					throw new ManaCostFormatException(
							"{0} used with other symbols in \"%s\"", input);
				}
				builder.addCopies(ManaSymbol.GENERIC, amount);
				genericSymbolFound = true;
			} else {
				builder.add(getSymbol(inner));
			}
			begin = end + 1;
		} while (begin < input.length());
		return new StandardManaCost(builder.build());
	}

	private static ManaSymbol getSymbol(String inner) {
		switch (inner.length()) {
			case 1:
				switch (inner.charAt(0)) {
					case 'X':
						return ManaSymbol.X;
					case 'C':
						return ManaSymbol.COLORLESS;
					case 'S':
						return ManaSymbol.SNOW;
					default:
						return ManaSymbol.primary(Color.forCode(inner.charAt(0)));
				}
			case 3:
				if (inner.charAt(1) != '/') {
					break;
				}
				char first = inner.charAt(0);
				char second = inner.charAt(2);
				if (first == '2') {
					return ManaSymbol.monocoloredHybrid(Color.forCode(second));
				}
				if (second == 'P') {
					return ManaSymbol.phyrexian(Color.forCode(first));
				}
				return ManaSymbol.hybrid(Color.forCode(first), Color.forCode(second));
			default:
		}
		throw new ManaCostFormatException("Bad mana symbol: {%s}", inner);
	}

	@SuppressWarnings("serial")
	public static class ManaCostFormatException extends IllegalArgumentException {

		public ManaCostFormatException(String format, Object... args) {
			super(String.format(format, args));
		}
	}

	private ManaCost() {}

	/**
	 * The combined colors of all {@link ManaSymbol}s in this {@code ManaCost}.
	 */
	public abstract ImmutableSet<Color> colors();

	/**
	 * The value of the generic symbol in this mana cost, or 0 if no generic
	 * symbol is present.
	 */
	public abstract int generic();

	/**
	 * A {@link Multiset} containing all mana symbols in this mana cost.
	 */
	public abstract ImmutableMultiset<ManaSymbol> symbols();

	/**
	 * The converted mana cost of this {@code ManaCost}.
	 */
	public abstract int converted();

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
	 * mana symbols, including numeric mana symbols, in the order that they
	 * would appear on an actual card.
	 */
	@Override public abstract String toString();

	private static class StandardManaCost extends ManaCost {

		private final ImmutableMultiset<ManaSymbol> symbols;

		// Cached values
		private final int converted;
		private final ImmutableSet<Color> colors;

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

		@Override public ImmutableMultiset<ManaSymbol> symbols() {
			return symbols;
		}

		@Override public int converted() {
			return converted;
		}

		@Override public int generic() {
			return symbols().count(ManaSymbol.GENERIC);
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
			return symbols.equals(((StandardManaCost) obj).symbols);
		}

		@Override public String toString() {
			final StringBuilder builder = new StringBuilder();
			for (Multiset.Entry<ManaSymbol> entry : symbols.entrySet()) {
				entry.getElement()
				
			}
			return builder.toString();
		}

		private static void appendCopies(StringBuilder builder, char symbol, int amount) {
			for (int i = 0; i < amount; i++) {
				builder.append('{')
						.append(symbol)
						.append('}');
			}
		}

		private static void appendCopies(StringBuilder builder, char first, char second,
				int amount) {
			for (int i = 0; i < amount; i++) {
				builder.append('{')
						.append(first)
						.append('/')
						.append(second)
						.append('}');
			}
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
