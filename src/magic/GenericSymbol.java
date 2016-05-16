package magic;

public class GenericSymbol implements Symbol {

	private final int amount;

	public GenericSymbol(int amount) {
		if (amount < 0) {
			throw new IllegalArgumentException();
		}
		this.amount = amount;
	}

	public int amount() {
		return amount;
	}

	@Override public String toString() {
		return '{' + Integer.toString(amount) + '}';
	}

	@Override public boolean equals(Object obj) {
		return obj instanceof GenericSymbol && amount == ((GenericSymbol) obj).amount;
	}

	@Override public int hashCode() {
		return amount;
	}

}
