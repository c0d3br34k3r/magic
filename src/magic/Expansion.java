package magic;

import java.util.Objects;

import javax.annotation.Nullable;

import org.joda.time.LocalDate;

/**
 * TODO
 */
public final class Expansion implements Comparable<Expansion> {

	private final String name;
	private final String code;
	private final LocalDate releaseDate;
	private final ReleaseType type;
	private final BorderColor borderColor;
	private final @Nullable Integer size;

	private Expansion(Builder builder) {
		this.name = Objects.requireNonNull(builder.name);
		this.code = Objects.requireNonNull(builder.code);
		this.releaseDate = Objects.requireNonNull(builder.releaseDate);
		this.type = Objects.requireNonNull(builder.type);
		this.borderColor = Objects.requireNonNull(builder.borderColor);
		this.size = builder.size;
	}

	public String name() {
		return name;
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

	public @Nullable Integer size() {
		return size;
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
		CORE_SET(true),
		EXPANSION(true),
		WELCOME_DECK,

		STARTER(true),
		PROMOTIONAL,

		ONLINE(true),

		DUEL_DECKS,
		FROM_THE_VAULT,
		PLANECHASE,
		PREMIUM_DECK,
		ARCHENEMY,
		COMMANDER,
		MODERN_MASTERS(true),
		MODERN_EVENT_DECK,
		CONSPIRACY(true),

		OTHER_BOX;

		private boolean hasBooster;

		ReleaseType(boolean hasBooster) {
			this.hasBooster = hasBooster;
		}

		ReleaseType() {
			this(false);
		}

		public boolean hasBooster() {
			return hasBooster;
		}
	}

	public static class Builder {

		private String name;
		private String code;
		private LocalDate releaseDate;
		private ReleaseType type;
		private BorderColor borderColor;
		public Integer size;

		private Builder() {}

		public void setName(String name) {
			this.name = name;
		}

		public void setCode(String code) {
			this.code = Objects.requireNonNull(code);
		}

		public void setReleaseDate(LocalDate releaseDate) {
			this.releaseDate = Objects.requireNonNull(releaseDate);
		}

		public void setType(ReleaseType type) {
			this.type = Objects.requireNonNull(type);
		}

		public void setBorderColor(BorderColor borderColor) {
			this.borderColor = Objects.requireNonNull(borderColor);
		}

		public void setSize(@Nullable Integer size) {
			this.size = size;
		}

		public Expansion build() {
			return new Expansion(this);
		}
	}

}
