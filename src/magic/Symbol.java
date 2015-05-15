package magic;

import java.util.Set;

import magic.Symbols.Visitor;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

public abstract class Symbol{

	private final ImmutableSet<Color> colors;
	private final int converted;
	private final String represenatation;

	Symbol(ImmutableSet<Color> colors, int converted, String innerPart) {
		this.colors = colors;
		this.converted = converted;
		this.represenatation = '{' + innerPart + '}';
	}

	Symbol(Color color, int converted, String innerPart) {
		this(ImmutableSet.of(color), converted, innerPart);
	}

	Symbol(int converted, String innerPart) {
		this(ImmutableSet.<Color> of(), converted, innerPart);
	}

	public abstract boolean payableWith(Set<Color> mana);

	@Override public final String toString() {
		return represenatation;
	}

	public final int converted() {
		return converted;
	}

	public final ImmutableSet<Color> colors() {
		return colors;
	}

	final String innerPart() {
		return represenatation.substring(1, represenatation.length() - 1);
	}

	public abstract void accept(Visitor visitor);

	protected abstract String format(int count);

	public static abstract class Repeatable extends Symbol {

		Repeatable(ImmutableSet<Color> colors,
				int converted, String innerPart) {
			super(colors, converted, innerPart);
		}

		Repeatable(Color color, int converted, String representation) {
			this(ImmutableSet.of(color), converted, representation);
		}

		@Override protected String format(int count) {
			return Strings.repeat(toString(), count);
		}
	}

	public static abstract class Monocolored extends Repeatable {

		private final Color color;

		Monocolored(Color color, int converted, String innerPart) {
			super(color, converted, innerPart);
			this.color = color;
		}

		public Color color() {
			return color;
		}
	}

}
