package magic;

import java.io.IOException;
import java.io.PrintStream;

import javax.annotation.Nullable;

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

	public void writeTo(PrintStream out) throws IOException {

	}

	public static final class Builder extends magic.PartialBuilder<Printing, WholePrinting> {

		private Card card;
		private String flavorText = "";
		private CollectorNumber collectorNumber = null;
		private int variation;
		private String artist;
		private String watermark = null;

		public Builder setCard(Card card) {
			this.card = card;
			return this;
		}

		public Builder setFlavorText(String flavorText) {
			this.flavorText = flavorText;
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
			this.artist = artist;
			return this;
		}

		public Builder setWatermark(String watermark) {
			this.watermark = watermark;
			return this;
		}

		@Override public Printing build() {
			return new Printing(this);
		}
	}

}
