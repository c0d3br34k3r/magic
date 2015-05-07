package magic;

import java.util.Set;

public class Numeric extends Symbol {

	private static final Numeric[] CACHE = new Numeric[17];

	static {
		for (int i = 0; i < CACHE.length; i++) {
			CACHE[i] = new Numeric(i);
		}
	}

	public static Numeric of(int amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("amount < 0");
		}
		if (amount < CACHE.length) {
			return CACHE[amount];
		}
		// should cause warning
		return new Numeric(amount);
	}

	private int value;

	private Numeric(int value) {
		super(value, String.format("{%d}", value));
		this.value = value;
	}

	public int value() {
		return value;
	}

	@Override public boolean payableWith(Set<Color> mana) {
		return true;
	}

	@Override public void accept(Visitor visitor) {
		visitor.visit(this);
	}
	
	@Override public int hashCode() {
		return value;
	}

	@Override public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		return obj instanceof Numeric && ((Numeric) obj).value == this.value;
	}

	static Numeric parseInner(String input) {
		try {
			return Numeric.of(Integer.parseInt(input));
		} catch (NumberFormatException e) {
			return null;
		}
	}

}
