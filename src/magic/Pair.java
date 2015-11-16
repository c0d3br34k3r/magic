package magic;

abstract class Pair<P extends Partial<P>> {

	private final P first;
	private final P second;

	Pair(P first, P second) {
		this.first = first;
		this.second = second;
	}

	public final P first() {
		return first;
	}

	public final P second() {
		return second;
	}

}
