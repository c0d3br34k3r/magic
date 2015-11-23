package magic;

import java.io.IOException;
import java.util.Objects;

import javax.annotation.Nullable;

import com.google.common.base.Joiner;

public final class Printing extends Partial<Printing> {

	private final Card card;
	private final WholePrinting whole;
	private final @Nullable Link<Printing> link;
	private final String flavorText;
	private final @Nullable CollectorNumber collectorNumber;
	private final int variation;
	private final String artist;
	private final @Nullable String watermark;

	private Printing(Builder builder) {
		this.card = builder.card;

		this.link = builder.buildLink(this);
		this.whole = builder.getWhole();

		this.flavorText = builder.flavorText;
		this.collectorNumber = builder.collectorNumber;
		this.variation = builder.variation;
		this.artist = builder.artist;
		this.watermark = builder.watermark;
	}

	public Card card() {
		return card;
	}

	@Override public WholePrinting whole() {
		return whole;
	}

	public String flavorText() {
		return flavorText;
	}

	public String artist() {
		return artist;
	}

	public CollectorNumber collectorNumber() {
		return collectorNumber;
	}

	public int variationIndex() {
		return variation;
	}

	public String watermark() {
		return watermark;
	}

	@Override public Link<Printing> link() {
		return link;
	}

	@Override public String toString() {
		StringBuilder builder = new StringBuilder(card.name());
		if (collectorNumber != null) {
			builder.append('#').append(collectorNumber.toString());
		} else {
			
		}
		return builder.toString();
	}

	private static final Joiner SPACE_JOINER = Joiner.on(' ');

	public void writeTo(Appendable out) throws IOException {
		String newline = System.lineSeparator();
		out.append(card.name());
		if (!card.manaCost().isEmpty()) {
			out.append(' ').append(card.manaCost().toString());
		}
		out.append(newline);
		if (card.colorOverride() != null && !card.colorOverride().isEmpty()) {
			out.append('(');
			for (Color color : card.colorOverride()) {
				out.append(color.code());
			}
			out.append(") ");
		}
		if (!card.supertypes().isEmpty()) {
			SPACE_JOINER.appendTo(out, card.supertypes()).append(' ');
		}
		SPACE_JOINER.appendTo(out, card.types());
		if (!card.subtypes().isEmpty()) {
			out.append(" - ");
			SPACE_JOINER.appendTo(out, card.subtypes());
		}
		out.append(whole.expansion().code()).append(':')
				.append(whole.rarity().code());
		out.append(newline);
		if (!card.text().isEmpty()) {
			out.append(card.text()).append(newline);
		}
		if (!flavorText.isEmpty()) {
			out.append('/').append(flavorText).append('/').append(newline);
		}
		if (card.power() != null) {
			out.append(card.power().toString()).append('/')
					.append(card.toughness().toString()).append(newline);
		} else if (card.loyalty() != null) {
			out.append(Integer.toString(card.loyalty())).append(newline);
		}
		out.append(newline);
		out.append("Illus. ").append(artist);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder
			extends magic.PartialBuilder<Printing, WholePrinting> {

		private Card card;
		private String flavorText = "";
		private CollectorNumber collectorNumber = null;
		private int variation;
		private String artist;
		private String watermark = null;

		private Builder() {}

		public Builder setCard(Card card) {
			this.card = Objects.requireNonNull(card);
			return this;
		}

		public Builder setFlavorText(String flavorText) {
			this.flavorText = Objects.requireNonNull(flavorText);
			return this;
		}

		public Builder setCollectorNumber(CollectorNumber collectorNumber) {
			this.collectorNumber = collectorNumber;
			return this;
		}

		public Builder setVariation(int variation) {
			this.variation = variation;
			return this;
		}

		public Builder setArtist(String artist) {
			this.artist = Objects.requireNonNull(artist);
			return this;
		}

		public Builder setWatermark(String watermark) {
			this.watermark = watermark;
			return this;
		}

		@Override Printing build() {
			return new Printing(this);
		}
	}

}
