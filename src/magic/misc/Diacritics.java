package magic.misc;

import java.util.Map;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ImmutableMap;

public class Diacritics {

	private static final Map<Character, String> REPLACEMENTS =
			ImmutableMap.<Character, String> builder()
					.put('Æ', "Ae")
					.put('à', "a")
					.put('á', "a")
					.put('â', "a")
					.put('é', "e")
					.put('í', "i")
					.put('ö', "o")
					.put('ú', "u")
					.put('û', "u")
					.put('—', "-")
					.put('‘', "'")
					.put('•', "*")
					.put('−', "-")
					.build();

	private static final CharMatcher NON_ASCII = CharMatcher.ASCII.negate();

	public static String remove(String input) {
		int end = NON_ASCII.indexIn(input);
		if (end == -1) {
			return input;
		}
		int begin = 0;
		StringBuilder result = new StringBuilder();
		do {
			result.append(input.substring(begin, end));
			result.append(REPLACEMENTS.get(input.charAt(end)));
			begin = end + 1;
			end = NON_ASCII.indexIn(input, begin);
		} while (end != -1);
		result.append(input.substring(begin));
		return result.toString();
	}

}
