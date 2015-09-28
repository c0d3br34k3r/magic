package magic;

/**
 * All two-part cards have a {@code Link} that contains additional information
 * about the linking card, and provides access to linked card. Implementations
 * of {@code Link} should be immutable.
 */
public interface Link {

	/**
	 * The other half of the linking card. The {@code Link} on the linked card
	 * is guaranteed to be nonnull.
	 */
	Card get();

	/**
	 * The layout, or type, of the two-part card that the linked card and
	 * the linking card make up.
	 */
	Layout layout();

	/**
	 * Returns {@code true} if the linking card is the "first" of the two
	 * halves.
	 */
	boolean isFirstHalf();

	/**
	 * Returns the appropriate name (either the {@link Layout#firstHalfName()}
	 * or the {@link Layout#secondHalfName()}) describing the physical location
	 * of the linked card.
	 */
	String location();
	
	public enum Layout {

		/**
		 * Used for split cards. The split card halves are called "left" and
		 * "right".
		 */
		SPLIT("Split", "left", "right") {
			@Override public String formatNames(String firstName, String secondName) {
				return firstName + " & " + secondName;
			}
		},
		
		/**
		 * Used for flip cards. The flip card halves are called "top" and "bottom".
		 */
		FLIP("Flip", "top", "bottom") {
			@Override public String formatNames(String firstName, String secondName) {
				return firstName;
			}
		},
		
		/**
		 * Used for double-faced cards. The double-faced card halves are called
		 * "front" and "back".
		 */
		DOUBLE_FACED("Double-faced", "front", "back") {
			@Override public String formatNames(String firstName, String secondName) {
				return firstName;
			}
		};

		private final String name;
		private final String firstHalfName;
		private final String secondHalfName;

		private Layout(String name, String firstHalfName, String secondHalfName) {
			this.name = name;
			this.firstHalfName = firstHalfName;
			this.secondHalfName = secondHalfName;
		}

		/**
		 * Returns a title-case representation of this layout.
		 */
		@Override public String toString() {
			return name;
		}

		/**
		 * The description of the physical location of the first half on the
		 * two-part card. Example: on {@code Split} cards, this returns
		 * {@code "left"}.
		 */
		public String firstHalfName() {
			return firstHalfName;
		}

		/**
		 * The description of physical location of the second half on the two-part
		 * card. Example: on {@code Split} cards, this returns {@code "right"}.
		 */
		public String secondHalfName() {
			return secondHalfName;
		}

		public abstract String formatNames(String firstName, String secondName);
	}

}
