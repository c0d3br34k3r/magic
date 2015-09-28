package magic.misc;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;

/**
 * Utility for rewriting a card's text without reminder text. The functionality
 * of this utility is somewhat experimental.
 * <p>
 * As of now, there are four forms reminder text takes.
 * <li>End-of-line reminder text:
 * "Alloy Golem is the chosen color. (It's still an artifact.)"</li>
 * <li>Middle-of-line reminder text:
 * "...without paying its mana cost for as long as it remains exiled. (If it has X in its mana cost, X is 0.) At the beginning of the next end step..."
 * </li>
 * <li>First line reminder text: "({G/U} can be paid with either {G} or {U}.)"</li>
 * <li>Final keyword reminder text:
 * "Defender; reach (This creature can block creatures with flying.)" This form
 * is different because lists of keywords normally use commas. When the final
 * keyword has reminder text, however, semicolons are used. In this case, they
 * are converted to commas.</li> </ul>
 */
@Beta
public final class ReminderText {

	private static Function<String, String> replacer(
			String regex,
			final String replacement) {
		final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);

		return new Function<String, String>() {
			@Override public String apply(@Nullable String input) {
				return pattern.matcher(input).replaceAll(replacement);
			}
		};
	}

	private static final List<Function<String, String>> REPLACERS =
			ImmutableList.of(
					// Last keyword reminder text
					new Function<String, String>() {
						final Pattern keywords = Pattern.compile(
								"^((?:.+; )+.+) \\(.+\\)$",
								Pattern.MULTILINE);

						@Override public String apply(@Nullable String input) {
							Matcher matcher = keywords.matcher(input);
							// Should only happen once per card
							if (matcher.find()) {
								StringBuilder builder =
										new StringBuilder(input);
								builder.replace(matcher.start(), matcher.end(),
										matcher.group(1).replace(';', ','));
								return builder.toString();
							}
							return input;
						}
					},
					// End of line reminder text
					replacer("^(.+) \\(.+\\)$", "$1"),
					// First line reminder text
					replacer("\\A\\(.+\\)$\\n?", ""),
					// Middle of line reminder text;
					replacer("^(.+) \\(.+\\)(.+)$", "$1$2"));

	/**
	 * Attempts to remove all reminder text from a card.
	 * 
	 * @param text
	 *            the card's text
	 * @return the card's text with no reminder text
	 * @throws IllegalArgumentException
	 *             if not all reminder text could be removed.
	 */
	public static String remove(String text) {
		if (text.indexOf('(') == -1) {
			return text;
		}
		String result = text;
		for (Function<String, String> replacer : REPLACERS) {
			result = replacer.apply(result);
		}
		if (result.contains("(")) {
			throw new IllegalArgumentException(
					"Failed to remove all reminder text.");
		}

		return result;
	}

	private ReminderText() {}
}
