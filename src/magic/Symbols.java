package magic;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import magic.Symbol.Monocolored;
import magic.Symbol.Repeatable;
import magic.misc.ConstantGetter;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.UnmodifiableIterator;

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
public abstract class Symbols {

	public static final class Primary extends Monocolored {

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

		private static final Map<Color, Primary> COLOR_MAP =
				getColorMap(Primary.class);

		public static Primary forColor(Color color) {
			return COLOR_MAP.get(color);
		}
	}

	public static final class Hybrid extends Repeatable {

		/**
		 * The hybrid White-Blue mana symbol: <code>{W/U}</code>.
		 */
		public static final Hybrid WHITE_BLUE = new Hybrid(Color.WHITE, Color.BLUE);

		/**
		 * The hybrid Blue-Black mana symbol: <code>{U/B}</code>.
		 */
		public static final Hybrid BLUE_BLACK = new Hybrid(Color.BLUE, Color.BLACK);

		/**
		 * The hybrid Black-Red mana symbol: <code>{B/R}</code>.
		 */
		public static final Hybrid BLACK_RED = new Hybrid(Color.BLACK, Color.RED);

		/**
		 * The hybrid Red-Green mana symbol: <code>{R/G}</code>.
		 */
		public static final Hybrid RED_GREEN = new Hybrid(Color.RED, Color.GREEN);

		/**
		 * The hybrid Green-White mana symbol: <code>{G/W}</code>.
		 */
		public static final Hybrid GREEN_WHITE = new Hybrid(Color.GREEN, Color.WHITE);

		/**
		 * The hybrid White-Black mana symbol: <code>{W/B}</code>.
		 */
		public static final Hybrid WHITE_BLACK = new Hybrid(Color.WHITE, Color.BLACK);

		/**
		 * The hybrid Blue-Red mana symbol: <code>{U/R}</code>.
		 */
		public static final Hybrid BLUE_RED = new Hybrid(Color.BLUE, Color.RED);

		/**
		 * The hybrid Black-Green mana symbol: <code>{B/G}</code>.
		 */
		public static final Hybrid BLACK_GREEN = new Hybrid(Color.BLACK, Color.GREEN);

		/**
		 * The hybrid Red-White mana symbol: <code>{R/W}</code>.
		 */
		public static final Hybrid RED_WHITE = new Hybrid(Color.RED, Color.WHITE);

		/**
		 * The hybrid Green-Blue mana symbol: <code>{G/U}</code>.
		 */
		public static final Hybrid GREEN_BLUE = new Hybrid(Color.GREEN, Color.BLUE);

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
		
		private static final Map<Color, Map<Color, Hybrid>> COLOR_MAP;
		
		public static Hybrid forColors(Color color1, Color color2) {
			return COLOR_MAP.get(color1).get(color2);
		}
		
		public static Hybrid forColors(Set<Color> colors) {
			if (colors.size() != 2) {
				throw new IllegalArgumentException("colors.size() != 2");
			}
			Iterator<Color> iterator = colors.iterator();
			return forColors(iterator.next(), iterator.next());
		}
		
		static {
			Map<Color, Map<Color, Hybrid>> colorMap = new EnumMap<>(Color.class);
			for (Color color : Color.values()) {
				colorMap.put(color, new EnumMap<Color, Hybrid>(Color.class));
			}
			for (Hybrid symbol : ConstantGetter.values(Hybrid.class)) {
				Iterator<Color> iterator = symbol.colors().iterator();
				Color color1 = iterator.next();
				Color color2 = iterator.next();
				colorMap.get(color1).put(color2, symbol);
				colorMap.get(color2).put(color1, symbol);
			}
			COLOR_MAP = colorMap;
		}
	}

	public static final class MonocoloredHybrid extends Monocolored {

		/**
		 * The monocolored hybrid White mana symbol: <code>{2/W}</code>.
		 */
		public static final MonocoloredHybrid WHITE = new MonocoloredHybrid(Color.WHITE);

