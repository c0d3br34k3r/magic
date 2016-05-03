package magic.misc;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;

/**
 * A standard implementation of the {@link SetInterner}.
 */
public class RegularSetInterner<E> implements SetInterner<E> {

	private Map<Set<E>, ImmutableSet<E>> data = new MapMaker().makeMap();

	@Override public ImmutableSet<E> intern(Set<E> sample) {
		ImmutableSet<E> result = data.get(sample);
		if (result == null) {
			result = ImmutableSet.copyOf(sample);
			data.put(result, result);
		}
		return result;
	}

}
