package magic;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * Internal class containing symbol logic for getting around the lack of type
 * hierarchies within an enum.
 */
abstract class SymbolLogic {

	private final ImmutableSet<Color> colors;
	private final int converted;

	private SymbolLogic(int converted, Color... colors) {
		this.converted = converted;
		this.colors = ImmutableSet.copyOf(colors);
	}

	int converted() {
		return converted;
	}

	ImmutableSet<Color> colors() {
		return colors;
	}

	abstract boolean payableWith(Set<Color> mana);

	abstract ManaSymbol.Type type();

	void format(StringBuilder builder, int amount) {
		for (int i = 0; i < amount; i++) {
			format(builder);
		}
	}

	void format(StringBuilder builder) {}

	Color color() {
		throw new IllegalStateException();
	}

	Pair<Color> colorPair() {
		throw new IllegalStateException();
	}

	static final class Variable extends SymbolLogic {

		Variable() {
			super(0);
		}

		@Override boolean payableWith(Set<Color> mana) {
			return true;
		}

		@Override ManaSymbol.Type type() {
			return ManaSymbol.Type.VARIABLE;
		}

		@Override void format(StringBuilder builder) {
			builder.append("{X}");
		}
	}

	static class Generic extends SymbolLogic {

		Generic() {
			super(1);
		}

		@Override boolean payableWith(Set<Color> mana) {
			return true;
		}

		@Override ManaSymbol.Type type() {
			return ManaSymbol.Type.GENERIC;
		}

		@Override void format(StringBuilder builder, int i) {
			builder.append('{').append(i).append('}');
		}
	}

	static class Colorless extends SymbolLogic {

		Colorless() {
			super(1);
		}

		@Override boolean payableWith(Set<Color> mana) {
			return true;
		}

		@Override ManaSymbol.Type type() {
			return ManaSymbol.Type.COLORLESS;
		}

		@Override void format(StringBuilder builder) {
			builder.append("{C}");
		}
	}

	static abstract class Monocolored extends SymbolLogic {

		protected final Color color;

		Monocolored(int converted, Color only) {
			super(converted, only);
			this.color = only;
		}

		@Override Color color() {
			return color;
		}
	}

	static final class Primary extends Monocolored {

		Primary(Color color) {
			super(1, color);
		}

		@Override boolean payableWith(Set<Color> mana) {
			return mana.contains(color);
		}

		@Override ManaSymbol.Type type() {
			return ManaSymbol.Type.PRIMARY;
		}

		@Override void format(StringBuilder builder) {
			builder.append('{').append(color.code()).append('}');
		}
	}

	static final class Hybrid extends SymbolLogic {

		private final Color first;
		private final Color second;

		Hybrid(Color first, Color second) {
			super(1, first, second);
			this.first = first;
			this.second = second;
		}

		@Override boolean payableWith(Set<Color> mana) {
			return mana.contains(first) || mana.contains(second);
		}

		@Override ManaSymbol.Type type() {
			return ManaSymbol.Type.HYBRID;
		}

		@Override Pair<Color> colorPair() {
			return new Pair<>(first, second);
		}

		@Override void format(StringBuilder builder) {
			builder.append('{').append(first.code()).append('/').append(second.code()).append('}');
		}
	}

	/**
	 * Base class for Monocolored Hybrid and Phyrexian, which have a color but
	 * can also be paid without mana of that color.
	 */
	private static abstract class ColorOptional extends Monocolored {

		private ColorOptional(int converted, Color only) {
			super(converted, only);
		}

		@Override boolean payableWith(Set<Color> mana) {
			return true;
		}
	}

	static final class MonocoloredHybrid extends ColorOptional {

		MonocoloredHybrid(Color color) {
			super(2, color);
		}

		@Override ManaSymbol.Type type() {
			return ManaSymbol.Type.MONOCOLORED_HYBRID;
		}

		@Override void format(StringBuilder builder) {
			builder.append("{2/").append(color.code()).append('}');
		}
	}

	static final class Phyrexian extends ColorOptional {

		Phyrexian(Color color) {
			super(1, color);
		}

		@Override ManaSymbol.Type type() {
			return ManaSymbol.Type.PHYREXIAN;
		}

		@Override void format(StringBuilder builder) {
			builder.append('{').append(color.code()).append("/P}");
		}
	}

	static final class Snow extends SymbolLogic {

		Snow() {
			super(1);
		}

		@Override boolean payableWith(Set<Color> mana) {
			return true;
		}

		@Override ManaSymbol.Type type() {
			return ManaSymbol.Type.SNOW;
		}

		@Override void format(StringBuilder builder) {
			builder.append("{S}");
		}
	}

}
