package magic;

import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;

public abstract class WholeCard extends Whole<Card>
		implements Comparable<WholeCard> {

	private final ImmutableSet<Color> colorIdentity;

	private WholeCard(Builder builder) {
		this.colorIdentity = Preconditions.checkNotNull(builder.colorIdentity);
	}

	public ImmutableSet<Color> colorIdentity() {
		return colorIdentity;
	}

	public abstract String name();

	@Override public abstract CardPair pair();

	@Override public abstract boolean hasOnePart();

	@Override public String toString() {
		return name();
	}

	@Override public int compareTo(WholeCard other) {
		return name().compareToIgnoreCase(other.name());
	}

	public abstract void writeTo(Appendable out) throws IOException;

	public void print() {
		try {
			writeTo(System.out);
		} catch (IOException impossible) {
			throw new AssertionError(impossible);
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	private static class StandaloneCard extends WholeCard {

		private final Card card;

		StandaloneCard(Builder builder) {
			super(builder);
			builder.only.setWhole(this);
			this.card = builder.only.build();
		}

		@Override public String name() {
			return card.name();
		}

		@Override public Card only() {
			return card;
		}

		@Override public CardPair pair() {
			throw new IllegalStateException();
		}

		@Override public Iterator<Card> iterator() {
			return Iterators.singletonIterator(card);
		}

		@Override public void writeTo(Appendable out) throws IOException {
			card.writeTo(out);
		}

		@Override public boolean hasOnePart() {
			return true;
		}
	}

	private static class CompositeCard extends WholeCard {

		private final CardPair cards;

		CompositeCard(Builder builder) {
			super(builder);
			this.cards = builder.pair.build(this);
		}

		@Override public String name() {
			return cards.names();
		}

		@Override public Card only() {
			throw new IllegalStateException();
		}

		@Override public CardPair pair() {
			return cards;
		}

		@Override public boolean hasOnePart() {
			return false;
		}

		@Override public Iterator<Card> iterator() {
			return cards.iterator();
		}

		@Override public void writeTo(Appendable out) throws IOException {
			cards.first().writeTo(out);
			out.append("* ")
					.append(cards.layout().toString().toUpperCase())
					.append(" *")
					.append(System.lineSeparator());
			cards.second().writeTo(out);
		}
	}

	public static class Builder {

		private Card.Builder only;
		private CardPair.Builder pair;
		private ImmutableSet<Color> colorIdentity = ImmutableSet.of();

		private Builder() {}

		public Builder setColorIdentity(ImmutableSet<Color> colorIdentity) {
			this.colorIdentity = Objects.requireNonNull(colorIdentity);
			return this;
		}

		public Builder setOnly(Card.Builder only) {
			this.only = only;
			return this;
		}

		public Builder setPair(CardPair.Builder pair) {
			this.pair = pair;
			return this;
		}

		public WholeCard build() {
			if (!(only == null ^ pair == null)) {
				throw new IllegalArgumentException();
			}
			if (only != null) {
				return new StandaloneCard(this);
			}
			return new CompositeCard(this);
		}

	}

}
