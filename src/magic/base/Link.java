package magic.base;

public class Link<T> {

	private final T linked;
	private final boolean isFirstHalf;

	public Link(T linked, boolean isFirstHalf) {
		this.linked = linked;
		this.isFirstHalf = isFirstHalf;
	}

	public T get() {
		return linked;
	}

	public boolean isFirstHalf() {
		return isFirstHalf;
	}

	@Override public String toString() {
		return (isFirstHalf ? "first" : "second") + " half; other half is "
				+ linked;
	}

}
