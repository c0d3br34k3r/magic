package magic;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import magic.SymbolLogic.Colorless;
import magic.SymbolLogic.Generic;
import magic.SymbolLogic.Hybrid;
import magic.SymbolLogic.MonocoloredHybrid;
import magic.SymbolLogic.Phyrexian;
import magic.SymbolLogic.Primary;
import magic.SymbolLogic.Snow;
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
	X(new Variable()),

	/**
	 * This mana symbol does not really exist, but it is used to represent any
	 * colorless generic mana symbol. For example, three appearances of this
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
	PHYREXIAN_GREEN(new Phyrexian(Color.GREEN)),

	/**
	 * The Snow mana symbol <code>{S}</code>
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

	public void accept(Visitor visitor) {
		internal.accept(visitor);
	}

	private static final Map<Color, ManaSymbol> PRIMARY;
	private static final Map<Set<Color>, ManaSymbol> HYBRID;
	private static final Map<Color, ManaSymbol> MONOCOLORED_HYBRID;
	private static final Map<Color, ManaSymbol> PHYREXIAN;

	// private static final Range

	static {
		final Map<Color, ManaSymbol> primary = new EnumMap<>(Color.class);
		final ImmutableMap.Builder<Set<Color>, ManaSymbol> hybrid = ImmutableMap.builder();
		final Map<Color, ManaSymbol> monocoloredHybrid = new EnumMap<>(Color.class);
		final Map<Color, ManaSymbol> phyrexian = new EnumMap<>(Color.class);

		class CategoryVisitor extends Visitor {

			ManaSymbol symbol;

			@Override protected void primary(Color color) {
				primary.put(color, symbol);
			}

			@Override protected void hybrid(Color first, Color second) {
				hybrid.put(Sets.immutableEnumSet(EnumSet.of(first, second)), symbol);
			}

			@Override protected void monocoloredHybrid(Color color) {
				monocoloredHybrid.put(color, symbol);
			}

			@Override protected void phyrexian(Color color) {
				phyrexian.put(color, symbol);
			}
		}

		CategoryVisitor visitor = new CategoryVisitor();
		for (ManaSymbol symbol : values()) {
			visitor.symbol = symbol;
			symbol.accept(visitor);
		}
		PRIMARY = Maps.immutableEnumMap(primary);
		HYBRID = hybrid.build();
		MONOCOLORED_HYBRID = Maps.immutableEnumMap(monocoloredHybrid);
		PHYREXIAN = Maps.immutableEnumMap(phyrexian);
	}

	public static ManaSymbol primary(Color color) {
		return PRIMARY.get(color);
	}

	public static ManaSymbol hybrid(Color color1, Color color2) {
		if (color1 == color2) {
			throw new IllegalArgumentException(
					"Hybrid symbols must have two different colors, but both were " + color1);
		}
		return HYBRID.get(EnumSet.of(color1, color2));
	}

	public static ManaSymbol monocoloredHybrid(Color color) {
		return MONOCOLORED_HYBRID.get(color);
	}

	public static ManaSymbol phyrexian(Color color) {
		return PHYREXIAN.get(color);
	}

	public static abstract class Visitor {

		protected void generic() {}

		protected void colorless() {}

		protected void variable() {}

		protected void snow() {}

		protected void primary(Color color) {}

		protected void hybrid(Color first, Color second) {}

		protected void monocoloredHybrid(Color color) {}

		protected void phyrexian(Color color) {}
	}

}
