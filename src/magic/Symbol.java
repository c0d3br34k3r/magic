package magic;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import magic.misc.ConstantGetter;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;

/**
 * All mana symbols other than numeric symbols; in other words, all symbols
 * that may appear more than once in a mana cost. The reason constant colorless
 * symbols (such as <code>{1}</code>. or <code>{7}</code>.) are excluded is that
 * they are better represented as plain {@code int}s, to make them easier to
 * work with, and to reduce the complexity of this {@code enum}. See
 * {@link ManaCost} for further details on this conceptualization.
 * 
 * @see ManaCost
 */
public abstract class Symbol extends ManaUnit {

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
	public static final Hybrid HYBRID_WHITE_BLUE = new Hybrid(WHITE, BLUE);

	/**
	 * The hybrid Blue-Black mana symbol: <code>{U/B}</code>.
	 */
	public static final Hybrid HYBRID_BLUE_BLACK = new Hybrid(BLUE, BLACK);

	/**
	 * The hybrid Black-Red mana symbol: <code>{B/R}</code>.
	 */
	public static final Hybrid HYBRID_BLACK_RED = new Hybrid(BLACK, RED);

	/**
	 * The hybrid Red-Green mana symbol: <code>{R/G}</code>.
	 */
	public static final Hybrid HYBRID_RED_GREEN = new Hybrid(RED, GREEN);

	/**
	 * The hybrid Green-White mana symbol: <code>{G/W}</code>.
	 */
	public static final Hybrid HYBRID_GREEN_WHITE = new Hybrid(GREEN, WHITE);

	/**
	 * The hybrid White-Black mana symbol: <code>{W/B}</code>.
	 */
	public static final Hybrid HYBRID_WHITE_BLACK = new Hybrid(WHITE, BLACK);

	/**
	 * The hybrid Blue-Red mana symbol: <code>{U/R}</code>.
	 */
	public static final Hybrid HYBRID_BLUE_RED = new Hybrid(BLUE, RED);

	/**
	 * The hybrid Black-Green mana symbol: <code>{B/G}</code>.
	 */
	public static final Hybrid HYBRID_BLACK_GREEN = new Hybrid(BLACK, GREEN);

	/**
	 * The hybrid Red-White mana symbol: <code>{R/W}</code>.
	 */
	public static final Hybrid HYBRID_RED_WHITE = new Hybrid(RED, WHITE);

	/**
	 * The hybrid Green-Blue mana symbol: <code>{G/U}</code>.
	 */
	public static final Hybrid HYBRID_GREEN_BLUE = new Hybrid(GREEN, BLUE);

	/**
	 * The monocolored hybrid White mana symbol: <code>{2/W}</code>.
	 */
	public static final MonocoloredHybrid MONOCOLORED_HYBRID_WHITE = new MonocoloredHybrid(WHITE);

	/**
	 * The monocolored hybrid Blue mana symbol: <code>{2/U}</code>.
	 */
	public static final MonocoloredHybrid MONOCOLORED_HYBRID_BLUE = new MonocoloredHybrid(BLUE);

	/**
	 * The monocolored hybrid Black mana symbol: <code>{2/B}</code>.
	 */
	public static final MonocoloredHybrid MONOCOLORED_HYBRID_BLACK = new MonocoloredHybrid(BLACK);

	/**
	 * The monocolored hybrid Red mana symbol: <code>{2/R}</code>.
	 */
	public static final MonocoloredHybrid MONOCOLORED_HYBRID_RED = new MonocoloredHybrid(RED);

	/**
	 * The monocolored hybrid Green mana symbol: <code>{2/G}</code>.
	 */
	public static final MonocoloredHybrid MONOCOLORED_HYBRID_GREEN = new MonocoloredHybrid(GREEN);

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
	
	public static final Generic GENERIC = new Generic();

	Symbol(ImmutableSet<Color> colors, int converted, String innerPart) {
		super(colors, converted, innerPart);
	}

	Symbol(Color color, int converted, String innerPart) {
		super(color, converted, innerPart);
	}
	
	Symbol(int converted, String innerPart) {
		super(converted, innerPart);
	}

	@Override public abstract boolean payableWith(Set<Color> mana);

	public abstract void accept(Visitor visitor);

	protected abstract String format(int count);

	public static abstract class RepeatableSymbol extends Symbol {

		private RepeatableSymbol(ImmutableSet<Color> colors,
				int converted, String innerPart) {
			super(colors, converted, innerPart);
		}

		private RepeatableSymbol(Color color, int converted, String representation) {
			this(ImmutableSet.of(color), converted, representation);
		}

		@Override protected String format(int count) {
			return Strings.repeat(toString(), count);
		}
	}

