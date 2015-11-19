package magic;

import java.util.List;
import java.util.Objects;

import org.joda.time.LocalDate;

import com.google.common.collect.ImmutableListMultimap;

/**
 * An object containing the attributes of expansion. It does not, however,
 * contain the cards within the expansion. Implementations of {@code Expansion}
 * should be immutable.
 * <p>
 * Because each instance of {@code Expansion} should be unique, it may be useful
 * to have {@code equals} and {@code hashCode} default to their identity-based
 * implementations in {@link Object}.
 */
public final class Expansion implements Comparable<Expansion> {

	private final String name;
	private final String code;
	private final ImmutableListMultimap<WholeCard, WholePrinting> printings;
	private final LocalDate releaseDate;
	private final ReleaseType type;
	private final BorderColor borderColor;
	private final boolean hasCollectorNumbers;
	private final boolean isPhysical;
	private final boolean hasBooster;

	private Expansion(Builder builder) {
		this.name = builder.name;
		this.code = builder.code;

		this.releaseDate = builder.releaseDate;
		this.type = builder.type;
		this.borderColor = builder.borderColor;
		this.hasCollectorNumbers = builder.hasCollectorNumbers;
		this.isPhysical = builder.isPhysical;
		this.hasBooster = builder.hasBooster;

		ImmutableListMultimap.Builder<WholeCard, WholePrinting> printingsBuilder =
				ImmutableListMultimap.builder();
		for (WholePrinting.Builder printing : builder.printings) {
			printing.setExpansion(this);
			WholePrinting wholePrinting = printing.build();
			printingsBuilder.put(wholePrinting.card(), wholePrinting);
		}
		this.printings = printingsBuilder.build();
	}

	public String name() {
		return name;
	}

	public ImmutableListMultimap<WholeCard, WholePrinting> printings() {
		return printings;
	}

	public List<WholePrinting> printingsOf(WholeCard wholeCard) {
		return printings.get(wholeCard);
	}

	public String code() {
		return code;
	}

	public LocalDate releaseDate() {
		return releaseDate;
	}

	public ReleaseType type() {
		return type;
	}

	public BorderColor borderColor() {
		return borderColor;
	}

	public boolean hasCollectorNumbers() {
		return hasCollectorNumbers;
	}

	public boolean isPhysical() {
		return isPhysical;
	}

	public boolean hasBooster() {
		return hasBooster;
	}

	/**
	 * Returns this expansion's name.
	 */
	@Override public String toString() {
		return name();
	}

	/**
	 * Provides a natural ordering for {@code Expansion}s based on their release
	 * date.
	 */
	@Override public int compareTo(Expansion o) {
		int result = releaseDate().compareTo(o.releaseDate());
		if (result == 0) {
			result = name().compareToIgnoreCase(o.name());
		}
		return result;
	}

	public static Builder builder() {
		return new Builder();
	}

	public enum BorderColor {
		BLACK,
		WHITE;
	}

	public enum ReleaseType {

		CORE_SET,
		EXPANSION,
		PROMOTIONAL,
		DUEL_DECKS,
		PORTAL,
		STARTER,
		MASTERS_EDITION,
		FROM_THE_VAULT,
		PREMIUM_DECK_SERIES,
		COMMANDER,
		PLANECHASE,
		OTHER;
	}

	public static class Builder {

		private String name;
		private Iterable<WholePrinting.Builder> printings;
		private String code;
		private LocalDate releaseDate;
		private ReleaseType type;
		private BorderColor borderColor;
		private boolean hasCollectorNumbers;
		private boolean isPhysical;
		private boolean hasBooster;

		private Builder() {}

		public Builder setName(String name) {
			this.name = name;
			return this;
		}

		public Builder setPrintings(Iterable<WholePrinting.Builder> list) {
			this.printings = Objects.requireNonNull(list);
			return this;
		}

		public Builder setCode(String code) {
			this.code = Objects.requireNonNull(code);
			return this;
		}

		public Builder setReleaseDate(LocalDate releaseDate) {
			this.releaseDate = Objects.requireNonNull(releaseDate);
			return this;
		}

		public Builder setType(ReleaseType type) {
			this.type = Objects.requireNonNull(type);
			return this;
		}

		public Builder setBorderColor(BorderColor borderColor) {
			this.borderColor = Objects.requireNonNull(borderColor);
			return this;
		}

		public Builder setHasCollectorNumbers(boolean hasCollectorNumbers) {
			this.hasCollectorNumbers = hasCollectorNumbers;
			return this;
		}

		public Builder setPhysical(boolean isPhysical) {
			this.isPhysical = isPhysical;
			return this;
		}

		public Builder setHasBooster(boolean hasBooster) {
			this.hasBooster = hasBooster;
			return this;
		}

	}

}
