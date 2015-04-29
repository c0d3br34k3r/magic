package magic;

import com.google.common.collect.ImmutableBiMap;

/**
 * A utility class for Subtypes, which are represented as {@code Strings}.
 * 
 * @see Card
 */
public final class Subtype {

	/**
	 * An {@link ImmutableBiMap} of all Basic Land types, mapped to the
	 * {@link Color} they produce. As a BiMap, it can be reversed to map
	 * {@link Color}s to Basic Land types.
	 */
	public static final ImmutableBiMap<String, Color> BASIC_LAND_TYPES =
			ImmutableBiMap.<String, Color> builder()
					.put("Plains", Color.WHITE)
					.put("Island", Color.BLUE)
					.put("Swamp", Color.BLACK)
					.put("Mountain", Color.RED)
					.put("Forest", Color.GREEN).build();

	private Subtype() {}
}
