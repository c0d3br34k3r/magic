package magic;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Iterators;

import magic.Card.Builder;
import magic.Link.Layout;

public abstract class WholeCard implements Comparable<WholeCard>, Iterable<Card> {

	private final Set<Color> colorIdentity = null;

	public Set<Color> colorIdentity() {
		return colorIdentity;
	}

	public abstract String name();

	public abstract Layout layout();
	
	public abstract Card getOnly();

	public abstract Card getFirst();
	
	public abstract Card getSecond();

	@Override public int compareTo(WholeCard other) {
		return String.CASE_INSENSITIVE_ORDER.compare(name(), other.name());
	}

	private static class CompositeCard extends WholeCard {

		private final Card firstCard;
		private final Card secondCard;
		private final Layout layout;

		CompositeCard(Layout layout, Builder first, Builder second) {
			this.layout = layout;
			this.firstCard = first.build();
			this.secondCard = second.build();
		}

		@Override public String name() {
			return layout.formatNames(firstCard.name(), secondCard.name());
		}

		@Override public Layout layout() {
			return layout;
		}

		@Override public Card getOnly() {
			throw new IllegalStateException();
		}

		@Override public Card getFirst() {
			return firstCard;
		}

		@Override public Card getSecond() {
			return secondCard;
		}

		@Override public Iterator<Card> iterator() {
			return Iterators.forArray(firstCard, secondCard);
		}
	}

	private static class StandaloneCard extends WholeCard {

		private final Card card;

		public StandaloneCard(Builder only) {
			this.card = only.build();
		}

		@Override public String name() {
			return card.name();
		}

		@Override public Layout layout() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override public Card getOnly() {
			return card;
		}

		@Override public Card getFirst() {
			throw new IllegalStateException();
		}

		@Override public Card getSecond() {
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
