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

/**
 * All mana symbols other than numeric symbols; in other words, all symbols that
 * may appear more than once in a mana cost. The reason constant colorless
 * symbols (such as <code>{1}</code>. or <code>{7}</code>.) are excluded is that
 * they are better represented as plain {@code int}s, to make them easier to
 * work with, and to reduce the complexity of this {@code enum}. See
 * {@link ManaCost} for further details on this conceptualization.
 * 
 * @see ManaCost
 */
public abstract class Symbol {

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
	public static final Hybrid HYBRID_WHITE_BLUE = new Hybrid(Color.WHITE, Color.BLUE);

	/**
	 * The hybrid Blue-Black mana symbol: <code>{U/B}</code>.
	 */
	public static final Hybrid HYBRID_BLUE_BLACK = new Hybrid(Color.BLUE, Color.BLACK);

	/**
	 * The hybrid Black-Red mana symbol: <code>{B/R}</code>.
	 */
	public static final Hybrid HYBRID_BLACK_RED = new Hybrid(Color.BLACK, Color.RED);

	/**
	 * The hybrid Red-Green mana symbol: <code>{R/G}</code>.
	 */
	public static final Hybrid HYBRID_RED_GREEN = new Hybrid(Color.RED, Color.GREEN);

	/**
	 * The hybrid Green-White mana symbol: <code>{G/W}</code>.
	 */
	public static final Hybrid HYBRID_GREEN_WHITE = new Hybrid(Color.GREEN, Color.WHITE);

	/**
	 * The hybrid White-Black mana symbol: <code>{W/B}</code>.
	 */
	public static final Hybrid HYBRID_WHITE_BLACK = new Hybrid(Color.WHITE, Color.BLACK);

	/**
	 * The hybrid Blue-Red mana symbol: <code>{U/R}</code>.
	 */
	public static final Hybrid HYBRID_BLUE_RED = new Hybrid(Color.BLUE, Color.RED);

	/**
	 * The hybrid Black-Green mana symbol: <code>{B/G}</code>.
	 */
	public static final Hybrid HYBRID_BLACK_GREEN = new Hybrid(Color.BLACK, Color.GREEN);

	/**
	 * The hybrid Red-White mana symbol: <code>{R/W}</code>.
	 */
	public static final Hybrid HYBRID_RED_WHITE = new Hybrid(Color.RED, Color.WHITE);

	/**
	 * The hybrid Green-Blue mana symbol: <code>{G/U}</code>.
	 */
	public static final Hybrid HYBRID_GREEN_BLUE = new Hybrid(Color.GREEN, Color.BLUE);

	/**
	 * The monocolored hybrid White mana symbol: <code>{2/W}</code>.
	 */
	public static final MonocoloredHybrid MONOCOLORED_HYBRID_WHITE = new MonocoloredHybrid(Color.WHITE);

	/**
	 * The monocolored hybrid Blue mana symbol: <code>{2/U}</code>.
	 */
	public static final MonocoloredHybrid MONOCOLORED_HYBRID_BLUE = new MonocoloredHybrid(Color.BLUE);

	/**
	 * The monocolored hybrid Black mana symbol: <code>{2/B}</code>.
	 */
	public static final MonocoloredHybrid MONOCOLORED_HYBRID_BLACK = new MonocoloredHybrid(Color.BLACK);

	/**
	 * The monocolored hybrid Red mana symbol: <code>{2/R}</code>.
	 */
	public static final MonocoloredHybrid MONOCOLORED_HYBRID_RED = new MonocoloredHybrid(Color.RED);

	/**
	 * The monocolored hybrid Green mana symbol: <code>{2/G}</code>.
	 */
	public static final MonocoloredHybrid MONOCOLORED_HYBRID_GREEN = new MonocoloredHybrid(Color.GREEN);

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

	private final ImmutableSet<Color> colors;
	private final int converted;
	private final String innerPart;

	Symbol(ImmutableSet<Color> colors, int converted, String innerPart) {
		this.colors = colors;
		this.converted = converted;
		this.innerPart = innerPart;
	}

	Symbol(Color color, int converted, String innerPart) {
		this(ImmutableSet.of(color), converted, innerPart);
	}

	Symbol(int converted, String innerPart) {
		this(ImmutableSet.<Color> of(), converted, innerPart);
	}
	
	public abstract boolean payableWith(Set<Color> mana);

	@Override public final String toString() {
		return '{' + innerPart + '}';
	}

	public final int converted() {
		return converted;
	}

	public final ImmutableSet<Color> colors() {
		return colors;
	}
	
	final String innerPart() {
		return innerPart;
	}

	public abstract void accept(Visitor visitor);

	protected abstract String format(int count);

	public static abstract class Repeatable extends Symbol {

		private Repeatable(ImmutableSet<Color> colors,
				int converted, String innerPart) {
			super(colors, converted, innerPart);
		}

		private Repeatable(Color color, int converted, String representation) {
			this(ImmutableSet.of(color), converted, representation);
		}

		@Override protected String format(int count) {
			return Strings.repeat(toString(), count);
		}
	}

	public static abstract class Monocolored extends Repeatable {

		private final Color color;

		private Monocolored(Color color, int converted, String innerPart) {
			super(color, converted, innerPart);
			this.color = color;
		}

		public Color color() {
			return color;
		}
	}

	public static final class Primary extends Monocolored {

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

	public static final class Hybrid extends Repeatable {

		private final Color first;
		private final Color second;

		private Hybrid(Color first, Color second) {
			super(ImmutableSet.of(first, second), 1,
					new String(new char[] { first.code(), '/', second.code() }));
			this.first = first;
			this.second = second;
		}

		@Override public boolean payableWith(Set<Color> mana) {
			return mana.contains(first) || mana.contains(second);
		}

		@Override public void accept(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static final class MonocoloredHybrid extends Monocolored {

		private MonocoloredHybrid(Color color) {
			super(color, 2, "2/" + color.code());
		}

		@Override public boolean payableWith(Set<Color> mana) {
			return true;
		}

		@Override public void accept(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static final class Phyrexian extends Monocolored {

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

	public static final class Variable extends Repeatable {

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

	public static ImmutableSet<Repeatable> values() {
		return VALUES;
	}

	public static <T extends Symbol> Set<T> valuesOfType(Class<T> type) {
		return ImmutableSet.copyOf(Iterables.filter(VALUES, type));
	}

	private static final ImmutableMap<String, Repeatable> PARSE_LOOKUP;
	private static final ImmutableSet<Repeatable> VALUES;

	static {
		List<Repeatable> values = ConstantGetter.values(Symbol.class, Repeatable.class);
		ImmutableMap.Builder<String, Repeatable> builder = ImmutableMap.builder();
		for (Repeatable symbol : values) {
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
	public static Repeatable parse(String input) {
		String inner = stripBrackets(input);
		Repeatable symbol = parseInner(inner);
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

	static Repeatable parseInner(String inner) {
		return PARSE_LOOKUP.get(inner);
	}

}
