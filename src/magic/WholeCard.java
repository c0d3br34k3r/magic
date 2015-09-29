package magic;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Iterators;

import magic.Card.Builder;
import magic.Card.CardPair;

public abstract class WholeCard implements Comparable<WholeCard>, Iterable<Card> {

	private final Set<Color> colorIdentity = null;

	public Set<Color> colorIdentity() {
		return colorIdentity;
	}

	public abstract String name();

	public abstract Card card();

	public abstract CardPair cards();

	@Override public int compareTo(WholeCard other) {
		return String.CASE_INSENSITIVE_ORDER.compare(name(), other.name());
	}

	private static class CompositeCard extends WholeCard {

		private final CardPair cards;

		CompositeCard(Layout layout, Builder first, Builder second) {
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

	private static class StandaloneCard extends WholeCard {

		private final Card card;

		public StandaloneCard(Builder only) {
			only.setWhole(this);
			this.card = only.build();
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

	public static WholeCard create(Card.Builder only) {
		return new StandaloneCard(only);
	}

	public static WholeCard create(Layout layout,
			Card.Builder first,
			Card.Builder second) {
		return new CompositeCard(layout, first, second);
	}

}
