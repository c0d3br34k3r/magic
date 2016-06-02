package magic;

import java.io.IOException;
import java.util.Objects;

import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.collect.ComparisonChain;

public final class PartialPrinting implements Comparable<PartialPrinting> {

	private final Characteristics charcs;
	private final Printing printing;
	private final @Nullable PrintingLink link;
	private final String flavorText;
	private final @Nullable CollectorNumber collectorNumber;
	private final String artist;
	private final @Nullable String watermark;

	private PartialPrinting(Builder builder) {
		this.charcs = builder.charcs;
		this.link = builder.buildLink(this);
		this.printing = builder.getWhole();
		this.flavorText = builder.flavorText;
		this.collectorNumber = builder.collectorNumber;
		this.artist = builder.artist;
		this.watermark = builder.watermark;
	}

	public Characteristics characteristics() {
		return charcs;
	}

	public Printing printing() {
		return printing;
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

	@Override public int compareTo(PartialPrinting o) {
		ComparisonChain chain = ComparisonChain.start()
				.compare(charcs, o.charcs)
				.compare(printing.expansion(), o.printing.expansion());
		if (o.collectorNumber != null) {
			chain = chain.compare(collectorNumber, o.collectorNumber);
		}
		return chain.compare(printing.variation(), o.printing.variation()).result();
	}

	@Override public String toString() {
		return charcs.name() + " (" + printing.expansion().code() + ":" + printing.rarity().code() + ")";
	}

	private static final Joiner SPACE_JOINER = Joiner.on(' ');

	public void writeTo(Appendable out) throws IOException {
		String newline = System.lineSeparator();
		out.append(charcs.name());
		if (!charcs.manaCost().isEmpty()) {
			out.append(' ').append(charcs.manaCost().toString());
		}
		out.append(newline);
		if (charcs.colorIndicator() != null) {
			out.append('(');
			for (Color color : charcs.colorIndicator()) {
				out.append(color.code());
			}
			out.append(") ");
		}
		if (!charcs.supertypes().isEmpty()) {
			SPACE_JOINER.appendTo(out, charcs.supertypes()).append(' ');
		}
		SPACE_JOINER.appendTo(out, charcs.types());
		if (!charcs.subtypes().isEmpty()) {
			out.append(" - ");
			SPACE_JOINER.appendTo(out, charcs.subtypes());
		}
		out.append(" (").append(printing.expansion().code()).append(':')
				.append(printing.rarity().code()).append(')');
		out.append(newline);
		if (!charcs.text().isEmpty()) {
			out.append(charcs.text()).append(newline);
		}
		if (!flavorText.isEmpty()) {
			out.append(flavorText).append(newline);
		}
		if (charcs.power() != null) {
			out.append(charcs.power().toString())
					.append('/')
					.append(charcs.toughness().toString())
					.append(newline);
		} else if (charcs.loyalty() != null) {
			out.append(Integer.toString(charcs.loyalty())).append(newline);
		}
		if (collectorNumber != null) {
			// TODO: fix this
			out.append(collectorNumber.toString()).append('/')
					.append(String.valueOf(printing.expansion().size())).append(' ');
		}
		out.append("Illus. ").append(artist);
		out.append(newline);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends PartialBuilder<PartialPrinting, Printing, PrintingLink> {

		private Characteristics charcs;
		private String flavorText = "";
		private CollectorNumber collectorNumber = null;
		private String artist;
		private String watermark = null;

		private Builder() {}

		void setCard(Characteristics card) {
			this.charcs = Objects.requireNonNull(card);
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

		@Override PartialPrinting build() {
			return new PartialPrinting(this);
		}

		@Override PrintingLink newLink(PartialPrinting partial, int index) {
			return new PrintingLink(partial, index);
		}
	}

}
