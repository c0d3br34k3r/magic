package magic;

import javax.annotation.Nullable;

/**
 * An immutable object that may represent a constant or variable value, used to
 * represent power and toughness. Expressions such as {@code 6} or {@code -1}
 * are constant, while the expressions {@code *} and {@code 7-*} are variable.
 * <p>
 * Constant expressions are cached between a certain range intended to maximize
 * performance.
 */
public abstract class Expression implements Comparable<Expression> {

	private static final int CACHE_LOW = -1;
	private static final int CACHE_HIGH = 15;
	private static final Expression[] CACHE;

	static {
		CACHE = new Expression[CACHE_HIGH - CACHE_LOW + 1];
		for (int i = CACHE_LOW; i <= CACHE_HIGH; i++) {
			CACHE[i - CACHE_LOW] = new ConstantExpression(i);
		}
	}

	/**
	 * Returns a constant expression with the given value.
	 */
	public static Expression of(int value) {
		if (CACHE_LOW <= value && value <= CACHE_HIGH) {
			return CACHE[value - CACHE_LOW];
		}
		// should cause a warning
		return new ConstantExpression(value);
	}

	/**
	 * Attempts to parse the given value as a constant; if possible, an
	 * Expression with that constant value is returned, otherwise, a variable
	 * expression is returned for the given String.
	 */
	public static Expression of(String value) {
		try {
			return of(Integer.parseInt(value));
		} catch (NumberFormatException ignore) {
			return new VariableExpression(value);
		}
	}

	private Expression() {}

	/**
	 * Returns whether this Expression has a constant value.
	 */
	public abstract boolean isConstant();

	/**
	 * If {@link #isConstant} returns {@code true}, returns the constant value
	 * of this {@code Expression}.
	 * 
	 * @throws IllegalStateException
	 *             if the instance is variable ({@link #isConstant} returns
	 *             {@code false})
	 */
	public abstract int value();

	/**
	 * Returns the {@link String} representation of this {@code Expression}.
	 */
	@Override public abstract String toString();

	/**
	 * Indicates whether some other object is equal to this {@code Expression}.
	 */
	@Override public abstract boolean equals(Object obj);

	/**
	 * An {@code Expression}'s hash code is equal to its constant value if it is
	 * constant; otherwise it is the hash code of the {@link String} that it
	 * represents.
	 */
	@Override public abstract int hashCode();

	private static class ConstantExpression extends Expression {

		private final int value;

		private ConstantExpression(int value) {
			this.value = value;
		}

		@Override public int value() {
			return value;
		}

		@Override public String toString() {
			return Integer.toString(value);
		}

		@Override public boolean isConstant() {
			return true;
		}

		@Override public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			return obj instanceof ConstantExpression
					&& ((ConstantExpression) obj).value == this.value;
		}

		@Override public int hashCode() {
			return value;
		}

		@Override public int compareTo(Expression o) {
			return o.isConstant()
					? Integer.compare(value(), o.value())
					: -1;
		}
	}

	private static class VariableExpression extends Expression {

		private final String value;

		private VariableExpression(String value) {
			this.value = value;
		}

		@Override public int value() {
			throw new IllegalStateException(String.format("\"%s\" has no constant value", this));
		}

		@Override public String toString() {
			return value;
		}

		@Override public boolean isConstant() {
			return false;
		}

		@Override public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			return obj instanceof VariableExpression
					&& ((VariableExpression) obj).value.equals(this.value);
		}

		@Override public int hashCode() {
			return value.hashCode();
		}

		@Override public int compareTo(Expression o) {
			return o.isConstant()
					? 1
					: toString().compareTo(o.toString());
		}
	}

	public static boolean gt(@Nullable Expression expression, int value) {
		return nonnullAndConstant(expression) && expression.value() > value;
	}

	public static boolean lt(@Nullable Expression expression, int value) {
		return nonnullAndConstant(expression) && expression.value() < value;
	}

	public static boolean eq(@Nullable Expression expression, int value) {
		return nonnullAndConstant(expression) && expression.value() == value;
	}

	private static boolean nonnullAndConstant(@Nullable Expression expression) {
		return expression != null && expression.isConstant();
	}

	public static boolean inRange(@Nullable Expression expression, int min, int max) {
		if (!nonnullAndConstant(expression)) {
			return false;
		}
		int value = expression.value();
		return min >= value && value >= max;
	}

	public static boolean variable(@Nullable Expression expression) {
		return expression != null && !expression.isConstant();
	}

}
