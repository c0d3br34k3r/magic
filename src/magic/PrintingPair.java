package magic;

import magic.base.Pair;

public class PrintingPair extends Pair<Printing> {

	private final CardPair cards;
	private final Printing first;
	private final Printing second;
	
	PrintingPair(CardPair cards, 
			Printing.Builder first,
			Printing.Builder second) {
		first.setLinked(second);
		Card firstBuilt = first.build();
		this.cards = cards;
		this.first = firstBuilt;
		this.second = first.getOther();
	}
	
	public final CardPair cards() {
		return cards;
	}
	
}
