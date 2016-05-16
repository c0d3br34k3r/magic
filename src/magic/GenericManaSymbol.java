package magic;

public class GenericManaSymbol implements Symbol {

	public static GenericManaSymbol of(int amount) {
		if (amount < 0) {
			throw new IllegalArgumentException();
		}
		return new GenericManaSymbol(amount);
	}

	private final int amount;

	private GenericManaSymbol(int amount) {
		this.amount = amount;
	}

	public int amount() {
		return amount;
	}

	@Override public String toString() {
		return '{' + Integer.toString(amount) + '}';
	}

	@Override public boolean equals(Object obj) {
		return obj instanceof GenericManaSymbol && amount == ((GenericManaSymbol) obj).amount;
	}

	@Override public int hashCode() {
		return amount;
	}

}
