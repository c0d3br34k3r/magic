package magic;

abstract class Link<P> {

	private final P linked;
	private final int index;

	Link(P linked, int index) {
		this.linked = linked;
		this.index = index;
	}

	public P get() {
		return linked;
	}

	public boolean isFirst() {
		return index == 0;
	}

	public int index() {
		return index;
	}

	@Override public String toString() {
		return (isFirst() ? "first" : "second")
				+ " half; other half is "
				+ linked;
	}

}
