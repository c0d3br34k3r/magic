package magic;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;

public abstract class WholeCard
		implements Comparable<WholeCard>, Iterable<Card> {

	private final ImmutableSet<Color> colorIdentity;

	private WholeCard(ImmutableSet<Color> colorIdentity) {
		this.colorIdentity = Preconditions.checkNotNull(colorIdentity);
	}

	public Set<Color> colorIdentity() {
		return colorIdentity;
	}

	public abstract String name();

	public abstract Card card();

	public abstract CardPair cards();
	
	public abstract boolean isStandalone();

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
	
	public static class Builder {

		private Builder() {}
		
		private Layout layout;
		private Card.Builder firstOrOnly;
		private Card.Builder second;
		private ImmutableSet<Color> colorIdentity;

		public Builder setLayout(Layout layout) {
			this.layout = layout;
			return this;
		}

		public Builder setOnly(Card.Builder only) {
			this.firstOrOnly = only;
			return this;
		}

		public Builder setFirst(Card.Builder first) {
			this.firstOrOnly = first;
			return this;
		}

		public Builder setSecond(Card.Builder second) {
			this.second = second;
			return this;
		}

		public Builder setColorIdentity(ImmutableSet<Color> colorIdentity) {
			this.colorIdentity = colorIdentity;
			return this;
		}

		public WholeCard build() {
			if (layout == null) {
				if (second != null) {
					throw new IllegalStateException();
				}
				return new StandaloneCard(colorIdentity, firstOrOnly);
			}
			return new CompositeCard(colorIdentity,
					layout,
					firstOrOnly,
					second);
		}
	}

	private static class StandaloneCard extends WholeCard {

		private final Card card;

		StandaloneCard(ImmutableSet<Color> colorIdentity, Card.Builder only) {
			super(colorIdentity);
			only.setWhole(this);
			this.card = only.buildCard();
		}

		@Override public String name() {
			return card.name();
		}

		@Override public Card card() {
			return card;
		}

		@Override public CardPair cards() {
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

		CompositeCard(ImmutableSet<Color> colorIdentity, Layout layout,
				Card.Builder first, Card.Builder second) {
			super(colorIdentity);
			first.setWhole(this);
			second.setWhole(this);
			this.cards = new CardPair(layout, first, second);
		}

		@Override public String name() {
			return cards.names();
		}

		@Override public Card card() {
			throw new IllegalStateException();
		}

		@Override public CardPair cards() {
			return cards;
		}

		@Override public Iterator<Card> iterator() {
			return Iterators.forArray(cards.first(), cards.second());
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

}
