package magic;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public abstract class ManaUnit {

	private final ImmutableSet<Color> colors;
	private final int converted;
	private final String innerPart;

	ManaUnit(ImmutableSet<Color> colors, int converted, String innerPart) {
		this.colors = colors;
		this.converted = converted;
		this.innerPart = innerPart;
	}

	ManaUnit(Color color, int converted, String innerPart) {
		this(ImmutableSet.of(color), converted, innerPart);
	}

	ManaUnit(int converted, String innerPart) {
		this(ImmutableSet.<Color> of(), converted, innerPart);
	}

	public abstract boolean payableWith(Set<Color> mana);

	@Override public final String toString() {
		return '{' + innerPart + '}';
	}

	public final int converted() {
		return converted;
	}

	public final ImmutableSet<Color> colors() {
		return colors;
	}
	
	final String innerPart() {
		return innerPart;
	}
	
}
