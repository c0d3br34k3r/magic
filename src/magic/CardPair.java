package magic;

public final class CardPair extends Pair<Card> {

	private final Layout layout;

	CardPair(Builder builder) {
		super(builder.first.build(), builder.first.getOther());
		this.layout = builder.layout;
	}

	public final Layout layout() {
		return layout;
	}

	public String names() {
		return layout.formatNames(first().name(), second().name());
	}

	@Override public String toString() {
		return names();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private Card.Builder first;
		private Card.Builder second;
		private Layout layout;

		private Builder() {}

		public Builder setFirst(Card.Builder first) {
			this.first = first;
			return this;
		}

		public Builder setSecond(Card.Builder second) {
			this.second = second;
			return this;
		}

		public Builder setLayout(Layout layout) {
			this.layout = layout;
			return this;
		}

		CardPair build(WholeCard whole) {
			first.setWhole(whole);
			second.setWhole(whole);
			first.prepareLink(second);
			return new CardPair(this);
		}
	}

}
