package magic;

public final class CardPair extends Pair<Card> {

	private final Layout layout;

	CardPair(Layout layout, Card first, Card second) {
		super(first, second);
		this.layout = layout;
	}

	public final Layout layout() {
		return layout;
	}

	public String names() {
		return layout.formatNames(first().name(), second().name());
	}

	@Override public String toString() {
		return names();
	}

}
