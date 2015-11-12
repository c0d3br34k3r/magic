package magic;

import magic.Card.Builder;

public final class CardPair {

	CardPair(Layout layout, 
			Builder first,
			Builder second) {
		first.setLinked(second);
		Card firstBuilt = first.buildCard();
		this.layout = layout;
		this.first = firstBuilt;
		this.second = first.getOther();
	}

	private final Layout layout;
	private final Card first;
	private final Card second;

	public final Layout layout() {
		return layout;
	}

	public Card first() {
		return first;
	}

	public Card second() {
		return second;
	}

	public String names() {
		return layout.formatNames(first.name(), second.name());
	}
}
