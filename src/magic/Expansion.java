package magic;

import java.util.List;
import java.util.Objects;

import org.joda.time.LocalDate;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Lists;

/**
 * TODO
 */
public final class Expansion implements Comparable<Expansion> {

	private final String name;
	private final String code;
	private final LocalDate releaseDate;
	private final ReleaseType type;
	private final BorderColor borderColor;
	private final boolean hasCollectorNumbers;
	private final boolean onlineOnly;
	private final boolean hasBooster;
	private final ImmutableListMultimap<WholeCard, WholePrinting> printings;

	private Expansion(Builder builder) {
		this.name = Objects.requireNonNull(builder.name);
		this.code = Objects.requireNonNull(builder.code);
		this.releaseDate = Objects.requireNonNull(builder.releaseDate);
		this.type = Objects.requireNonNull(builder.type);
		this.borderColor = Objects.requireNonNull(builder.borderColor);
		this.hasCollectorNumbers = builder.hasCollectorNumbers;
		this.onlineOnly = builder.onlineOnly;
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

	public List<Printing> printingsOf(Card card) {
		return Lists.transform(printings.get(card.whole()), transform(card));
	}

	private static Function<? super WholePrinting, ? extends Printing> transform(
			Card card) {
		if (card.link() == null) {
			return TRANSFORM_ONLY;
		}
		if (card.link().isFirst()) {
			return TRANSFORM_FIRST;
		}
		return TRANSFORM_SECOND;
	}

	private static final Function<? super WholePrinting, ? extends Printing> TRANSFORM_ONLY =
			new Function<WholePrinting, Printing>() {
				@Override public Printing apply(WholePrinting input) {
					return input.only();
				}
			};

	private static final Function<? super WholePrinting, ? extends Printing> TRANSFORM_FIRST = transformPart(0);

	private static final Function<? super WholePrinting, ? extends Printing> TRANSFORM_SECOND = transformPart(1);

	private static final Function<? super WholePrinting, ? extends Printing> transformPart(final int index) {
		return new Function<WholePrinting, Printing>() {
			@Override public Printing apply(WholePrinting input) {
				return input.pair().get(index);
			}
		};
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

	public boolean onlineOnly() {
		return onlineOnly;
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
		MASTERS,
		FROM_THE_VAULT,
		PREMIUM_DECK_SERIES,
		COMMANDER,
		PLANECHASE,
		CONSPIRACY,
		OTHER;
	}

	public static class Builder {

		private String name;
		private Iterable<WholePrinting.Builder> printings;
		private String code;
		private LocalDate releaseDate;
		private ReleaseType type;
		private BorderColor borderColor;
		private boolean hasCollectorNumbers = false;
		private boolean onlineOnly = false;
		private boolean hasBooster = false;

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

		public Builder setOnlineOnly(boolean onlineOnly) {
			this.onlineOnly = onlineOnly;
			return this;
		}

		public Builder setHasBooster(boolean hasBooster) {
			this.hasBooster = hasBooster;
			return this;
		}

		public Expansion build() {
			return new Expansion(this);
		}

	}

}
