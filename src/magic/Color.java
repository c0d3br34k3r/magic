package magic;

import java.util.EnumSet;
import java.util.Set;

import magic.misc.EnumSetInterner;
import magic.misc.SetInterner;

import com.google.common.collect.ImmutableSet;

/**
 * The five colors of mana. {@code Color}s are used almost exclusively as
 * elements of a {@code Set}, as most things that have a color in Magic can have
 * zero or more colors.
 */
public enum Color {
	/**
	 * The color White ({@code W}).
	 */
	WHITE("White", 'W'),
	/**
	 * The color Blue ({@code U}).
	 */
	BLUE("Blue", 'U'),
	/**
	 * The color Black ({@code B}).
	 */
	BLACK("Black", 'B'),
	/**
	 * The color Red ({@code R}).
	 */
	RED("Red", 'R'),
	/**
	 * The color Green ({@code G}).
	 */
	GREEN("Green", 'G');

	private final String name;
	private final char code;

	private Color(String name, char code) {
		this.name = name;
		this.code = code;
	}

	/**
	 * Returns the character used as an abbreviation for this color. For
	 * example: {@code BLUE.code()} returns {@code 'U'}.
	 * 
	 * @return the character that stands for this color
	 */
	public char code() {
		return code;
	}

	/**
	 * Returns the name of this color in title case.
	 */
	@Override public String toString() {
		return name;
	}

	/**
	 * An {@link SetInterner} for interning {@link Set}s of {@code Color}s.
	 */
	public static EnumSetInterner<Color> INTERNER = new EnumSetInterner<>();

	/**
	 * Returns an interned, immutable {@code Set} of {@code Color}s specified by
	 * the input string. For example, {@code "WUB"} will produce a {@code Set}
	 * containing {@code WHITE}, {@code BLUE}, and {@code BLACK}.
	 * 
	 * @param input
	 *            a string containing only the characters {@code 'W'},
	 *            {@code 'U'}, {@code 'B'}, {@code 'R'}, and {@code 'G'}. The
	 *            input is case-insensitive, order-insensitive, and repeated
	 *            values are ignored.
	 * @return the set of colors corresponding to the input
	 * @throws IllegalArgumentException
	 *             if the input string contains a character not corresponding to
	 *             a color
	 */
	public static ImmutableSet<Color> parseSet(String input) {
		EnumSet<Color> result = EnumSet.noneOf(Color.class);
		for (int i = 0; i < input.length(); i++) {
			result.add(forCode(input.charAt(i)));
		}
		return INTERNER.intern(result);
	}
	
	/**
	 * Returns a String of the color codes in this set of colors.
	 */
	public static String toString(Set<Color> colors) {
		StringBuilder builder = new StringBuilder();
		for (Color color : colors) {
			builder.append(color.code);
		}
		return builder.toString();
	}

	public static Color forCode(char letter) {
		switch (Character.toUpperCase(letter)) {
			case 'W':
				return WHITE;
			case 'U':
				return BLUE;
			case 'B':
				return BLACK;
			case 'R':
				return RED;
			case 'G':
				return GREEN;
			default:
				throw new IllegalArgumentException(Character.toString(letter));
		}
	}

}
