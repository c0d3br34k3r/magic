package magic;

import java.util.Iterator;

import com.google.common.collect.ImmutableList;

abstract class Pair<P extends Partial<P>> {

	private ImmutableList<P> values;

	Pair(P first, P second) {
		this.values = ImmutableList.of(first, second);
	}
	
	Pair(ImmutableList<P> values) {
		this.values = values;
	}

	public final P first() {
		return get(0);
	}

	public final P second() {
		return get(1);
	}
	
	public final P get(int firstOrSecond) {
		return values.get(firstOrSecond);
	}
	
	Iterator<P> iterator() {
		return values.iterator();
	}

}
