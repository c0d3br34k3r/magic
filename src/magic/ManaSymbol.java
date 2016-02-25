package magic;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import magic.SymbolLogic.Colorless;
import magic.SymbolLogic.Generic;
import magic.SymbolLogic.Hybrid;
import magic.SymbolLogic.MonocoloredHybrid;
import magic.SymbolLogic.Phyrexian;
import magic.SymbolLogic.Primary;
import magic.SymbolLogic.Variable;

/**
 * All mana symbols other than constant colorless symbols; in other words, all
 * symbols that may appear more than once in a mana cost. The reason constant
 * colorless symbols (such as <code>{1}</code> or <code>{7}</code>) are excluded
 * is that they are better represented as plain {@code int}s, to make them
 * easier to work with, and to reduce the complexity of this {@code enum}. See
 * {@link ManaCost} for further details on this conceptualization.
 * 
 * @see ManaCost
 */
public enum ManaSymbol {

	/**
	 * The variable mana symbol <code>{X}</code>
	 */
	X(new Variable('X')),
	
	/**
	 * This mana symbol does not really exist, but it is used to represent any 
	 * colorless generic mana symbol.  For example, three appearances of this 
	 * symbol in a {@link ManaCost} represents the symbol <code>{3}</code>.
	 * <p>
	 * The String representation of this object is <code>{1}</code>, but this is
	 * primarily for debugging purposes, as this object has no real-world 
	 * meaning.
	 */
	GENERIC(new Generic()),
	
	/**
	 * The colorless mana symbol <code>{C}</code>
	 */
	COLORLESS(new Colorless()),

	/**
	 * The hybrid White-Blue mana symbol <code>{W/U}</code>
	 */
	HYBRID_WHITE_BLUE(new Hybrid(Color.WHITE, Color.BLUE)),
	/**
	 * The hybrid Blue-Black mana symbol <code>{U/B}</code>
	 */
	HYBRID_BLUE_BLACK(new Hybrid(Color.BLUE, Color.BLACK)),
	/**
	 * The hybrid Black-Red mana symbol <code>{B/R}</code>
	 */
	HYBRID_BLACK_RED(new Hybrid(Color.BLACK, Color.RED)),
	/**
	 * The hybrid Red-Green mana symbol <code>{R/G}</code>
	 */
	HYBRID_RED_GREEN(new Hybrid(Color.RED, Color.GREEN)),
	/**
	 * The hybrid Green-White mana symbol <code>{G/W}</code>
	 */
	HYBRID_GREEN_WHITE(new Hybrid(Color.GREEN, Color.WHITE)),

	/**
	 * The hybrid White-Black mana symbol <code>{W/B}</code>
	 */
	HYBRID_WHITE_BLACK(new Hybrid(Color.WHITE, Color.BLACK)),
	/**
	 * The hybrid Blue-Red mana symbol <code>{U/R}</code>
	 */
	HYBRID_BLUE_RED(new Hybrid(Color.BLUE, Color.RED)),
	/**
	 * The hybrid Black-Green mana symbol <code>{B/G}</code>
	 */
	HYBRID_BLACK_GREEN(new Hybrid(Color.BLACK, Color.GREEN)),
	/**
	 * The hybrid Red-White mana symbol <code>{R/W}</code>
	 */
	HYBRID_RED_WHITE(new Hybrid(Color.RED, Color.WHITE)),
	/**
	 * The hybrid Green-Blue mana symbol <code>{G/U}</code>
	 */
	HYBRID_GREEN_BLUE(new Hybrid(Color.GREEN, Color.BLUE)),
	
	/**
	 * The primary White mana symbol <code>{W}</code>
	 */
	WHITE(new Primary(Color.WHITE)),
	/**
	 * The primary Blue mana symbol <code>{U}</code>
	 */
	BLUE(new Primary(Color.BLUE)),
	/**
	 * The primary Black mana symbol <code>{B}</code>
	 */
	BLACK(new Primary(Color.BLACK)),
	/**
	 * The primary Red mana symbol <code>{R}</code>
	 */
	RED(new Primary(Color.RED)),
	/**
	 * The primary Green mana symbol {G}</code>
	 */
	GREEN(new Primary(Color.GREEN)),

