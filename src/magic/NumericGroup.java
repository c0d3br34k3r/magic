package magic;

import java.util.Set;

import magic.Symbol.Numeric;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;

public class NumericGroup extends Element implements Multiset.Entry<Numeric> {

	private final int value;
	
	public static NumericGroup of(int value) {
		return new NumericGroup(value);
	}
	
	private NumericGroup(int value) {
		super(ImmutableSet.<Color> of(), value, Integer.toString(value));
		this.value = value;
	}
	
	@Override public Numeric getElement() {
		return Symbol.COLORLESS;
	}

	@Override public int getCount() {
		return value;
	}

	@Override public boolean payableWith(Set<Color> mana) {
		return true;
	}
	
}
