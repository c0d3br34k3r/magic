package magic.misc;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;

/**
 * A special {@link SetInterner} that uses EnumSets for efficiency.
 * 
 * @param <E>
 *            the type of enum in the set
 */
public class EnumSetInterner<E extends Enum<E>> implements SetInterner<E> {

	private final Map<Set<E>, ImmutableSet<E>> data = new MapMaker().makeMap();

	public EnumSetInterner() {}

	@Override public ImmutableSet<E> intern(Set<E> sample) {
		ImmutableSet<E> result = data.get(sample);
		if (result == null) {
			result = Sets.immutableEnumSet(sample);
			data.put(result, result);
		}
		return result;
	}

}
