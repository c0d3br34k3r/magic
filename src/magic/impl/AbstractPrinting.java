package magic.impl;

import static magic.impl.AbstractCard.EOL;
import static magic.impl.AbstractCard.SPACE_JOINER;

import java.io.IOException;

import magic.Card;
import magic.Color;
import magic.Printing;

import com.google.common.annotations.Beta;

public abstract class AbstractPrinting implements Printing {

	@Override public boolean starterOnly() {
		return collectorNumber() != null && collectorNumber().starter();
	}
	
	@Override public String toString() {
		String result = card() + " (" + expansion().code()
				+ ':'
				+ rarity().code();
		if (collectorNumber() != null) {
			result += " #" + collectorNumber();
		} else if (variationIndex() != 0) {
			result += " v." + (variationIndex() + 1);
		}
		return result + ")";
	}

	/**
	 * Prints all attributes of the card.
	 * <p>
	 * Examples:
	 * <p>
	 * <code>
	 * Akroma, Angel of Wrath {5}{W}{W}{W}<br>
	 * Legendary Creature - Angel (TSB:S)<br>
	 * Flying, first strike, vigilance, trample, haste, protection from black
	 * and from red<br>
	 * /"No rest. No mercy. No matter what."/<br>
	 * (Timeshifted)<br>
	 * 6/6<br>
	 * #1 Illus. Ron Spears
	 * </code>
	 * <p>
	 * <code>
	 * Electrolyze {1}{U}{R}<br>
	 * Instant (GPT:U)<br>
	 * Electrolyze deals 2 damage divided as you choose among one or two target
	 * creatures and/or players.<br>
	 * Draw a card.<br>
	 * /The Izzet learn something from every lesson they teach./<br>
	 * [Izzet]<br>
	 * #111 Illus. Zoltan Boros & Gabor Szikszai<br>
	 * </code>
	 * <p>
	 * <code>
	 * Grizzly Bears {1}{G}<br>
	 * Creature - Bear (LEB:C)<br>
	 * /Don't try to outrun one of Dominia's Grizzlies; it'll catch you, knock 
	 * you down, and eat you. Of course, you could run up a tree. In that case
	 * you'll get a nice view before it knocks the tree down and eats you./<br>
	 * 2/2<br>
	 * Illus. Jeff A. Menges
	 * </code>
	 * 
	 * @throws IOException
	 * 
	 */
	@Beta @Override public void writeTo(Appendable out) throws IOException {
		Card card = card();
		out.append(card.name());
		if (!card.manaCost().isEmpty()) {
			out.append(' ').append(card.manaCost().toString());
		}
		if (card.link() != null) {
			out.append(" [").append(card.link().toString()).append(']');
		}
		out.append(EOL);
		if (!card.colorIndicator().isEmpty()) {
			out.append('(');
			for (Color color : card.colorIndicator()) {
				out.append(color.code());
			}
			out.append(") ");
		}
		if (!card.supertypes().isEmpty()) {
			SPACE_JOINER.appendTo(out, card.supertypes()).append(' ');
		}
		SPACE_JOINER.appendTo(out, card.types());
		if (!card.subtypes().isEmpty()) {
			out.append(" - ");
			SPACE_JOINER.appendTo(out, card.subtypes());
		}
		out.append(" (").append(expansion().code()).append(':')
				.append(rarity().code()).append(')').append(EOL);
		if (!card.text().isEmpty()) {
			out.append(card.text()).append(EOL);
		}
		if (!flavorText().isEmpty()) {
			out.append('/').append(flavorText()).append('/').append(EOL);
		}
		if (watermark() != null) {
			out.append('[').append(watermark()).append(']').append(EOL);
		}
		if (isTimeshifted()) {
			out.append("(Timeshifted)").append(EOL);
		}
		if (card.power() != null) {
			out.append(card.power().toString()).append('/')
					.append(card.toughness().toString()).append(EOL);
		} else if (card.loyalty() != null) {
			out.append(card.loyalty().toString()).append(EOL);
		}
		if (collectorNumber() != null) {
			out.append('#').append(collectorNumber().toString()).append(' ');
		}
		out.append("Illus. ").append(artist()).append(EOL);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #writeTo(Appendable)
	 */
	@Beta @Override public void print() {
		try {
			writeTo(System.out);
		} catch (IOException impossible) {
			throw new AssertionError(impossible);
		}
	}

	@Override public int compareTo(Printing o) {
		int expCmp = expansion().compareTo(o.expansion());
		if (expCmp != 0) {
			return expCmp;
		}
		if (expansion().hasCollectorNumbers()) {
			return collectorNumber().compareTo(o.collectorNumber());
		}
		int nameCmp = card().compareTo(o.card());
		return nameCmp != 0
				? nameCmp
				: Integer.compare(variationIndex(), o.variationIndex());
	}

}
