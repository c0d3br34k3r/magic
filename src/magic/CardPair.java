package magic;

import magic.base.Pair;

public final class CardPair extends Pair<Card> {
	
	private final Layout layout;
	private final Card first;
	private final Card second;

	CardPair(Layout layout, 
			Card.Builder first,
			Card.Builder second) {
		first.setLinked(second);
		Card firstBuilt = first.build();
		this.layout = layout;
		this.first = firstBuilt;
		this.second = first.getOther();
	}

	public final Layout layout() {
		return layout;
	}

	public String names() {
		return layout.formatNames(first.name(), second.name());
	}
}
