package magic;

import java.io.IOException;

import javax.annotation.Nullable;

import com.google.common.annotations.Beta;

/**
 * An object contain all printing-specific attributes of a card within an
 * expansion. Implementations of {@code Printing} should be immutable.
 * <p>
 * Because each instance of {@code Printing} should be unique, it may be useful
 * to have {@code equals} and {@code hashCode} default to their identity-based
 * implementations in {@link Object}.
 */
public interface Printing extends Comparable<Printing> {

	/**
	 * The {@link Card} that contains this printing.
	 */
	Card card();

	/**
	 * The expansion in which this card is printed.
	 */
	Expansion expansion();

	/**
	 * The card's rarity.
	 */
	Rarity rarity();

	/**
	 * The card's flavor text, using ASCII characters where possible.
	 * <p>
	 * The only time this wasn't possible was for Niv-Mizzet, the Firemind's
	 * original printing!
	 */
	String flavorText();

	/**
	 * The card's artist.
	 */
	String artist();

	/**
	 * The card's collector number, which is null when not supported.
	 */
	@Nullable CollectorNumber collectorNumber();

	/**
	 * Returns the zero-based variation number of the card, representing its
	 * ordinal position among other printings of the same card within an
	 * expansion.
	 */
	int variationIndex();

	/**
	 * In Core Sets, this is set to true if the card is only available through
	 * starter kits and not in booster packs.
	 */
	boolean starterOnly();

	/**
	 * The card's watermark, or {@code null} if this card has no watermark.
	 */
	@Nullable String watermark();

	/**
	 * Whether the card is Timeshifted. Only applicable to Time Spiral-block
	 * cards.
	 */
	boolean isTimeshifted();

	/**
	 * Prints the card in a nice format the specified {@code Appendable}.
	 * 
	 */
	void writeTo(Appendable out) throws IOException;

	/**
	 * Calls {@link #writeTo(Appendable)} using {@code System.out}.
	 */
	@Beta void print();

}
