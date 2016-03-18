package magic.misc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindAndReplace {

	public interface Replacer {

		String replacement(Matcher matcher);
	}

	public static String replace(String string, Pattern find, Replacer replace) {
		StringBuilder builder = new StringBuilder();
		Matcher matcher = find.matcher(string);
		int index = 0;
		while (matcher.find(index)) {
			builder.append(string, index, matcher.start()).append(replace.replacement(matcher));
			index = matcher.end();
		}
		builder.append(string, index, string.length());
		return builder.toString();
	}

}
