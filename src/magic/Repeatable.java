package magic;

import java.util.List;
import java.util.Set;

import magic.misc.ConstantGetter;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public abstract class Repeatable extends Symbol {

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
	public static final Hybrid HYBRID_BLUE_RED = new Hybrid(BLUE,RED);

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
	
	

	Repeatable(ImmutableSet<Color> colors, int converted, String representation) {
		super(colors, converted, representation);
	}

	Repeatable(Color color, int converted, String representation) {
		super(color, converted, representation);
	}

	Repeatable(int converted, String representation) {
		super(converted, representation);
	}

	public static final class Primary extends Repeatable {

		private final Color color;

		private Primary(Color color) {
			super(color, 1, String.format("{%c}", color.code()));
			this.color = color;
		}

		@Override public boolean payableWith(Set<Color> mana) {
			return mana.contains(color);
		}

		@Override public void accept(Visitor visitor) {
			visitor.visit(this);
		}
	}

	private static abstract class TwoPart extends Repeatable {

		private final Symbol first;
		private final Symbol second;

		private TwoPart(Symbol first, Symbol second) {
			super(Sets.union(first.colors(), second.colors()).immutableCopy(),
					Math.max(first.converted(), second.converted()),
					String.format("{%s/%s}", stripBrackets(first), stripBrackets(second)));
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
			super(Numeric.of(2), symbol);
		}

		@Override public boolean payableWith(Set<Color> mana) {
			return true;
		}

		@Override public void accept(Visitor visitor) {
			visitor.visit(this);
		}
	}

	public static final class Phyrexian extends Repeatable {

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

	public static final class Variable extends Repeatable {

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
	
	
	
	public static ImmutableSet<Repeatable> values() {
		return VALUES;
	}
	
	public static <T extends Repeatable> Set<T> valuesOfType(Class<T> type) {
		return ImmutableSet.copyOf(Iterables.filter(VALUES, type));
	}

	static Repeatable parseInner(String inner) {
		return PARSE_LOOKUP.get(inner);
	}
	
	private static final ImmutableMap<String, Repeatable> PARSE_LOOKUP;
	private static final ImmutableSet<Repeatable> VALUES;

	static {
		List<Repeatable> values = ConstantGetter.values(Repeatable.class);
		ImmutableMap.Builder<String, Repeatable> builder = ImmutableMap.builder();
		for (Repeatable symbol : values) {
			builder.put(stripBrackets(symbol), symbol);
		}
		PARSE_LOOKUP = builder.build();
		VALUES = ImmutableSet.copyOf(values);
	}
	
}
