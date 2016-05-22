package magic;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ComparisonChain;

/**
 * An immutable object that represents a collector number. A collector number is
 * a positive integer with an optional letter, which, when present, can be used
 * to denote different halves of two-part cards, or variations of the same card
 * within an expansion.
 */
public final class CollectorNumber implements Comparable<CollectorNumber> {

	private final int number;
	private final Prefix prefix;
	private final Letter letter;

	private static final Pattern PATTERN = Pattern.compile("([S\\*]?)([0-9]+)([a-z]?)");

	public enum Prefix {
		NONE(""),
		STARTER("S"),
		STAR("*");

		private String display;

		Prefix(String display) {
			this.display = display;
		}

		@Override public String toString() {
			return display;
		}
	}

	public enum Letter {
		NONE(""),
		A("a"),
		B("b"),
		C("c"),
		D("d"),
		E("e");

		private String display;

		Letter(String display) {
			this.display = display;
		}

		@Override public String toString() {
			return display;
		}
	}

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
				toPrefix(matcher.group(1)),
				Integer.parseInt(matcher.group(2)),
				toLetter(matcher.group(3)));
	}

	private static Prefix toPrefix(String s) {
		if (s.isEmpty()) {
			return Prefix.NONE;
		}
		switch (s.charAt(0)) {
			case 'S':
				return Prefix.STARTER;
			case '*':
				return Prefix.STAR;
			default:
				throw new IllegalArgumentException(s);
		}
	}

	private static Letter toLetter(String s) {
		if (s.isEmpty()) {
			return Letter.NONE;
		}
		switch (s.charAt(0)) {
			case 'a':
				return Letter.A;
			case 'b':
				return Letter.B;
			case 'c':
				return Letter.C;
			case 'd':
				return Letter.D;
			case 'e':
				return Letter.E;
			default:
				throw new IllegalArgumentException(s);
		}
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
		return new CollectorNumber(Prefix.NONE, number, letter);
	}

	/**
	 * Returns a new {@code CollectorNumber} with a letter.
	 * 
	 * @param number
	 *            the number
	 * @param prefix
	 *            the prefix
	 */
	public static CollectorNumber of(Prefix prefix, int number) {
		return new CollectorNumber(prefix, number, Letter.NONE);
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
		this(Prefix.NONE, number, Letter.NONE);
	}

	private CollectorNumber(Prefix prefix, int number, Letter letter) {
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

	public Prefix prefix() {
		return prefix;
	}

	public Letter letter() {
		return letter;
	}

	/**
	 * Returns a representation of a collector number as it would appear on a
	 * Magic card.
	 */
	@Override public String toString() {
		return prefix.toString() + number + letter;
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
		return Objects.hash(number, prefix, letter);
	}

	@Override public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CollectorNumber)) {
			return false;
		}
		CollectorNumber other = (CollectorNumber) obj;
		return prefix == other.prefix
				&& number == other.number
				&& letter == other.letter;
	}
}
