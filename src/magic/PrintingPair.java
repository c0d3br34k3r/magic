package magic;

public class PrintingPair extends Pair<Printing> {

	private final CardPair cardPair;

	PrintingPair(Builder builder) {
		super(builder.first.build(), builder.first.getOther());
		this.cardPair = builder.cardPair;
	}

	public final CardPair cards() {
		return cardPair;
	}

	public String names() {
		return cardPair.layout().formatNames(first().card().name(),
				second().card().name());
	}

	@Override public String toString() {
		return names();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private Printing.Builder first;
		private Printing.Builder second;
		private CardPair cardPair;

		private Builder() {}

		public Builder setFirst(Printing.Builder first) {
			this.first = first;
			return this;
		}

		public Builder setSecond(Printing.Builder second) {
			this.second = second;
			return this;
		}

		public Builder setCardPair(CardPair cardPair) {
			this.cardPair = cardPair;
			return this;
		}

		void setWhole(WholePrinting whole) {
			first.setWhole(whole);
			second.setWhole(whole);
		}

		PrintingPair build() {
			first.prepareLink(second);
			return new PrintingPair(this);
		}
	}

}
