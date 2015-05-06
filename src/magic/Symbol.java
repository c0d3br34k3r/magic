package magic;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import magic.misc.ConstantGetter;
import magic.misc.ConstantGetter.EnumLike;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * All mana symbol:s other than numeric symbols; in other words, all symbols that
 * may appear more than once in a mana cost. The reason constant colorless
 * symbols (such as <code>{1}</code>. or <code>{7}</code>.) are excluded is that
 * they are better represented as plain {@code int}s, to make them easier to
 * work with, and to reduce the complexity of this {@code enum}. See
 * {@link ManaCost} for further details on this conceptualization.
 * 
 * @see ManaCost
 */
public abstract class Symbol implements EnumLike {

	/**
	 * The primary White mana symbol: <code>{W}</code>.
	 */
	public static final Primary WHITE = new Primary(Color.WHITE);

	/**
	 * The primary Blue mana symbol: <code>{U}</code>.
	 */
	public static final Primary BLUE = new Primary(Color.BLUE);

	/**
	 * The primary Black mana symbol: <code>{B}</code>.
	 */
	public static final Primary BLACK = new Primary(Color.BLACK);

	/**
	 * The primary Red mana symbol: <code>{R}</code>.
	 */
	public static final Primary RED = new Primary(Color.RED);

	/**
	 * The primary Green mana symbol: {G}</code>.
	 */
	public static final Primary GREEN = new Primary(Color.GREEN);

	/**
	 * The hybrid White-Blue mana symbol: <code>{W/U}</code>.
	 */
	public static final Hybrid HYBRID_WHITE_BLUE = new Hybrid(Symbol.WHITE, Symbol.BLUE);

	/**
	 * The hybrid Blue-Black mana symbol: <code>{U/B}</code>.
	 */
	public static final Hybrid HYBRID_BLUE_BLACK = new Hybrid(Symbol.BLUE, Symbol.BLACK);

	/**
	 * The hybrid Black-Red mana symbol: <code>{B/R}</code>.
	 */
	public static final Hybrid HYBRID_BLACK_RED = new Hybrid(Symbol.BLACK, Symbol.RED);

	/**
	 * The hybrid Red-Green mana symbol: <code>{R/G}</code>.
	 */
	public static final Hybrid HYBRID_RED_GREEN = new Hybrid(Symbol.RED, Symbol.GREEN);

	/**
	 * The hybrid Green-White mana symbol: <code>{G/W}</code>.
	 */
	public static final Hybrid HYBRID_GREEN_WHITE = new Hybrid(Symbol.GREEN, Symbol.WHITE);

	/**
	 * The hybrid White-Black mana symbol: <code>{W/B}</code>.
	 */
	public static final Hybrid HYBRID_WHITE_BLACK = new Hybrid(Symbol.WHITE, Symbol.BLACK);

	/**
	 * The hybrid Blue-Red mana symbol: <code>{U/R}</code>.
	 */
	public static final Hybrid HYBRID_BLUE_RED = new Hybrid(Symbol.BLUE, Symbol.RED);

	/**
	 * The hybrid Black-Green mana symbol: <code>{B/G}</code>.
	 */
	public static final Hybrid HYBRID_BLACK_GREEN = new Hybrid(Symbol.BLACK, Symbol.GREEN);

	/**
	 * The hybrid Red-White mana symbol: <code>{R/W}</code>.
	 */
	public static final Hybrid HYBRID_RED_WHITE = new Hybrid(Symbol.RED, Symbol.WHITE);

	/**
	 * The hybrid Green-Blue mana symbol: <code>{G/U}</code>.
	 */
	public static final Hybrid HYBRID_GREEN_BLUE = new Hybrid(Symbol.GREEN, Symbol.BLUE);

	/**
	 * The monocolored hybrid White mana symbol: <code>{2/W}</code>.
	 */
	public static final MonocoloredHybrid MONOCOLORED_HYBRID_WHITE = new MonocoloredHybrid(Symbol.WHITE);

	/**
	 * The monocolored hybrid Blue mana symbol: <code>{2/U}</code>.
	 */
	public static final MonocoloredHybrid MONOCOLORED_HYBRID_BLUE = new MonocoloredHybrid(Symbol.BLUE);

	/**
	 * The monocolored hybrid Black mana symbol: <code>{2/B}</code>.
	 */
	public static final MonocoloredHybrid MONOCOLORED_HYBRID_BLACK = new MonocoloredHybrid(Symbol.BLACK);

	/**
	 * The monocolored hybrid Red mana symbol: <code>{2/R}</code>.
	 */
	public static final MonocoloredHybrid MONOCOLORED_HYBRID_RED = new MonocoloredHybrid(Symbol.RED);

	/**
	 * The monocolored hybrid Green mana symbol: <code>{2/G}</code>.
	 */
	public static final MonocoloredHybrid MONOCOLORED_HYBRID_GREEN = new MonocoloredHybrid(Symbol.GREEN);

	/**
	 * The Phyrexian White mana symbol: <code>{W/P}</code>.
	 */
	public static final Phyrexian PHYREXIAN_WHITE = new Phyrexian(Color.WHITE);

	/**
	 * The Phyrexian Blue mana symbol: <code>{U/P}</code>.
	 */
	public static final Phyrexian PHYREXIAN_BLUE = new Phyrexian(Color.BLUE);

	/**
	 * The Phyrexian Black mana symbol: <code>{B/P}</code>.
	 */
	public static final Phyrexian PHYREXIAN_BLACK = new Phyrexian(Color.BLACK);

