package magic;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import magic.SymbolLogic.Colorless;
import magic.SymbolLogic.Generic;
import magic.SymbolLogic.Hybrid;
import magic.SymbolLogic.MonocoloredHybrid;
import magic.SymbolLogic.Phyrexian;
import magic.SymbolLogic.Primary;
import magic.SymbolLogic.Snow;
import magic.SymbolLogic.Variable;

/**
 * 
 */
public enum ManaSymbol {

	/**
	 * Paid with any amount of mana.
	 */
	X(new Variable()),

	/**
	 * Paid with one mana of any type.
	 */
	GENERIC(new Generic()),

	/**
	 * Paid with one colorless mana.
	 */
	COLORLESS(new Colorless()),

	/**
	 * Paid with one white or blue mana.
	 */
	HYBRID_WHITE_BLUE(new Hybrid(Color.WHITE, Color.BLUE)),
	/**
	 * Paid with one blue or black mana.
	 */
	HYBRID_BLUE_BLACK(new Hybrid(Color.BLUE, Color.BLACK)),
	/**
	 * Paid with one black or red mana.
	 */
	HYBRID_BLACK_RED(new Hybrid(Color.BLACK, Color.RED)),
	/**
	 * Paid with one red or green mana.
	 */
	HYBRID_RED_GREEN(new Hybrid(Color.RED, Color.GREEN)),
	/**
	 * Paid with one green or white mana.
	 */
	HYBRID_GREEN_WHITE(new Hybrid(Color.GREEN, Color.WHITE)),

	/**
	 * Paid with one white or black mana.
	 */
	HYBRID_WHITE_BLACK(new Hybrid(Color.WHITE, Color.BLACK)),
	/**
	 * Paid with one blue or red mana.
	 */
	HYBRID_BLUE_RED(new Hybrid(Color.BLUE, Color.RED)),
	/**
	 * Paid with one black or green mana.
	 */
	HYBRID_BLACK_GREEN(new Hybrid(Color.BLACK, Color.GREEN)),
	/**
	 * Paid with one red or white mana.
	 */
	HYBRID_RED_WHITE(new Hybrid(Color.RED, Color.WHITE)),
	/**
	 * Paid with one green or blue mana.
	 */
	HYBRID_GREEN_BLUE(new Hybrid(Color.GREEN, Color.BLUE)),

	/**
	 * Paid with one white mana.
	 */
	WHITE(new Primary(Color.WHITE)),
	/**
	 * Paid with one blue mana.
	 */
	BLUE(new Primary(Color.BLUE)),
	/**
	 * Paid with one black mana.
	 */
	BLACK(new Primary(Color.BLACK)),
	/**
	 * Paid with one red mana.
	 */
	RED(new Primary(Color.RED)),
	/**
	 * Paid with one green mana.
	 */
	GREEN(new Primary(Color.GREEN)),

	/**
	 * Paid with one white mana or two generic mana.
	 */
	MONOCOLORED_HYBRID_WHITE(new MonocoloredHybrid(Color.WHITE)),
	/**
	 * Paid with one blue mana or two generic mana.
	 */
	MONOCOLORED_HYBRID_BLUE(new MonocoloredHybrid(Color.BLUE)),
	/**
	 * Paid with one black mana or two generic mana.
	 */
	MONOCOLORED_HYBRID_BLACK(new MonocoloredHybrid(Color.BLACK)),
	/**
	 * Paid with one red mana or two generic mana.
	 */
	MONOCOLORED_HYBRID_RED(new MonocoloredHybrid(Color.RED)),
	/**
	 * Paid with one green mana or two generic mana.
	 */
	MONOCOLORED_HYBRID_GREEN(new MonocoloredHybrid(Color.GREEN)),

	/**
	 * Paid with one white mana or 2 life.
	 */
	PHYREXIAN_WHITE(new Phyrexian(Color.WHITE)),
	/**
	 * Paid with one blue mana or 2 life.
	 */
	PHYREXIAN_BLUE(new Phyrexian(Color.BLUE)),
	/**
	 * Paid with one black mana or 2 life.
	 */
	PHYREXIAN_BLACK(new Phyrexian(Color.BLACK)),
	/**
	 * Paid with one red mana or 2 life.
	 */
	PHYREXIAN_RED(new Phyrexian(Color.RED)),
	/**
	 * Paid with one green mana or 2 life.
	 */
	PHYREXIAN_GREEN(new Phyrexian(Color.GREEN)),

	/**
	 * Paid with one mana from a snow permanent.
	 */
	SNOW(new Snow());

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
	 * <li>Generic, Monocolored Hybrid, Phyrexian, and Variable symbols are
	 * always payable.</li>
	 * </ul>
	 */
	public boolean payableWith(Set<Color> mana) {
		return internal.payableWith(mana);
	}

	/**
	 * Returns {@code false} if any of the symbols are not payable with the
	 * given colors of mana.
	 */
	public static boolean payableWith(Collection<ManaSymbol> symbols, Set<Color> mana) {
		for (ManaSymbol symbol : symbols) {
			if (!symbol.payableWith(mana)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @deprecated I never asked for this.
	 */
	@Deprecated
	public Type type() {
		return internal.type();
	}

	public static enum Type {
		VARIABLE,
		GENERIC,
		COLORLESS,
		HYBRID,
		PRIMARY,
		MONOCOLORED_HYBRID,
		PHYREXIAN,
		SNOW;
	}

}
