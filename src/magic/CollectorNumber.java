package magic;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;

/**
 * An immutable object that represents a collector number. A collector number is
 * a positive integer with an optional letter, which, when present, can be used
 * to denote different halves of two-part cards, or variations of the same card
 * within an expansion.
 */
public final class CollectorNumber implements Comparable<CollectorNumber> {

	private final int number;
	private final Letter letter;
	private final boolean starter;

	public enum Letter {
		NONE(""),
		A("a"),
		B("b");

		private final String value;

		private Letter(String value) {
			this.value = value;
		}

		@Override public String toString() {
			return value;
		}

		private static Letter valueOf(char c) {
			return Letter.valueOf(
					Character.toString(Character.toUpperCase(c)));
		}
	}

	private static final Pattern PATTERN =
			Pattern.compile("(S?)(\\d+)([a-z]?)");
	
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
		String starter = matcher.group(1);
		String number = matcher.group(2);
		String letter = matcher.group(3);
		return new CollectorNumber(
				!starter.isEmpty(),
				Integer.parseInt(number),
				letter.isEmpty()
						? Letter.NONE
						: Letter.valueOf(letter.charAt(0)));
	}

	/**
	 * Returns a new {@code CollectorNumber} with a letter.
	 * 
	 * @param number
	 *            the number
	 * @param letter
	 *            the letter
	 */
	public static CollectorNumber of(int number, Letter letter) {
		Preconditions.checkNotNull(letter);
		return new CollectorNumber(false, number, letter);
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

	/**
	 * Returns a new "Starter" {@code CollectorNumber} without a letter.
	 * 
	 * @param number
	 *            the number
	 */
	public static CollectorNumber starter(int number) {
		return new CollectorNumber(true, number, Letter.NONE);
	}

	private CollectorNumber(int number) {
		this(false, number, Letter.NONE);
	}

	private CollectorNumber(boolean starter, int number, Letter letter) {
		if (number < 1) {
			throw new IllegalArgumentException("number < 1");
		}
		this.starter = starter;
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
	public Letter letter() {
		return letter;
	}

	/**
	 * Whether this collector number is a Starter collector number (prefixed
	 * with "S").
	 */
	public boolean starter() {
		return starter;
	}

	/**
	 * Returns a representation of a collector number as it would appear on a
	 * Magic card.
	 */
	@Override public String toString() {
		StringBuilder builder = new StringBuilder();
		if (starter) {
			builder.append('S');
		}
		builder.append(number);
		if (letter != Letter.NONE) {
			builder.append(letter);
		}
		return builder.toString();
	}

	/**
	 * Orders collector numbers by their numerical value; when those are the
	 * same, by their letter. Starter numbers come at the end.
	 */
	@Override public int compareTo(CollectorNumber o) {
		return ComparisonChain.start()
				.compareFalseFirst(starter, o.starter)
				.compare(number, o.number)
				.compare(letter, o.letter)
				.result();
	}

	@Override public int hashCode() {
		return Objects.hash(starter, number, letter);
	}

	@Override public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CollectorNumber)) {
			return false;
		}
		CollectorNumber other = (CollectorNumber) obj;
		return starter == other.starter
				&& number == other.number
				&& letter == other.letter;
	}
}
