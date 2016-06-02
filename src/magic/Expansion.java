package magic;

import java.util.Objects;

import javax.annotation.Nullable;

import org.joda.time.LocalDate;

/**
 * TODO
 */
public final class Expansion implements Comparable<Expansion> {

	private final String identifier;
	private final String code;
	private final LocalDate releaseDate;
	private final ReleaseType type;
	private final BorderColor borderColor;
	private final @Nullable Integer size;
	private final int level;

	private Expansion(Builder builder) {
		this.identifier = builder.identifier;
		this.code = Objects.requireNonNull(builder.code);
		this.releaseDate = Objects.requireNonNull(builder.releaseDate);
		this.type = Objects.requireNonNull(builder.type);
		this.borderColor = Objects.requireNonNull(builder.borderColor);
		this.size = builder.size;
		this.level = builder.level;
	}

	public @Nullable String name() {
		return type.format(this);
	}

	public @Nullable String identifier() {
		return identifier;
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
	
	public int level() {
		return level;
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
		EDITION(true, true),
		CORE_SET(true, true) {
			
			@Override String format(Expansion expansion) {
				return "Magic " + (expansion.releaseDate.getYear() + 1);
			}
		},
		EXPANSION(true, true),
		WELCOME_DECK(true, false) {

			@Override String format(Expansion expansion) {
				return "Welcome Deck " + expansion.releaseDate.getYear();
			}
		},

		STARTER(true, false),
		PROMOTIONAL(false, false),

		ONLINE(true, false),

		DUEL_DECKS(false, false) {

			@Override String format(Expansion expansion) {
				return "Duel Decks: " + expansion.identifier;
			}
		},
		FROM_THE_VAULT(false, false) {

			@Override String format(Expansion expansion) {
				return "From the Vault: " + expansion.identifier;
			}
		},
		PLANECHASE(false, false),
		PREMIUM_DECK(false, false) {

			@Override String format(Expansion expansion) {
				return "Premium Deck Series: " + expansion.identifier;
			}
		},
		ARCHENEMY(false, false),
		COMMANDER(false, false),
		MODERN_MASTERS(true, false),
		ETERNAL_MASTERS(true, false),
		MODERN_EVENT_DECK(false, false),
		CONSPIRACY(true, false),

		OTHER_BOX(false, false);

		private boolean hasBooster;
		private boolean effectsStandard;

		ReleaseType(boolean hasBooster, boolean effectsStandard) {
			this.hasBooster = hasBooster;
			this.effectsStandard = effectsStandard;
		}

		String format(Expansion expansion) {
			return expansion.identifier;
		}

		public final boolean hasBooster() {
			return hasBooster;
		}

		public final boolean effectsStandard() {
			return effectsStandard;
		}
	}

	public static class Builder {

		private int level = 0;
		private String identifier;
		private String code;
		private LocalDate releaseDate;
		private ReleaseType type;
		private BorderColor borderColor;
		public Integer size;

		private Builder() {}

		public void setIdentifier(String identifier) {
			this.identifier = identifier;
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
		
		public void setLevel(int size) {
			this.level = size;
		}

		public Expansion build() {
			return new Expansion(this);
		}
	}

}
