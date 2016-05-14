package magic;

import javax.annotation.Nullable;

abstract class PartialBuilder<P extends Partial<P>, W extends Whole<P>, L extends Link<P>> {

	private W whole;
	// only the first half will set this
	private @Nullable PartialBuilder<P, W, L> linked;
	// each linked builder sets this field for the other
	private @Nullable P other;

	abstract P build();

	void setWhole(W whole) {
		this.whole = whole;
	}

	void prepareLink(PartialBuilder<P, W, L> linked) {
		this.linked = linked;
	}

	W getWhole() {
		return whole;
	}

	P getOther() {
		return other;
	}

	L buildLink(P partiallyInitialized) {
		if (linked != null) {
			linked.other = partiallyInitialized;
			other = linked.build();
			return newLink(other, true);
		}
		if (other != null) {
			return newLink(other, false);
		}
		return null;
	}

	abstract L newLink(P partial, boolean isFirstHalf);

}
