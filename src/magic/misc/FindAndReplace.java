package magic.misc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindAndReplace {

	public interface Replacer {

		String replacement(Matcher matcher);
	}

	public static String replace(String string, Pattern find, Replacer replace) {
		Matcher matcher = find.matcher(string);
		if (!matcher.find()) {
			return string;
		}
		int index = 0;
		StringBuilder builder = new StringBuilder();
		do {
			builder.append(string, index, matcher.start()).append(replace.replacement(matcher));
			index = matcher.end();
		} while (matcher.find(index));
		builder.append(string, index, string.length());
		return builder.toString();
	}

}
