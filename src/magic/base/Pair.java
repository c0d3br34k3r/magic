package magic.base;

public abstract class Pair<T> {

	private final T first;
	private final T second;

	Pair(T first, T second) {
		this.first = first;
		this.second = second;
	}

	public final T first() {
		return first;
	}

	public final T second() {
		return second;
	}

}
