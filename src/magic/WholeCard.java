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

	@Override public abstract boolean isStandalone();

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

		StandaloneCard(Builder builder, Card.Builder only) {
			super(builder);
			only.setWhole(this);
			this.card = only.build();
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

		@Override public boolean isStandalone() {
			return true;
		}
	}

	private static class CompositeCard extends WholeCard {

		private final CardPair cards;

		CompositeCard(Builder builder, Card.Builder first,
				Card.Builder second) {
			super(builder);
			PartialBuilder.link(first, second, this);
			this.cards = new CardPair(builder.layout, first.build(),
					first.getOther());
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

		@Override public boolean isStandalone() {
			return false;
		}
	}

	public static class Builder {

		private Layout layout;
		private Card.Builder firstOrOnly;
		private Card.Builder second;
		private ImmutableSet<Color> colorIdentity;

		private Builder() {}

		public Builder setColorIdentity(ImmutableSet<Color> colorIdentity) {
			this.colorIdentity = Objects.requireNonNull(colorIdentity);
			return this;
		}

		public Builder setLayout(Layout layout) {
			this.layout = Objects.requireNonNull(layout);
			return this;
		}

		public Builder setOnly(Card.Builder only) {
			this.firstOrOnly = only;
			return this;
		}

		public Builder setFirst(Card.Builder first) {
			return setOnly(first);
		}

		public Builder setSecond(Card.Builder second) {
			this.second = second;
			return this;
		}

		public void addPart(Card.Builder part) {
			if (firstOrOnly == null) {
				firstOrOnly = part;
			} else if (second == null) {
				second = part;
			} else {
				throw new IllegalStateException();
			}
		}

		public WholeCard build() {
			if (layout == null) {
				if (second != null) {
					throw new IllegalStateException();
				}
				return new StandaloneCard(this, firstOrOnly);
			}
			return new CompositeCard(this, firstOrOnly, second);
		}

	}

}
