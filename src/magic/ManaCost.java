package magic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import magic.Symbol.Hybrid;
import magic.Symbol.MonocoloredHybrid;
import magic.Symbol.Phyrexian;
import magic.Symbol.Primary;
import magic.Symbol.Variable;

import com.google.common.base.Optional;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.MapMaker;
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
public final class ManaCost {

	/**
	 * Returns a new {@code ManaCost} with the given {@code Symbol}s and no
	 * colorless mana. If the {@link Collection} of symbols is empty, the empty
	 * mana cost is returned.
	 */
	public static ManaCost of(Collection<Symbol> symbols) {
		Sorter sorter = new Sorter();
		for (Symbol symbol : symbols) {
			
		}
		
		return new ManaCost(Optional.of(Colorless.of(0)), ImmutableMultiset.copyOf(symbols));
	}

	/**
	 * Returns a new {@code ManaCost} with the given amount of colorless mana
	 * and other mana symbols. If the array of symbols is empty and the amount
	 * of colorless is zero, the zero mana cost is returned.
	 */
	public static ManaCost of(Symbol... symbols) {
		return of(Arrays.asList(symbols));
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
		List<Symbol> symbols = new ArrayList<>();
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
			symbols.add(Symbol.parseInner(input.substring(begin + 1, end)));
			begin = end + 1;
		} while (begin < input.length());
		return ManaCost.of(symbols);
	}

	private final Optional<Colorless> colorless;
	private final ImmutableMultiset<Symbol> symbols;

	// Cached values
	private final int converted;
	private final ImmutableSet<Color> colors;

	private ManaCost(Optional<Colorless> colorless, ImmutableMultiset<Symbol> symbols) {
		this.colorless = colorless;
		this.symbols = symbols;
		int converted = colorlessValue();
		EnumSet<Color> colors = EnumSet.noneOf(Color.class);
		for (Multiset.Entry<Symbol> entry : this.symbols.entrySet()) {
			converted += entry.getElement().converted() * entry.getCount();
			colors.addAll(entry.getElement().colors());
		}
		this.converted = converted;
		this.colors = Color.INTERNER.intern(colors);
	}

	public <T extends Symbol> boolean containsAnyOf(Class<T> type) {
		for (Symbol symbol : symbols.elementSet()) {
			if (type.isInstance(symbol)) {
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
		return Symbol.payableWith(nonNumeric().elementSet(), mana);
	}

	/**
	 * Returns the number of symbols in this mana cost that are of the given
	 * color.
	 */
	public int countColor(Color color) {
		int count = 0;
		for (Multiset.Entry<Symbol> entry : nonNumeric().entrySet()) {
			if (entry.getElement().colors().contains(color)) {
				count += entry.getCount();
			}
		}
		return count;
	}

	/**
	 * The combined colors of all {@link Symbol}s in this {@code ManaCost}.
	 */
	public ImmutableSet<Color> colors() {
		return colors;
	}

	public Optional<Colorless> colorless() {
		return colorless;
	}

	/**
	 * The value of the colorless symbol in this mana cost, or 0 if no colorless
	 * symbol is present.
	 */
	public int colorlessValue() {
		return colorless.isPresent() ? colorless.get().value() : 0;
	}

	/**
	 * A {@link Multiset} containing all symbols other than numeric colorless
	 * mana symbols in the order they would appear on a Magic card.
	 */
	public ImmutableMultiset<Symbol> nonNumeric() {
		return symbols;
	}

	/**
	 * The converted mana cost of this {@code ManaCost}.
	 */
	public int converted() {
		return converted;
	}

	public boolean isEmpty() {
		return symbols.isEmpty() && !colorless.isPresent();
	}

	public boolean isZero() {
		return symbols.isEmpty()
				&& colorless.isPresent() && colorless.get().value() == 0;
	}

	@Override public int hashCode() {
		return Objects.hash(colorless, symbols);
	}

	@Override public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ManaCost)) {
			return false;
		}
		ManaCost other = (ManaCost) obj;
		return colorless == other.colorless
				&& symbols.equals(other.symbols);
	}

	/**
	 * Returns the {@link String} representation of this mana cost: a series of
	 * mana symbols, including constant colorless mana symbols (specified by
	 * {@link Symbol#toString()}) in the order that they would appear on an
	 * actual card.
	 */
	@Override public String toString() {
		StringBuilder builder = new StringBuilder();
		if (colorless.isPresent()) {
			builder.append(colorless);
		}
		StringBuilder variables = new StringBuilder();
		for (Symbol symbol : symbols) {
			if (symbol instanceof Variable) {
				variables.append(symbol);
			} else {
				builder.append(symbol);
			}
		}
		return variables.toString() + builder.toString();
	}

	private static Map<Multiset<Symbol>, ImmutableMultiset<Symbol>> precalculated =
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
	
	private static class Sorter implements Symbol.Visitor {

		private Optional<Colorless> colorless = Optional.absent();
		private Multiset<Primary> primary = HashMultiset.create();
		
		@Override public void visit(Colorless symbol) {
			if (colorless.isPresent()) {
				throw new IllegalArgumentException();
			}
			colorless = Optional.of(symbol);
		}

		@Override public void visit(Primary symbol) {
			
		}

		@Override public void visit(Hybrid symbol) {
			// TODO Auto-generated method stub
			
		}

		@Override public void visit(MonocoloredHybrid symbol) {
			// TODO Auto-generated method stub
			
		}

		@Override public void visit(Phyrexian symbol) {
			// TODO Auto-generated method stub
			
		}

		@Override public void visit(Variable symbol) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public static void main(String[] args) {
		System.out.println(Symbol.parse("{U}").getClass());
	}

}