	/**
	 * The monocolored hybrid White mana symbol <code>{2/W}</code>
	 */
	MONOCOLORED_HYBRID_WHITE(new MonocoloredHybrid(Color.WHITE)),
	/**
	 * The monocolored hybrid Blue mana symbol <code>{2/U}</code>
	 */
	MONOCOLORED_HYBRID_BLUE(new MonocoloredHybrid(Color.BLUE)),
	/**
	 * The monocolored hybrid Black mana symbol <code>{2/B}</code>
	 */
	MONOCOLORED_HYBRID_BLACK(new MonocoloredHybrid(Color.BLACK)),
	/**
	 * The monocolored hybrid Red mana symbol <code>{2/R}</code>
	 */
	MONOCOLORED_HYBRID_RED(new MonocoloredHybrid(Color.RED)),
	/**
	 * The monocolored hybrid Green mana symbol <code>{2/G}</code>
	 */
	MONOCOLORED_HYBRID_GREEN(new MonocoloredHybrid(Color.GREEN)),

	/**
	 * The Phyrexian White mana symbol <code>{W/P}</code>
	 */
	PHYREXIAN_WHITE(new Phyrexian(Color.WHITE)),
	/**
	 * The Phyrexian Blue mana symbol <code>{U/P}</code>
	 */
	PHYREXIAN_BLUE(new Phyrexian(Color.BLUE)),
	/**
	 * The Phyrexian Black mana symbol <code>{B/P}</code>
	 */
	PHYREXIAN_BLACK(new Phyrexian(Color.BLACK)),
	/**
	 * The Phyrexian Red mana symbol <code>{R/P}</code>
	 */
	PHYREXIAN_RED(new Phyrexian(Color.RED)),
	/**
	 * The Phyrexian Green mana symbol <code>{G/P}</code>
	 */
	PHYREXIAN_GREEN(new Phyrexian(Color.GREEN));

	private final SymbolLogic internal;

	private ManaSymbol(SymbolLogic internal) {
		this.internal = internal;
	}

	/**
	 * The converted value of this symbol.
	 * 
	 * @return this symbol's converted value
	 */
	public int converted() {
		return internal.converted();
	}

	/**
	 * The {@code Set} of {@code Color}s of the mana symbol
	 * 
	 * @return this symbol's colors
	 */
	public ImmutableSet<Color> colors() {
		return internal.colors();
	}

	/**
	 * Returns whether this mana symbol can be paid with the given colors of
	 * mana.
	 * <ul>
	 * <li>Primary symbols check to see if the set contains their color.</li>
	 * <li>Hybrid symbols check to see if the set contains either of their
	 * colors.</li>
	 * <li>Generic, Monocolored Hybrid, Phyrexian, and Variable symbols are always
	 * payable.</li>
	 * </ul>
	 */
	public boolean payableWith(Set<Color> mana) {
		return internal.payableWith(mana);
	}

	/**
	 * Returns this symbol's {@code String} representation. {@code Symbol}s are
	 * rendered as either a single value, or two values separated by as slash
	 * ('/'), and enclosed in curly brackets ('{' and '}').
	 */
	@Override public String toString() {
		return internal.toString();
	}
	
	public String format(int occurences) {
		return internal.format(occurences);
	}
	
	public void formatTo(StringBuilder builder, int occurences) {
		internal.formatTo(builder, occurences);
	}


	/**
	 * Returns the {@code Symbol} with the given representation, or {@code null}
	 * if no mana symbol matches.  Does not return the Generic mana symbol for 
	 * any input.
	 */
	public static ManaSymbol parse(String input) {
		return SYMBOLS.get(input);
	}

	/**
	 * Returns {@code false} if any of the symbols are not payable with the
	 * given colors of mana.
	 */
	public static boolean payableWith(Collection<ManaSymbol> symbols,
			Set<Color> mana) {
		for (ManaSymbol symbol : symbols) {
			if (!symbol.payableWith(mana)) {
				return false;
			}
		}
		return true;
	}

	private static ImmutableMap<String, ManaSymbol> SYMBOLS;

	static {
		ImmutableMap.Builder<String, ManaSymbol> builder = ImmutableMap.builder();
		for (ManaSymbol symbol : values()) {
			if (symbol != ManaSymbol.GENERIC) {
				builder.put(symbol.toString(), symbol);
			}
		}
		SYMBOLS = builder.build();
	}

}
