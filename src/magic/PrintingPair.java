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
	
}