		/**
		 * The monocolored hybrid Blue mana symbol: <code>{2/U}</code>.
		 */
		public static final MonocoloredHybrid BLUE = new MonocoloredHybrid(Color.BLUE);

		/**
		 * The monocolored hybrid Black mana symbol: <code>{2/B}</code>.
		 */
		public static final MonocoloredHybrid BLACK = new MonocoloredHybrid(Color.BLACK);

		/**
		 * The monocolored hybrid Red mana symbol: <code>{2/R}</code>.
		 */
		public static final MonocoloredHybrid RED = new MonocoloredHybrid(Color.RED);

		/**
		 * The monocolored hybrid Green mana symbol: <code>{2/G}</code>.
		 */
		public static final MonocoloredHybrid GREEN = new MonocoloredHybrid(Color.GREEN);

		private MonocoloredHybrid(Color color) {
			super(color, 2, "2/" + color.code());
		}

		@Override public boolean payableWith(Set<Color> mana) {
			return true;
		}

		@Override public void accept(Visitor visitor) {
			visitor.visit(this);
		}

		private static final Map<Color, MonocoloredHybrid> COLOR_MAP =
				getColorMap(MonocoloredHybrid.class);

		public static Monocolored forColor(Color color) {
			return COLOR_MAP.get(color);
		}
	}

	public static final class Phyrexian extends Monocolored {

		/**
		 * The Phyrexian White mana symbol: <code>{W/P}</code>.
		 */
		public static final Phyrexian WHITE = new Phyrexian(Color.WHITE);

		/**
		 * The Phyrexian Blue mana symbol: <code>{U/P}</code>.
		 */
		public static final Phyrexian BLUE = new Phyrexian(Color.BLUE);

		/**
		 * The Phyrexian Black mana symbol: <code>{B/P}</code>.
		 */
		public static final Phyrexian BLACK = new Phyrexian(Color.BLACK);

		/**
		 * The Phyrexian Red mana symbol: <code>{R/P}</code>.
		 */
		public static final Phyrexian RED = new Phyrexian(Color.RED);

		/**
		 * The Phyrexian Green mana symbol: <code>{G/P}</code>.
		 */
		public static final Phyrexian GREEN = new Phyrexian(Color.GREEN);

		private Phyrexian(Color color) {
			super(color, 1, color.code() + "/P");
		}

		@Override public boolean payableWith(Set<Color> mana) {
			return true;
		}

		@Override public void accept(Visitor visitor) {
			visitor.visit(this);
		}

		private static final Map<Color, Phyrexian> COLOR_MAP =
				getColorMap(Phyrexian.class);

		public static Phyrexian forColor(Color color) {
			return COLOR_MAP.get(color);
		}
	}

	public static final class Variable extends Repeatable {

		/**
		 * The variable Colorless mana symbol: <code>{X}</code>.
		 */
		public static final Variable X = new Variable('X');

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

		public static final Generic GENERIC = new Generic();

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

	private static final ImmutableSet<Repeatable> VALUES;

	static {
		Builder<Repeatable> builder = ImmutableSet.builder();
		for (Class<?> clazz : Symbols.class.getDeclaredClasses()) {
			if (Repeatable.class.isAssignableFrom(clazz)) {
				@SuppressWarnings("unchecked")
				Class<? extends Repeatable> repeatableSubclass =
						(Class<? extends Repeatable>) clazz;
				builder.addAll(ConstantGetter.values(repeatableSubclass));
			}
		}
		VALUES = builder.build();
	}

	public static ImmutableSet<Repeatable> values() {
		return VALUES;
	}

	public interface Visitor {

		void visit(Generic symbol);

		void visit(Primary symbol);

		void visit(Hybrid symbol);

		void visit(MonocoloredHybrid symbol);

		void visit(Phyrexian symbol);

		void visit(Variable symbol);
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

	private static <M extends Monocolored> Map<Color, M> getColorMap(Class<M> clazz) {
		EnumMap<Color, M> colorMap = new EnumMap<>(Color.class);
		for (M m : ConstantGetter.values(clazz)) {
			colorMap.put(m.color(), m);
		}
		return colorMap;
	}

}
