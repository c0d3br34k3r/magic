package magic;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
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

	abstract ManaSymbol.Type type();

	Color color() {
		throw new IllegalStateException();
	}

	List<Color> colorPair() {
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

		@Override List<Color> colorPair() {
			return ImmutableList.of(first, second);
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
	}

	static final class Phyrexian extends ColorOptional {

		Phyrexian(Color color) {
			super(1, color);
		}

		@Override ManaSymbol.Type type() {
			return ManaSymbol.Type.PHYREXIAN;
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
	}

}
