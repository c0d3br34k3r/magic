package magic.misc;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Interner;

/**
 * An {@link Interner} that interns {@link Set}s. Because Interners must be used with
 * immutable types, an {@link ImmutableSet} will be returned, but the samples do
 * not need to be immutable.
 * 
 * @param <E>
 *            The type of element in the set
 */
public interface SetInterner<E> extends Interner<Set<E>> {

	@Override ImmutableSet<E> intern(Set<E> sample);

}