	/**
	 * The Phyrexian Red mana symbol: <code>{R/P}</code>.
	 */
	public static final Phyrexian PHYREXIAN_RED = new Phyrexian(Color.RED);

	/**
	 * The Phyrexian Green mana symbol: <code>{G/P}</code>.
	 */
	public static final Phyrexian PHYREXIAN_GREEN = new Phyrexian(Color.GREEN);

	/**
	 * The variable Colorless mana symbol: <code>{X}</code>.
	 */
	public static final Variable X = new Variable('X');

	/**
	 * Returns the {@code Symbol} with the given representation.
	 * 
	 * @throws IllegalArgumentException
	 *             if the input does not correspond to a symbol.
	 */
	public static Symbol parse(String input) {
		return parseInner(stripBrackets(input));
	}

	static Symbol parseInner(String inner) {
		Symbol symbol = PARSE_LOOKUP.get(inner);
		if (symbol == null) {
			symbol = Numeric.parseInner(inner);
			if (symbol == null) {
				throw new IllegalArgumentException(inner);
			}
		}
		return symbol;
	}

	static String stripBrackets(Symbol symbol) {
		return stripBrackets(symbol.toString());
	}

	static String stripBrackets(String input) {
		int length = input.length();
		if (length != 0
				&& input.charAt(0) == '{'
				&& input.charAt(length - 1) == '}') {
			return input.substring(1, length - 1);
		}
		throw new IllegalArgumentException(input);
	}

	/**
	 * Returns {@code false} if any of the symbols are not payable with the
	 * given colors of mana.
	 */
	public static boolean payableWith(Collection<Symbol> symbols,
			Set<Color> mana) {
		for (Symbol symbol : symbols) {
			if (!symbol.payableWith(mana)) {
				return false;
			}
		}
		return true;
	}

	public static <T extends Symbol> Set<T> valuesOfType(Class<T> type) {
		return ImmutableSet.copyOf(Iterables.filter(VALUES, type));
	}

	private static final ImmutableMap<String, Symbol> PARSE_LOOKUP;
	private static final ImmutableSet<Symbol> VALUES;

	static {
		List<Symbol> values = ConstantGetter.values(Symbol.class);
		ImmutableMap.Builder<String, Symbol> builder = ImmutableMap.builder();
		for (Symbol symbol : values) {
			builder.put(stripBrackets(symbol), symbol);
		}
		PARSE_LOOKUP = builder.build();
		VALUES = ImmutableSet.copyOf(values);
	}

	private final ImmutableSet<Color> colors;
	private final int converted;
	private final String representation;

	Symbol(ImmutableSet<Color> colors, int converted, String representation) {
		this.colors = colors;
		this.converted = converted;
		this.representation = representation;
	}

	Symbol(Color color, int converted, String representation) {
		this(ImmutableSet.of(color), converted, representation);
	}

	Symbol(int converted, String representation) {
		this(ImmutableSet.<Color> of(), converted, representation);
	}

	public int converted() {
		return converted;
	}

	public ImmutableSet<Color> colors() {
		return colors;
	}

	@Override public String toString() {
		return representation;
	}

	public abstract boolean payableWith(Set<Color> mana);

	public abstract void accept(Visitor visitor);

	public static abstract class Primitive extends Symbol {

		Primitive(Color color) {
			super(color, 1, String.format("{%c}", color.code()));
		}

		Primitive(int amount) {
			super(ImmutableSet.<Color> of(), amount, String.format("{%d}", amount));
		}
	}

	public static final class Primary extends Primitive {

		private final Color color;

		private Primary(Color color) {
			super(color);
			this.color = color;
		}

		@Override public boolean payableWith(Set<Color> mana) {
			return mana.contains(color);
		}

		@Override public void accept(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static abstract class TwoPartSymbol extends Symbol {

		private final Primitive first;
		private final Primitive second;

		private TwoPartSymbol(Primitive first, Primitive second) {
			super(Sets.union(first.colors(), second.colors()).immutableCopy(),
					Math.max(first.converted(), second.converted()),
					String.format("{%s/%s}", stripBrackets(first), stripBrackets(second)));
			this.first = first;
			this.second = second;
		}

		public Primitive first() {
			return first;
		}

		public Primitive second() {
			return second;
		}
	}

	public static final class Hybrid extends TwoPartSymbol {

		private Hybrid(Primary first, Primary second) {
			super(first, second);
		}

		@Override public boolean payableWith(Set<Color> mana) {
			return first().payableWith(mana) || second().payableWith(mana);
		}

		@Override public void accept(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static final class MonocoloredHybrid extends TwoPartSymbol {

		private MonocoloredHybrid(Primary symbol) {
			super(Numeric.of(2), symbol);
		}

		@Override public boolean payableWith(Set<Color> mana) {
			return true;
		}

		@Override public void accept(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static final class Phyrexian extends Symbol {

		private Phyrexian(Color color) {
			super(color, 1, String.format("{%c/P}", color.code()));
		}

		@Override public boolean payableWith(Set<Color> mana) {
			return true;
		}

		@Override public void accept(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static final class Variable extends Symbol {

		private Variable(char letter) {
			super(ImmutableSet.<Color> of(), 0, String.format("{%c}", letter));
		}

		@Override public boolean payableWith(Set<Color> mana) {
			return true;
		}

		@Override public void accept(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public interface Visitor {

		void visit(Numeric colorless);

		void visit(Primary primary);

		void visit(Hybrid hybrid);

		void visit(MonocoloredHybrid monocoloredHybrid);

		void visit(Phyrexian phyrexian);

		void visit(Variable variable);
	}

}
