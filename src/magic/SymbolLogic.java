package magic;

import java.util.Collections;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

/**
 * Internal class containing symbol logic for getting around the lack of type
 * hierarchies within an enum.
 */
abstract class SymbolLogic {

	private final ImmutableSet<Color> colors;
	private final int converted;
	private final String representation;

	private SymbolLogic(int converted, String representation) {
		this(ImmutableSet.<Color> of(), converted, representation);
	}

	private SymbolLogic(Color only, int converted, String representation) {
		this(ImmutableSet.of(only), converted, representation);
	}

	private SymbolLogic(Color first, Color second, int converted,
			String representation) {
		this(ImmutableSet.of(first, second), converted, representation);
	}

	private SymbolLogic(ImmutableSet<Color> colors,
			int converted,
			String representation) {
		this.colors = colors;
		this.converted = converted;
		this.representation = representation;
	}

	int converted() {
		return converted;
	}

	ImmutableSet<Color> colors() {
		return colors;
	}

	@Override public String toString() {
		return representation;
	}

	abstract boolean payableWith(Set<Color> mana);

	String format(int occurences) {
		return Strings.repeat(this.toString(), occurences);
	}

	void formatTo(StringBuilder builder, int occurences) {
		for (int i = 0; i < occurences; i++) {
			builder.append(this.toString());
		}
	}

	static class Generic extends SymbolLogic {

		Generic() {
			super(1, "{1}");
		}

		@Override boolean payableWith(Set<Color> mana) {
			return true;
		}

		@Override String format(int occurences) {
			StringBuilder builder = new StringBuilder();
			formatTo(builder, occurences);
			return builder.toString();
		}

		@Override void formatTo(StringBuilder builder, int occurences) {
			builder.append('{')
					.append(occurences)
					.append('}');
		}
	}

	static class Colorless extends SymbolLogic {

		Colorless() {
			super(1, "{C}");
		}

		@Override boolean payableWith(Set<Color> mana) {
			return true;
		}
	}

	static abstract class Monocolored extends SymbolLogic {

		private final Color color;

		Color color() {
			return color;
		}

		Monocolored(Color only, int converted, String representation) {
			super(only, converted, representation);
			this.color = only;
		}

	}

	static final class Primary extends Monocolored {

		Primary(Color color) {
			super(color, 1, String.format("{%c}", color.code()));
		}

		@Override boolean payableWith(Set<Color> mana) {
			return mana.contains(color());
		}
	}

	static final class Hybrid extends SymbolLogic {

		Hybrid(Color first, Color second) {
			super(first, second, 1,
					String.format("{%c/%c}", first.code(), second.code()));
		}

		@Override boolean payableWith(Set<Color> mana) {
			return !Collections.disjoint(mana, colors());
		}
	}

	/**
	 * Base class for Monocolored Hybrid and Phyrexian, which have a color but
	 * can also be paid without mana of that color.
	 */
	private static abstract class ColorOptional extends Monocolored {

		private ColorOptional(Color only, int converted, String represntation) {
			super(only, converted, represntation);
		}

		@Override boolean payableWith(Set<Color> mana) {
			return true;
		}
	}

	static final class MonocoloredHybrid extends ColorOptional {

		MonocoloredHybrid(Color color) {
			super(color, 2, String.format("{2/%c}", color.code()));
		}
	}

	static final class Phyrexian extends ColorOptional {

		Phyrexian(Color color) {
			super(color, 1, String.format("{%c/P}", color.code()));
		}
	}

	static final class Variable extends SymbolLogic {

		Variable(char symbol) {
			super(0, String.format("{%c}", symbol));
		}

		@Override boolean payableWith(Set<Color> mana) {
			return true;
		}
	}

}
