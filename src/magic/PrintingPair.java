package magic;

public class PrintingPair extends Pair<Printing> {

	private final CardPair cards;
	
	PrintingPair(CardPair cards, Printing first, Printing second) {
		super(first, second);
		this.cards = cards;
	}
	
	public final CardPair cards() {
		return cards;
	}
	
	public String names() {
		return cards.layout().formatNames(first().card().name(), second().card().name());
	}

	@Override public String toString() {
		return names();
	}
	
}
