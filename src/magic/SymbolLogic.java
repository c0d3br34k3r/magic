package magic;

import java.util.Collections;
import java.util.Set;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableSet;

/**
 * Internal class containing symbol logic for getting around the lack of type
 * hierarchies within an enum.
 */
abstract class SymbolLogic {

	private final ImmutableSet<Color> colors;
	private final int converted;
	private final String representation;
	private final Symbol.Group group;

	private SymbolLogic(Color only, int converted, String representation) {
		this(ImmutableSet.of(only), converted, representation);
	}

	private SymbolLogic(Color first, Color second, int converted, String representation) {
		this(ImmutableSet.of(first, second), converted, representation);
	}

	private SymbolLogic(ImmutableSet<Color> colors, int converted, String representation) {
		this.colors = colors;
		this.converted = converted;
		this.representation = representation;
		this.group = Symbol.Group.valueOf(
				CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.UPPER_UNDERSCORE)
						.convert(getClass().getSimpleName()));
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
	
	static abstract class Monocolored extends SymbolLogic {

		private final Color color;
		
		Color color() {
			return color;
		}
		
		protected Monocolored(Color only, int converted, String representation) {
			super(only, converted, representation);
			this.color = only;
		}
		
	}

	static final class Primary extends Monocolored {

		Primary(Color color) {
			super(color, 1, String.format("{%c}", color.code()));
		}

		@Override public boolean payableWith(Set<Color> mana) {
			return mana.contains(color());
		}
	}

	static final class Hybrid extends SymbolLogic {

		Hybrid(Color first, Color second) {
			super(first, second, 1, String.format("{%c/%c}", first.code(), second.code()));
		}

		@Override public boolean payableWith(Set<Color> mana) {
			return !Collections.disjoint(mana, colors());
		}
	}

	/**
	 * Base class for Monocolored Hybrid and Phyrexian, which have a color but
	 * can also be paid without mana of that color.
	 */
	private static abstract class ColorOptional extends Monocolored {

		private ColorOptional(Object first, Color second, int converted) {
			super(second, converted, String.format("{%s/%c}", first, second.code()));
		}

		private ColorOptional(Color first, Object second, int converted) {
			super(first, converted, String.format("{%c/%s}", first.code(), second));
		}

		@Override public boolean payableWith(Set<Color> mana) {
			return true;
		}
	}

	static final class MonocoloredHybrid extends ColorOptional {

		MonocoloredHybrid(Color color) {
			this(2, color);
		}

		private MonocoloredHybrid(int first, Color second) {
			super(first, second, first);
		}
	}

	static final class Phyrexian extends ColorOptional {

		Phyrexian(Color color) {
			super(color, 'P', 1);
		}
	}

	static final class Variable extends SymbolLogic {

		Variable(char symbol) {
			super(ImmutableSet.<Color> of(), 0, String.format("{%c}", symbol));
		}

		@Override public boolean payableWith(Set<Color> mana) {
			return true;
		}
	}

	Symbol.Group group() {
		return group;
	}

}
