package magic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.Set;

import magic.Symbol.Group;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableMultiset.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;

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
 * {@code ManaCost}s impose a special iteration order on the {@link Symbol}s
 * thanks to the guaranteed iteration order of {@link ImmutableMultiset}.
 * {@link Symbol}s are always in the exact order that they appear on the actual
 * Magic card. (WOTC has detailed their system for the order of symbols on
 * multicolored cards.) With the release of Khans of Tarkir, however, this is no
 * longer true. "Wedge" multicolored cards with a clan watermark use a new order
 * for the symbols, leading with that clan's dominant color.
 * 
 * @see Symbol
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
	public static ManaCost of(Symbol... symbols) {
		return of(Arrays.asList(symbols));
	}

	/**
	 * Returns a new {@code ManaCost} with the given {@code Symbol}s and no
	 * colorless mana. If the {@link Collection} of symbols is empty, the empty
	 * mana cost is returned.
	 */
	public static ManaCost of(Collection<Symbol> symbols) {
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
	public static ManaCost of(int colorless, Symbol... symbols) {
		return of(colorless, Arrays.asList(symbols));
	}

	/**
	 * Returns a new {@code ManaCost} with the given amount of colorless mana
	 * and other mana symbols. If the {@link Collection} of symbols is empty and
	 * the amount of colorless is zero, the zero mana cost is returned.
	 */
	public static ManaCost of(int colorless, Collection<Symbol> symbols) {
		if (colorless == 0 && symbols.isEmpty()) {
			return ZERO;
		}
		return new StandardManaCost(
				colorless,
				orderSymbols(TreeMultiset.create(symbols)));
	}

	/**
	 * Returns a new {@code ManaCost} as specified by the input {@link String}.
	 * The input can contain any number of mana symbols in the format specified
	 * by {@link Symbol#toString()}, with no separators. Symbols do not have to
	 * be in the order in which they would appear on a card.
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
		Multiset<Symbol> symbols = TreeMultiset.create();
		int colorless = 0;
		int begin = 0;
		do {
			if (input.charAt(begin) != '{') {
				throw new IllegalArgumentException(String.format(
						"expected '{' at position %d in \"%s\"", begin, input));
			}
			int end = input.indexOf('}', begin + 1);
			if (end == -1) {
				throw new IllegalArgumentException(String.format(
						"no closing '}' in \"%s\"", input));
			}
			String part = input.substring(begin, end + 1);
			Symbol symbol = Symbol.parse(part);
			if (symbol != null) {
				symbols.add(symbol);
			} else {
				int parsed;
				try {
					parsed = Integer.parseInt(input.substring(begin + 1, end));
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException(String.format(
							"invalid symbol \"%s\" in \"%s\"", part, input));
				}
				if (colorless != 0) {
					throw new IllegalArgumentException(String.format(
							"multiple colorless symbols in \"%s\"", input));
				}
				if (parsed == 0) {
					throw new IllegalArgumentException(String.format(
							"{0} used with other symbols in \"%s\"", input));
				}
				colorless = parsed;
			}
			begin = end + 1;
		} while (begin < input.length());
		return new StandardManaCost(colorless, orderSymbols(symbols));
	}

	private ManaCost() {}

	/**
	 * The combined colors of all {@link Symbol}s in this {@code ManaCost}.
	 */
	public abstract ImmutableSet<Color> colors();

	/**
	 * The value of the colorless symbol in this mana cost, or 0 if no colorless
	 * symbol is present.
	 */
	public abstract int colorless();

	/**
	 * A {@link Multiset} containing all symbols other than constant colorless
	 * mana symbols in the order they would appear on a Magic card.
	 */
	public abstract ImmutableMultiset<Symbol> symbols();

	/**
	 * The converted mana cost of this {@code ManaCost}.
	 */
	public abstract int converted();

	public boolean containsAnyOf(Symbol.Group group) {
		for (Symbol symbol : symbols().elementSet()) {
			if (symbol.group() == group) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns whether this mana cost is payable with a certain set of colors of
	 * mana. Equivalent to calling {@link Symbol#payableWith(Set)} on each
	 * unique
	 */
	public boolean payableWith(Set<Color> mana) {
		return Symbol.payableWith(symbols().elementSet(), mana);
	}

	/**
	 * Returns the number of symbols in this mana cost that are of the given
	 * color.
	 */
	public int countColor(Color color) {
		int count = 0;
		for (Multiset.Entry<Symbol> entry : symbols().entrySet()) {
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
	 * {@link Symbol#toString()}) in the order that they would appear on an
	 * actual card.
	 */
	@Override public abstract String toString();

	private static class StandardManaCost extends ManaCost {

		private final int colorless;
		private final ImmutableMultiset<Symbol> symbols;

		// Cached values
		private final int converted;
		private final ImmutableSet<Color> colors;

		private StandardManaCost(int colorless,
				ImmutableMultiset<Symbol> symbols) {
			if (colorless < 0) {
				throw new IllegalArgumentException(
						"colorless cannot be negative: " + colorless);
			}
			this.colorless = colorless;
			this.symbols = symbols;
			int converted = colorless;
			EnumSet<Color> colors = EnumSet.noneOf(Color.class);
			for (Multiset.Entry<Symbol> entry : this.symbols.entrySet()) {
				converted += entry.getElement().converted() * entry.getCount();
				colors.addAll(entry.getElement().colors());
			}
			this.converted = converted;
			this.colors = Color.INTERNER.intern(colors);
		}

		@Override public ImmutableSet<Color> colors() {
			return colors;
		}

		@Override public int colorless() {
			return colorless;
		}

		@Override public ImmutableMultiset<Symbol> symbols() {
			return symbols;
		}

		@Override public int converted() {
			return converted;
		}

		@Override public int hashCode() {
			return Objects.hash(colorless, symbols);
		}

		@Override public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof StandardManaCost)) {
				return false;
			}
			StandardManaCost other = (StandardManaCost) obj;
			return colorless == other.colorless
					&& symbols.equals(other.symbols);
		}

		@Override public String toString() {
			StringBuilder builder = new StringBuilder();
			if (colorless != 0) {
				builder.append('{').append(colorless).append('}');
			}
			StringBuilder variables = new StringBuilder();
			for (Symbol symbol : symbols) {
				if (symbol.group() == Symbol.Group.VARIABLE) {
					variables.append(symbol);
				} else {
					builder.append(symbol);
				}
			}
			return variables.toString() + builder.toString();
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

		@Override public int colorless() {
			return 0;
		}

		@Override public ImmutableMultiset<Symbol> symbols() {
			return ImmutableMultiset.of();
		}

		@Override public int converted() {
			return 0;
		}

		@Override public String toString() {
			return representation;
		}
	}

	public static Map<Multiset<Symbol>, ImmutableMultiset<Symbol>> precalculated =
			new MapMaker().makeMap();

	/*
	 * All the methods from here on out are weird and black-boxy, and I'm not
	 * entirely happy with them. They work well, but they can probably be
	 * improved.
	 */

	private static ImmutableMultiset<Symbol> orderSymbols(
			Multiset<Symbol> symbols) {
		ImmutableMultiset<Symbol> result = precalculated.get(symbols);
		if (result == null) {
			int distinct = symbols.elementSet().size();
			if (distinct < 2) {
				return ImmutableMultiset.copyOf(symbols);
			}
			Builder<Symbol> builder = ImmutableMultiset.builder();
			ListMultimap<Group, Symbol> groups = Multimaps.newListMultimap(
					new EnumMap<Symbol.Group, Collection<Symbol>>(Symbol.Group.class),
					new Supplier<List<Symbol>>() {
						@Override public List<Symbol> get() {
							return new ArrayList<>();
						}
					});
			for (Symbol symbol : symbols.elementSet()) {
				groups.put(symbol.group(), symbol);
			}
			for (Entry<Group, List<Symbol>> entry : Multimaps.asMap(groups).entrySet()) {
				List<Symbol> inGroup = entry.getValue();
				order(inGroup);
				for (Symbol symbol : inGroup) {
					builder.addCopies(symbol, symbols.count(symbol));
				}
			}
			result = builder.build();
			precalculated.put(result, result);
		}
		return result;
	}

	/*
	 * argument must be a sorted list
	 */
	private static void order(List<Symbol> symbols) {
		int size = symbols.size();
		switch (size) {
			case 1:
			case 5:
				break;
			case 2:

				/*
				 * If the two symbols have more than one symbol between them,
				 * swap the order.
				 */
				if (distance(symbols.get(0), symbols.get(1)) > 2) {
					Collections.swap(symbols, 0, 1);
				}
				break;
			case 3:

				/*
				 * If the two symbols on the right are next to each other, and
				 * the symbol on the left is separated by at most one space, no
				 * rotation is needed. This includes: WUB.., W.BR., .UBR.,
				 * .U.RG, and ..BRG, where '.' represents an absent symbol.
				 * 
				 * For the remaining five possibilities, check if it contains
				 * the "blue" symbol. If it does, rotate forward by 1;
				 * otherwise, backward by 1. WU..G, WU.R., and .UB.G are rotated
				 * forward and become GWU, RWU, and GUB, respectively, while
				 * W.B.G and W..RG are rotated backward and become BGW and RGW
				 * respectively. It just happens to work out that way.
				 */
				if (!(distance(
						symbols.get(1),
						symbols.get(2)) == 1
				&& distance(
						symbols.get(0),
						symbols.get(1)) <= 2)) {
					Collections.rotate(symbols, containsBlue(symbols) ? 1 : -1);
				}
				break;
			case 4:

				/*
				 * Find the one symbol missing, and rotate backward by that
				 * symbol's distance from the first.
				 */
				EnumSet<Color> range = EnumSet.allOf(Color.class);
				for (Symbol symbol : symbols) {
					range.remove(getColor(symbol));
				}
				Collections.rotate(symbols,
						-Iterables.getOnlyElement(range).ordinal());
				break;
			default:
				throw new AssertionError();
		}
	}

	private static boolean containsBlue(Collection<Symbol> symbols) {
		for (Symbol symbol : symbols) {
			if (getColor(symbol) == Color.BLUE) {
				return true;
			}
		}
		return false;
	}

	private static Color getColor(Symbol symbol) {
		return symbol.colors().asList().get(0);
	}

	private static int distance(Symbol start, Symbol end) {
		return Math.abs(getColor(start).ordinal() - getColor(end).ordinal());
	}

}