	public static final class Primary extends RepeatableSymbol {

		private final Color color;

		private Primary(Color color) {
			super(color, 1, Character.toString(color.code()));
			this.color = color;
		}

		@Override public boolean payableWith(Set<Color> mana) {
			return mana.contains(color);
		}

		@Override public void accept(Visitor visitor) {
			visitor.visit(this);
		}
	}

	private static abstract class TwoPart extends RepeatableSymbol {

		private final ManaUnit first;
		private final ManaUnit second;

		private TwoPart(ManaUnit first, ManaUnit second) {
			super(Sets.union(first.colors(), second.colors()).immutableCopy(),
					Math.max(first.converted(), second.converted()),
					first.innerPart() + '/' + second.innerPart());
			this.first = first;
			this.second = second;
		}

		@Override public boolean payableWith(Set<Color> mana) {
			return first.payableWith(mana) || second.payableWith(mana);
		}
	}

	public static final class Hybrid extends TwoPart {

		private Hybrid(Primary first, Primary second) {
			super(first, second);
		}

		@Override public void accept(Visitor visitor) {
			visitor.visit(this);
		}
	}
	
	public static final class MonocoloredHybrid extends TwoPart {

		private MonocoloredHybrid(Primary symbol) {
			super(NumericGroup.TWO, symbol);
		}

		@Override public boolean payableWith(Set<Color> mana) {
			return true;
		}

		@Override public void accept(Visitor visitor) {
			visitor.visit(this);
		}
	}

//	private static final ManaUnit PHYREXIAN = new ManaUnit(0, "P") {
//
//		@Override public boolean payableWith(Set<Color> mana) {
//			return true;
//		}
//	};
	
	public static final class Phyrexian extends RepeatableSymbol {

		private Phyrexian(Color color) {
			super(color, 1, color.code() + "/P");
		}

		@Override public boolean payableWith(Set<Color> mana) {
			return true;
		}

		@Override public void accept(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static final class Variable extends RepeatableSymbol {

		private Variable(char letter) {
			super(ImmutableSet.<Color> of(), 0, Character.toString(letter));
		}

		@Override public boolean payableWith(Set<Color> mana) {
			return true;
		}

		@Override public void accept(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static final class Generic extends Symbol {

		private Generic() {
			super(1, "generic");
		}

		@Override public boolean payableWith(Set<Color> mana) {
			return true;
		}

		@Override public void accept(Visitor visitor) {
			visitor.visit(this);
		}

		@Override protected String format(int count) {
			return '{' + Integer.toString(count) + '}';
		}
	}

	public static ImmutableSet<RepeatableSymbol> values() {
		return VALUES;
	}

	public static <T extends Symbol> Set<T> valuesOfType(Class<T> type) {
		return ImmutableSet.copyOf(Iterables.filter(VALUES, type));
	}

	private static final ImmutableMap<String, RepeatableSymbol> PARSE_LOOKUP;
	private static final ImmutableSet<RepeatableSymbol> VALUES;

	static {
		List<RepeatableSymbol> values = ConstantGetter.values(Symbol.class, RepeatableSymbol.class);
		ImmutableMap.Builder<String, RepeatableSymbol> builder = ImmutableMap.builder();
		for (RepeatableSymbol symbol : values) {
			builder.put(symbol.innerPart(), symbol);
		}
		PARSE_LOOKUP = builder.build();
		VALUES = ImmutableSet.copyOf(values);
	}

	public interface Visitor {

		void visit(Generic generic);

		void visit(Primary primary);

		void visit(Hybrid hybrid);

		void visit(MonocoloredHybrid monocoloredHybrid);

		void visit(Phyrexian phyrexian);

		void visit(Variable variable);
	}

	/**
	 * Returns {@code false} if any of the symbols are not payable with the
	 * given colors of mana.
	 */
	public static boolean payableWith(Collection<? extends Symbol> symbols,
			Set<Color> mana) {
		for (Symbol symbol : symbols) {
			if (!symbol.payableWith(mana)) {
				return false;
			}
		}
		return true;
	}

	public static String format(Multiset.Entry<Symbol> entry) {
		return entry.getElement().format(entry.getCount());
	}

	/**
	 * Returns the {@code Symbol} with the given representation.
	 * 
	 * @throws IllegalArgumentException
	 *             if the input does not correspond to a symbol.
	 */
	public static RepeatableSymbol parse(String input) {
		String inner = stripBrackets(input);
		RepeatableSymbol symbol = parseInner(inner);
		if (symbol == null) {
			throw new IllegalArgumentException(input);
		}
		return symbol;
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
	
	static RepeatableSymbol parseInner(String inner) {
		return PARSE_LOOKUP.get(inner);
	}

}
