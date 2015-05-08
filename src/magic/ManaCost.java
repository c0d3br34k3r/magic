package magic;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;

import magic.Symbol.Generic;
import magic.Symbol.Hybrid;
import magic.Symbol.MonocoloredHybrid;
import magic.Symbol.Phyrexian;
import magic.Symbol.Primary;
import magic.Symbol.RepeatableSymbol;
import magic.Symbol.Variable;
import magic.Symbol.Visitor;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
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
public class ManaCost {

	private final ImmutableMultiset<Symbol> symbols;
	private final int converted;
	private final ImmutableSet<Color> colors;

	private ManaCost(ImmutableMultiset<Symbol> symbols) {
		this.symbols = symbols;
		int converted = 0;
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
		return Symbol.payableWith(symbols.elementSet(), mana);
	}

	/**
	 * Returns the number of symbols in this mana cost that are of the given
	 * color.
	 */
	public int countColor(Color color) {
		int count = 0;
		for (Multiset.Entry<Symbol> entry : symbols.entrySet()) {
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

	/**
	 * The value of the numeric symbol in this mana cost, or 0 if no numeric
	 * symbol is present.
	 */
	public int generic() {
		return symbols.count(Symbol.GENERIC);
	}

	/**
	 * A {@link Multiset} containing all symbols other than numeric colorless
	 * mana symbols in the order they would appear on a Magic card.
	 */
	public ImmutableMultiset<Symbol> symbols() {
		return symbols;
	}

	/**
	 * The converted mana cost of this {@code ManaCost}.
	 */
	public int converted() {
		return converted;
	}

	public boolean isEmpty() {
		return this == EMPTY;
	}

	public boolean isZero() {
		return this == ZERO;
	}

	@Override public int hashCode() {
		return symbols.hashCode();
	}

	@Override public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		return obj instanceof ManaCost
				&& ((ManaCost) obj).symbols.equals(this.symbols);
	}

	/**
	 * Returns the {@link String} representation of this mana cost: a series of
	 * mana symbols, including numeric mana symbols (specified by
	 * {@link Symbol#toString()}) in the order that they would appear on an
	 * actual card.
	 */
	@Override public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Multiset.Entry<Symbol> entry : symbols.entrySet()) {
			builder.append(Symbol.format(entry));
		}
		return builder.toString();
	}

	public static final ManaCost ZERO = new SpecialManaCost("{0}");

	public static final ManaCost EMPTY = new SpecialManaCost("");

	public static ManaCost of(int numeric) {
		if (numeric == 0) {
			return ZERO;
		}
		return of(numeric, Collections.<RepeatableSymbol> emptySet());
	}

	public static ManaCost of(RepeatableSymbol... unordered) {
		if (unordered.length == 0) {
			return EMPTY;
		}
		return of(0, unordered);
	}

	public static ManaCost of(int numeric, RepeatableSymbol... unordered) {
		return of(numeric, Arrays.asList(unordered));
	}

	public static ManaCost of(
			int numeric,
			Collection<RepeatableSymbol> unordered) {
		HashMultiset<Symbol> symbols = HashMultiset.create();
		symbols.setCount(Symbol.GENERIC, numeric);
		symbols.addAll(unordered);
		return new ManaCost(sort(symbols));
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
		Multiset<Symbol> symbols = HashMultiset.create();
		int begin = 0;
		do {
			if (input.charAt(begin) != '{') {
				throw new IllegalArgumentException(input);
			}
			int end = input.indexOf('}', begin + 1);
			if (end == -1) {
				throw new IllegalArgumentException(input);
			}
			String inner = input.substring(begin + 1, end);
			Symbol symbol = Symbol.parseInner(inner);
			if (symbol == null) {
				if (symbols.contains(Symbol.GENERIC)) {
					throw new IllegalArgumentException(input);
				}
				symbols.setCount(Symbol.GENERIC, Integer.parseInt(inner));
			} else {
				symbols.add(symbol);
			}
			begin = end + 1;
		} while (begin < input.length());
		return new ManaCost(sort(symbols));
	}

	private static ImmutableMultiset<Symbol> sort(Multiset<Symbol> symbols) {
		return ImmutableMultiset.copyOf(symbols);
	}

	private static class Sorter implements Visitor {

		int generic = 0;
		int variable = 0;
		Multimap<Class<? extends Symbol>, Symbol> sorted = Multimaps.newMultimap(
				new HashMap<Class<? extends Symbol>, Collection<Symbol>>(),
				new Supplier<Collection<Symbol>>() {
					@Override public Collection<Symbol> get() {
						return HashMultiset.create(1);
					}
				});
		
		public void setGeneric(int value) {
			if (generic != 0) {
				throw new IllegalStateException();
			}
			generic = value;
		}

		@Override public void visit(Variable symbol) {
			variable++;
		}
		
		@Override public void visit(Generic symbol) {
			generic++;
		}

		@Override public void visit(Hybrid symbol) {
			sorted.put(Hybrid.class, symbol);
		}

		@Override public void visit(MonocoloredHybrid symbol) {
			sorted.put(MonocoloredHybrid.class, symbol);
		}

		@Override public void visit(Phyrexian symbol) {
			sorted.put(Phyrexian.class, symbol);
		}
		
		@Override public void visit(Primary symbol) {
			sorted.put(Primary.class, symbol);
		}

	}

	private static class SpecialManaCost extends ManaCost {

		private final String representation;

		private SpecialManaCost(String representation) {
			super(ImmutableMultiset.<Symbol> of());
			this.representation = representation;
		}

		@Override public String toString() {
			return representation;
		}
	}

	public static void main(String[] args) {
		System.out.println(ManaCost.of(5, Symbol.PHYREXIAN_RED).symbols());
	}

}
