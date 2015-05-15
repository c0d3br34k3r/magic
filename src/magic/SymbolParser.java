package magic;

import magic.Symbol.Repeatable;

import com.google.common.collect.ImmutableMap;

public abstract class SymbolParser {

	protected abstract void repeatable(Repeatable symbol);

	protected abstract void generic(int value);

	public final void parseMultiple(String input) {
		int begin = 0;
		while (begin < input.length()) {
			if (input.charAt(begin) != '{') {
				throw new IllegalArgumentException(input);
			}
			int end = input.indexOf('}', begin + 1);
			if (end == -1) {
				throw new IllegalArgumentException(input);
			}
			parseInner(input.substring(begin + 1, end));
			begin = end + 1;
		}
	}
	
	public final void parse(String input) {
		parseInner(stripBrackets(input));
	}
	
	private void parseInner(String inner) {
		Repeatable symbol = getRepeatable(inner);
		if (symbol != null) {
			repeatable(symbol);
		} else {
			try {
				int value = Integer.parseInt(inner);
				if (value < 0) {
					throw new IllegalArgumentException("generic value < 0");
				}
				generic(value);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("bad symbol: {" + inner + '}');
			}
		}
	}

	private static String stripBrackets(String input) {
		int length = input.length();
		if (length != 0
				&& input.charAt(0) == '{'
				&& input.charAt(length - 1) == '}') {
			return input.substring(1, length - 1);
		}
		throw new IllegalArgumentException(input);
	}
	
	static Repeatable getRepeatable(String inner) {
		return PARSE_LOOKUP.get(inner);
	}

	private static final ImmutableMap<String, Repeatable> PARSE_LOOKUP;

	static {
		ImmutableMap.Builder<String, Repeatable> builder = ImmutableMap.builder();
		for (Repeatable symbol : Symbols.values()) {
			builder.put(symbol.innerPart(), symbol);
		}
		PARSE_LOOKUP = builder.build();
	}
	
}
