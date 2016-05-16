package magic;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.google.common.base.CharMatcher;
import com.google.common.collect.ComparisonChain;

/**
 * An immutable object that represents a collector number. A collector number is
 * a positive integer with an optional letter, which, when present, can be used
 * to denote different halves of two-part cards, or variations of the same card
 * within an expansion.
 */
public final class CollectorNumber implements Comparable<CollectorNumber> {

	private final int number;
	private final @Nullable Character prefix;
	private final @Nullable Character letter;

	private static final Pattern PATTERN = Pattern.compile("([S\\*]?)([0-9]+)([a-z]?)");
	private static final CharMatcher PREFIX = CharMatcher.anyOf("S*");
	private static final CharMatcher LETTER = CharMatcher.inRange('a', 'z');

	/**
	 * Parses the given input as a collector number. The input must be in the
	 * form of an integer greater than or equal to 1 followed immediately by a
	 * letter, which must be within a specific range. The input may begin with
	 * "S" if it is a starter card.
	 * <p>
	 * The following examples are valid input:
	 * <ul>
	 * <li>200</li>
	 * <li>3</li>
	 * <li>56b</li>
	 * <li>S3</li>
	 * </ul>
	 * <p>
	 * The following examples are invalid input:
	 * <ul>
	 * <li>-30</li>
	 * <li>4%</li>
	 * <li>70ab</li>
	 * </ul>
	 */
	public static CollectorNumber parse(String input) {
		try {
			// most common type of collector number
			return new CollectorNumber(Integer.parseInt(input));
		} catch (NumberFormatException ignore) {
			// continue
		}
		Matcher matcher = PATTERN.matcher(input);
		if (!matcher.matches()) {
			throw new IllegalArgumentException(input);
		}
		return new CollectorNumber(
				toCharacter(matcher.group(1)),
				Integer.parseInt(matcher.group(2)),
				toCharacter(matcher.group(3)));
	}

	private static @Nullable Character toCharacter(String s) {
		return s.isEmpty() ? null : s.charAt(0);
	}

	/**
	 * Returns a new {@code CollectorNumber} with a letter.
	 * 
	 * @param number
	 *            the number
	 * @param letter
	 *            the letter
	 */
	public static CollectorNumber of(int number, char letter) {
		if (!LETTER.matches(letter)) {
			throw new IllegalArgumentException(String.valueOf(letter));
		}
		return new CollectorNumber(null, number, letter);
	}

	/**
	 * Returns a new {@code CollectorNumber} with a letter.
	 * 
	 * @param number
	 *            the number
	 * @param prefix
	 *            the prefix
	 */
	public static CollectorNumber of(char prefix, int number) {
		if (!PREFIX.matches(prefix)) {
			throw new IllegalArgumentException(String.valueOf(prefix));
		}
		return new CollectorNumber(prefix, number, null);
	}

	/**
	 * Returns a new {@code CollectorNumber} without a letter.
	 * 
	 * @param number
	 *            the number
	 */
	public static CollectorNumber of(int number) {
		return new CollectorNumber(number);
	}

	private CollectorNumber(int number) {
		this(null, number, null);
	}

	private CollectorNumber(Character prefix, int number, Character letter) {
		if (number < 1) {
			throw new IllegalArgumentException(String.valueOf(number));
		}
		this.prefix = prefix;
		this.number = number;
		this.letter = letter;
	}

	/**
	 * Returns the number part of this {@code CollectorNumber}.
	 */
	public int number() {
		return number;
	}

	/**
	 * Returns the letter part of the {@code CollectorNumber}, or {@code null}
	 * if there is no letter.
	 */
	public @Nullable Character prefix() {
		return prefix;
	}

	public @Nullable Character letter() {
		return letter;
	}

	/**
	 * Whether this collector number is a Starter collector number (prefixed
	 * with "S").
	 */
	public boolean starter() {
		return prefix.equals('S');
	}

	/**
	 * Returns a representation of a collector number as it would appear on a
	 * Magic card.
	 */
	@Override public String toString() {
		return new StringBuilder().append(prefix).append(number).append(letter).toString();
	}

	/**
	 * Orders collector numbers by their numerical value; when those are the
	 * same, by their letter. Starter numbers come at the end.
	 */
	@Override public int compareTo(CollectorNumber o) {
		return ComparisonChain.start()
				.compare(number, o.number)
				.compare(prefix, o.prefix)
				.compare(letter, o.letter)
				.result();
	}

	@Override public int hashCode() {
		return Objects.hash(prefix, number, letter);
	}

	@Override public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CollectorNumber)) {
			return false;
		}
		CollectorNumber other = (CollectorNumber) obj;
		return prefix.equals(other.prefix)
				&& number == other.number
				&& letter.equals(other.letter);
	}
}
