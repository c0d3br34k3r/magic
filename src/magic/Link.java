package magic;

abstract class Link<P extends Partial<P>> {

	private final P linked;
	private final boolean isFirstHalf;

	Link(P linked, boolean isFirstHalf) {
		this.linked = linked;
		this.isFirstHalf = isFirstHalf;
	}

	public P get() {
		return linked;
	}

	public boolean isFirstHalf() {
		return isFirstHalf;
	}
	
	public int index() {
		return isFirstHalf ? 0 : 1;
	}

	@Override public String toString() {
		return (isFirstHalf ? "first" : "second") + " half; other half is "
				+ linked;
	}

}
