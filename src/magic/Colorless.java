package magic;

import java.util.Set;

import magic.Symbol.Primitive;

public class Colorless extends Primitive {

	private static final Colorless[] CACHE = new Colorless[17];

	static {
		for (int i = 0; i < CACHE.length; i++) {
			CACHE[i] = new Colorless(i);
		}
	}

	public static Colorless of(int amount) {
		if (amount < 0) {
			throw new IllegalArgumentException("amount < 0");
		}
		if (amount < CACHE.length) {
			return CACHE[amount];
		}
		// should cause warning
		return new Colorless(amount);
	}

	private int value;

	private Colorless(int value) {
		super(value);
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

	static Colorless parseInner(String input) {
		try {
			return Colorless.of(Integer.parseInt(input));
		} catch (NumberFormatException e) {
			return null;
		}
	}

}
