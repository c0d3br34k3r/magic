package magic;

import java.util.Set;

import magic.Symbol.Generic;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;

public class NumericGroup extends ManaUnit implements Multiset.Entry<Generic> {

	private final int value;
	
	public static NumericGroup of(int value) {
		return new NumericGroup(value);
	}
	
	private NumericGroup(int value) {
		super(ImmutableSet.<Color> of(), value, Integer.toString(value));
		this.value = value;
	}
	
	@Override public Generic getElement() {
		return Symbol.GENERIC;
	}

	@Override public int getCount() {
		return value;
	}

	@Override public boolean payableWith(Set<Color> mana) {
		return true;
	}
	
	@Override public int hashCode() {
		return value;
	}

	@Override public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		return obj instanceof NumericGroup
				&& ((NumericGroup) obj).value == this.value;
	}

	static final NumericGroup TWO = NumericGroup.of(2);
	
}
