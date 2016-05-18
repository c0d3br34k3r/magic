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
		// TODO: store converted?
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

	abstract void accept(ManaSymbol.Visitor visitor);

	static final class Variable extends SymbolLogic {

		Variable() {
			super(0);
		}

		@Override boolean payableWith(Set<Color> mana) {
			return true;
		}

		@Override void accept(ManaSymbol.Visitor visitor) {
			visitor.variable();
		}
	}

	static class Generic extends SymbolLogic {

		Generic() {
			super(1);
		}

		@Override boolean payableWith(Set<Color> mana) {
			return true;
		}

		@Override void accept(ManaSymbol.Visitor visitor) {
			visitor.generic();
		}
	}

	static class Colorless extends SymbolLogic {

		Colorless() {
			super(1);
		}

		@Override boolean payableWith(Set<Color> mana) {
			return true;
		}

		@Override void accept(ManaSymbol.Visitor visitor) {
			visitor.colorless();
		}
	}

	static abstract class Monocolored extends SymbolLogic {

		protected final Color color;

		Monocolored(int converted, Color only) {
			super(converted, only);
			this.color = only;
		}

		Color color() {
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

		@Override void accept(ManaSymbol.Visitor visitor) {
			visitor.primary(color);
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

		@Override void accept(ManaSymbol.Visitor visitor) {
			visitor.hybrid(first, second);
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

		@Override void accept(ManaSymbol.Visitor visitor) {
			visitor.monocoloredHybrid(color);
		}
	}

	static final class Phyrexian extends ColorOptional {

		Phyrexian(Color color) {
			super(1, color);
		}

		@Override void accept(ManaSymbol.Visitor visitor) {
			visitor.phyrexian(color);
		}
	}

	static final class Snow extends SymbolLogic {

		Snow() {
			super(1);
		}

		@Override boolean payableWith(Set<Color> mana) {
			return true;
		}

		@Override void accept(ManaSymbol.Visitor visitor) {
			visitor.snow();
		}
	}

}
