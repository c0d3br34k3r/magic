package magic;

public enum Layout {

	/**
	 * Used for split cards. The split card halves are called "left" and
	 * "right".
	 */
	SPLIT("Split", "left", "right") {
		@Override public String formatNames(String firstName,
				String secondName,
				String splitCardSeparator) {
			return firstName + splitCardSeparator + secondName;
		}
	},

	/**
	 * Used for flip cards. The flip card halves are called "top" and "bottom".
	 */
	FLIP("Flip", "top", "bottom") {
		@Override public String formatNames(String firstName,
				String secondName,
				String splitCardSeparator) {
			return firstName;
		}
	},

	/**
	 * Used for double-faced cards. The double-faced card halves are called
	 * "front" and "back".
	 */
	DOUBLE_FACED("Double-faced", "front", "back") {
		@Override public String formatNames(String firstName,
				String secondName,
				String splitCardSeparator) {
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

	public abstract String formatNames(String firstName, String secondName,
			String splitCardSeparator);

	public String formatNames(String firstName, String secondName) {
		return formatNames(firstName, secondName, " // ");
	}
}
