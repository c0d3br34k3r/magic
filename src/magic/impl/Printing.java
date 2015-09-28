package magic.impl;

import magic.Card;
import magic.CollectorNumber;
import magic.Expansion;
import magic.Rarity;

public class Printing extends AbstractPrinting {

	private final Card card;
	private final Expansion expansion;
	private final Rarity rarity;
	private final String flavorText = "";
	private final String artist;
	private final CollectorNumber collectorNumber;
	private final int variation;
	private final String watermark = null;
	private final boolean isTimeshifted = false;
	
	@Override public Card card() {
		return card;
	}

	@Override public Expansion expansion() {
		return expansion;
	}

	@Override public Rarity rarity() {
		return rarity;
	}

	@Override public String flavorText() {
		return flavorText;
	}

	@Override public String artist() {
		return artist;
	}

	@Override public CollectorNumber collectorNumber() {
		return collectorNumber;
	}

	@Override public int variationIndex() {
		return variation;
	}

	@Override public String watermark() {
		return watermark;
	}

	@Override public boolean isTimeshifted() {
		return isTimeshifted;
	}

}
