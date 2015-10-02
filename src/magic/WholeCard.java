package magic;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

import magic.Card.Builder;
import magic.Card.CardPair;

public abstract class WholeCard implements Comparable<WholeCard>, Iterable<Card> {

	private final ImmutableSet<Color> colorIdentity;

	private WholeCard(Set<Color> colorIdentity) {
		this.colorIdentity = Color.INTERNER.intern(colorIdentity);
	}

	public Set<Color> colorIdentity() {
		return colorIdentity;
	}

	public abstract String name();

	public abstract Card card();

	public abstract CardPair cards();

	@Override public String toString() {
		return name();
	}

	@Override public int compareTo(WholeCard other) {
		return String.CASE_INSENSITIVE_ORDER.compare(name(), other.name());
	}

	static class StandaloneCard extends WholeCard {

		private final Card card;

		StandaloneCard(Builder only) {
			super(only.calculateColorIdentity());
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
	}

	static class CompositeCard extends WholeCard {

		private final CardPair cards;

		CompositeCard(Layout layout, Builder first, Builder second) {
			super(Sets.union(first.calculateColorIdentity(), second.calculateColorIdentity()));
			first.setWhole(this);
			second.setWhole(this);
			this.cards = first.buildLinkedTo(layout, second);
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
	}

}
