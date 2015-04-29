package magic.impl;

import java.io.IOException;
import java.util.Set;

import magic.Card;
import magic.Color;
import magic.ManaCost;

import com.google.common.base.Joiner;

/**
 * Provides some basic functionality of the {@link Card} class.
 * {@code AbstractCard} also provides an implementation for
 * {@link magic.Printing}, by using a non-static inner class that delegates all
 * methods of {@link Card} to the enclosing instance.
 */
public abstract class AbstractCard implements Card {

	static final String EOL = System.lineSeparator();

	static final Joiner SPACE_JOINER = Joiner.on(' ');
	
	/**
	 * Returns this card's color indicator if it is nonempty; otherwise returns
	 * the colors of this card's {@link ManaCost}.
	 */
	@Override public Set<Color> colors() {
		return colorIndicator().isEmpty()
				? manaCost().colors()
				: colorIndicator();
	}

	/**
	 * Prints all attributes of the card other than printing-specific
	 * information.
	 * <p>
	 * Examples:
	 * <p>
	 * <code>
	 * Lhurgoyf {2}{G}{G} <br>
	 * Creature - Lhurgoyf <br>
	 * Lhurgoyf's power is equal to the number of creature cards in all
	 * graveyards and its toughness is equal to that number plus 1.<br>
	 * *&#x2F;1+*
	 * </code>
	 * <p>
	 * <code>
	 * Pact of Negation {0}<br>
	 * (U) Instant<br>
	 * Counter target spell.<br>
	 * At the beginning of your next upkeep, pay {3}{U}{U}. If you don't, you
	 * lose the game.<br>
	 * </code>
	 * <p>
	 * <code>
	 * Chandra Pyromaster {2}{R}{R}<br>
	 * Planeswalker - Chandra<br>
	 * +1: Chandra, Pyromaster deals 1 damage to target player and 1 damage to
	 * up to one target creature that player controls. That creature can't block
	 * this turn.<br>
	 * 0: Exile the top card of your library. You may play it this turn.<br>
	 * -7: Exile the top ten cards of your library. Choose an instant or sorcery
	 * card exiled this way and copy it three times. You may cast the copies
	 * without paying their mana costs.<br>
	 * 4
	 * </code>
	 */
	@Override public void writeTo(Appendable out) throws IOException {
		out.append(name());
		if (!manaCost().isEmpty()) {
			out.append(' ').append(manaCost().toString());
		}
		if (link() != null) {
			out.append(" [").append(link().toString()).append(']');
		}
		out.append(EOL);
		if (!colorIndicator().isEmpty()) {
			out.append('(');
			for (Color color : colorIndicator()) {
				out.append(color.code());
			}
			out.append(") ");
		}
		if (!supertypes().isEmpty()) {
			SPACE_JOINER.appendTo(out, supertypes()).append(' ');
		}
		SPACE_JOINER.appendTo(out, types());
		if (!subtypes().isEmpty()) {
			out.append(" - ");
			SPACE_JOINER.appendTo(out, subtypes());
		}
		out.append(EOL);
		if (!text().isEmpty()) {
			out.append(text()).append(EOL);
		}
		if (power() != null) {
			out.append(power().toString()).append('/')
					.append(toughness().toString()).append(EOL);
		} else if (loyalty() != null) {
			out.append(Integer.toString(loyalty())).append(EOL);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see #writeTo(Appendable)
	 */
	@Override public void print() {
		try {
			writeTo(System.out);
		} catch (IOException impossible) {
			throw new AssertionError(impossible);
		}
	}
	
	/**
	 * Returns this card's name.
	 */
	@Override public String toString() {
		return name();
	}
	
	/**
	 * Provides a natural ordering for {@code Card}s based on non-case-sensitive
	 * alphabetical ordering.
	 */
	@Override public int compareTo(Card o) {
		return String.CASE_INSENSITIVE_ORDER.compare(name(), o.name());
	}
	
}
