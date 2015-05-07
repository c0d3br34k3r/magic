package magic;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public abstract class Element {

	private final ImmutableSet<Color> colors;
	private final int converted;
	private final String innerPart;

	Element(ImmutableSet<Color> colors, int converted, String innerPart) {
		this.colors = colors;
		this.converted = converted;
		this.innerPart = innerPart;
	}

	Element(Color color, int converted, String innerPart) {
		this(ImmutableSet.of(color), converted, innerPart);
	}

	Element(int converted, String innerPart) {
		this(ImmutableSet.<Color> of(), converted, innerPart);
	}

	public abstract boolean payableWith(Set<Color> mana);

	@Override public final String toString() {
		return '{' + innerPart + '}';
	}

	public final int converted() {
		return converted;
	}

	public final ImmutableSet<Color> colors() {
		return colors;
	}
	
	final String innerPart() {
		return innerPart;
	}

	public static Element parse(String input) {
		String inner = stripBrackets(input);
		Element element = Symbol.parseInner(inner);
		if (inner == null) {
			
		}
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
