package magic;

import java.util.List;
import java.util.Objects;

public final class CardPair extends Pair<Card> {

	private final Layout layout;

	CardPair(Builder builder) {
		super(builder.first.build(), builder.first.getOther());
		this.layout = Objects.requireNonNull(builder.layout);
	}

	public final Layout layout() {
		return layout;
	}

	public String names() {
		return layout.formatNames(first().name(), second().name());
	}

	@Override
	public String toString() {
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
			this.first = Objects.requireNonNull(first);
			return this;
		}

		public Builder setSecond(Card.Builder second) {
			this.second = Objects.requireNonNull(second);
			return this;
		}

		public Builder setCards(List<Card.Builder> cards) {
			if (cards.size() != 2) {
				throw new IllegalArgumentException();
			}
			this.first = cards.get(0);
			this.second = cards.get(1);
			return this;
		}

		public Builder setLayout(Layout layout) {
			this.layout = Objects.requireNonNull(layout);
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
