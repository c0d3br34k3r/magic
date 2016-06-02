package magic;

import java.io.IOException;
import java.util.Objects;

import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.collect.ComparisonChain;

public final class Printing implements Comparable<Printing> {

	private final Characteristics card;
	private final WholePrinting whole;
	private final @Nullable PrintingLink link;
	private final String flavorText;
	private final @Nullable CollectorNumber collectorNumber;
	private final String artist;
	private final @Nullable String watermark;

	private Printing(Builder builder) {
		this.card = builder.card;
		this.link = builder.buildLink(this);
		this.whole = builder.getWhole();
		this.flavorText = builder.flavorText;
		this.collectorNumber = builder.collectorNumber;
		this.artist = builder.artist;
		this.watermark = builder.watermark;
	}

	public Characteristics card() {
		return card;
	}

	public WholePrinting whole() {
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

	public String watermark() {
		return watermark;
	}

	public PrintingLink link() {
		return link;
	}

	@Override public int compareTo(Printing o) {
		ComparisonChain chain = ComparisonChain.start()
				.compare(card, o.card)
				.compare(whole.expansion(), o.whole.expansion());
		if (o.collectorNumber != null) {
			chain = chain.compare(collectorNumber, o.collectorNumber);
		}
		return chain.compare(whole.variation(), o.whole.variation()).result();
	}

	@Override public String toString() {
		return card.name() + " (" + whole.expansion().code() + ":" + whole.rarity().code() + ")";
	}

	private static final Joiner SPACE_JOINER = Joiner.on(' ');

	public void writeTo(Appendable out) throws IOException {
		String newline = System.lineSeparator();
		out.append(card.name());
		if (!card.manaCost().isEmpty()) {
			out.append(' ').append(card.manaCost().toString());
		}
		out.append(newline);
		if (card.colorIndicator() != null) {
			out.append('(');
			for (Color color : card.colorIndicator()) {
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
		out.append(" (").append(whole.expansion().code()).append(':')
				.append(whole.rarity().code()).append(')');
		out.append(newline);
		if (!card.text().isEmpty()) {
			out.append(card.text()).append(newline);
		}
		if (!flavorText.isEmpty()) {
			out.append(flavorText).append(newline);
		}
		if (card.power() != null) {
			out.append(card.power().toString())
					.append('/')
					.append(card.toughness().toString())
					.append(newline);
		} else if (card.loyalty() != null) {
			out.append(Integer.toString(card.loyalty())).append(newline);
		}
		if (collectorNumber != null) {
			// TODO: fix this
			out.append(collectorNumber.toString()).append('/')
					.append(String.valueOf(whole.expansion().size())).append(' ');
		}
		out.append("Illus. ").append(artist);
		out.append(newline);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends PartialBuilder<Printing, WholePrinting, PrintingLink> {

		private Characteristics card;
		private String flavorText = "";
		private CollectorNumber collectorNumber = null;
		private String artist;
		private String watermark = null;

		private Builder() {}

		void setCard(Characteristics card) {
			this.card = Objects.requireNonNull(card);
		}

		public Builder setFlavorText(String flavorText) {
			this.flavorText = Objects.requireNonNull(flavorText);
			return this;
		}

		public Builder setCollectorNumber(CollectorNumber collectorNumber) {
			this.collectorNumber = collectorNumber;
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

		@Override PrintingLink newLink(Printing partial, int index) {
			return new PrintingLink(partial, index);
		}
	}

}
