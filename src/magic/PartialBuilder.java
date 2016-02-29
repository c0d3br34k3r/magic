package magic;

import javax.annotation.Nullable;

abstract class PartialBuilder<P extends Partial<P>, W extends Whole<P>> {

	private W whole;
	// only the first half will set this
	private @Nullable PartialBuilder<P, W> linked;
	// each linked builder sets this field for the other
	private @Nullable P other;

	abstract P build();

	void setWhole(W whole) {
		this.whole = whole;
	}

	void prepareLink(PartialBuilder<P, W> linked) {
		this.linked = linked;
	}

	W getWhole() {
		return whole;
	}

	P getOther() {
		return other;
	}

	Link<P> buildLink(P partiallyInitialized) {
		if (linked != null) {
			linked.other = partiallyInitialized;
			other = linked.build();
			return new Link<P>(other, true);
		}
		if (other != null) {
			return new Link<P>(other, false);
		}
		return null;
	}

}
