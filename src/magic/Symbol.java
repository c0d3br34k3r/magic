package magic;

import java.util.Collection;
import java.util.Set;

import magic.Repeatable.Hybrid;
import magic.Repeatable.MonocoloredHybrid;
import magic.Repeatable.Phyrexian;
import magic.Repeatable.Primary;
import magic.Repeatable.Variable;

import com.google.common.collect.ImmutableSet;

/**
 * All mana symbol:s other than numeric symbols; in other words, all symbols that
 * may appear more than once in a mana cost. The reason constant colorless
 * symbols (such as <code>{1}</code>. or <code>{7}</code>.) are excluded is that
 * they are better represented as plain {@code int}s, to make them easier to
 * work with, and to reduce the complexity of this {@code enum}. See
 * {@link ManaCost} for further details on this conceptualization.
 * 
 * @see ManaCost
 */
public abstract class Symbol {

	private final ImmutableSet<Color> colors;
	private final int converted;
	private final String representation;

	Symbol(ImmutableSet<Color> colors, int converted, String representation) {
		this.colors = colors;
		this.converted = converted;
		this.representation = representation;
	}

	Symbol(Color color, int converted, String representation) {
		this(ImmutableSet.of(color), converted, representation);
	}

	Symbol(int converted, String representation) {
		this(ImmutableSet.<Color> of(), converted, representation);
	}

	public final int converted() {
		return converted;
	}

	public final ImmutableSet<Color> colors() {
		return colors;
	}

	@Override public final String toString() {
		return representation;
	}

	public abstract boolean payableWith(Set<Color> mana);

	public abstract void accept(Visitor visitor);
	
	public interface Visitor {

		void visit(Numeric colorless);

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
	
	/**
	 * Returns the {@code Symbol} with the given representation.
	 * 
	 * @throws IllegalArgumentException
	 *             if the input does not correspond to a symbol.
	 */
	public static Symbol parse(String input) {
		return parseInner(stripBrackets(input));
	}

	static Symbol parseInner(String inner) {
		Symbol symbol = Repeatable.parseInner(inner);
		if (symbol == null) {
			symbol = Numeric.parseInner(inner);
			if (symbol == null) {
				throw new IllegalArgumentException(inner);
			}
		}
		return symbol;
	}

	static String stripBrackets(Symbol symbol) {
		return stripBrackets(symbol.toString());
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

}
