package magic;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.ListMultimap;

/**
 * An object containing all attributes of a card. {@code Card} objects contain
 * all printing-independent attributes, such as name and mana cost, and also
 * contain a {@code Map} of all printing information. Implementations of
 * {@code Card} should be immutable.
 * <p>
 * Because each instance of {@code Card} should be unique, it may be useful to
 * have {@code equals} and {@code hashCode} default to their identity-based
 * implementations in {@link Object}.
 */
public interface Card extends Comparable<Card> {

	/**
	 * This card's name. For example: {@code "Totally Lost"}.
	 */
	String name();

	/**
	 * This card's mana cost.
	 */
	ManaCost manaCost();

	/**
	 * A {@code Set} representing this card's color indicator. If this card has
	 * no color indicator, an empty {@code Set} is returned. Color indicators on
	 * Magic cards are never colorless.
	 */
	Set<Color> colorIndicator();

	/**
	 * This card's supertypes. For example:
	 * {@code [Supertype.LEGENDARY, Supertype.SNOW]}.
	 */
	Set<Supertype> supertypes();

	/**
	 * This card's card types. For example:
	 * {@code [Type.ARTIFACT, Type.CREATURE]}.
	 */
	Set<Type> types();

	/**
	 * This card's subtypes. For example: {@code ["Goblin", "Warrior"]}.
	 */
	Set<String> subtypes();

	/**
	 * This card's text in ASCII characters as specified by its Oracle text. If
	 * this card has no text, an empty {@code String} is returned.
	 */
	String text();

	/**
	 * Returns this card's power if it's a Creature, or {@code null} otherwise.
	 * <p>
	 * {@code power() == null ^ toughness == null} is guaranteed to be false,
	 * that is, either power and toughness are both null, or both are nonnull.
	 */
	@Nullable Expression power();

	/**
	 * Returns this card's toughness if it's a Creature, or {@code null}
	 * otherwise.
	 * <p>
	 * {@code power() == null ^ toughness == null} is guaranteed to be false,
	 * that is, either power and toughness are both null, or both are nonnull.
	 */
	@Nullable Expression toughness();

	/**
	 * Returns this card's starting loyalty, or {@code null} if this card does
	 * not have starting loyalty. If {@code types().contains(Type.PLANESWALKER)}
	 * is false, this value is guaranteed to be {@code null}.
	 */
	@Nullable Integer loyalty();

	/**
	 * Two-part cards have additional properties, such as whether they are
	 * split, flip, or double faced, what their other part is, and if they are
	 * the first part. This information is contained within the {@link Link},
	 * which is null on cards that don't represent half of a two-part card.
	 */
	@Nullable Link link();

	/**
	 * A set of this card's colors. For example:
	 * {@code [Color.WHITE, Color.RED]}. If this card has a color indicator,
	 * then the colors of the color indicator are returned. Otherwise, the color
	 * of this card's mana cost is returned.
	 */
	Set<Color> colors();

	/**
	 * This card's color identity. Color identity is inferred from this card's
	 * mana cost, color indicator, the symbols in this card's text, and the
	 * linked card's color identity, if applicable.
	 */
	Set<Color> colorIdentity();
	
	/**
	 * Returns a {@code Map} whose key set is the set of all {@code Expansion}s
	 * in which this card has been printed, mapped to printing-specific
	 * information.
	 * <p>
	 * This method returns a {@link ListMultimap} because cards can have
	 * multiple appearances within the same set. Most often this happens with
	 * Basic Lands, but some older cards, such as the Urza lands, also have
	 * multiple printings within the same expansion. The {@link List} of
	 * printings is in order of collector number, if the set uses collector
	 * numbers; otherwise it is in the arbitrary order specified by WOTC.
	 */
	ListMultimap<Expansion, ? extends Printing> printings();

	/**
	 * Prints the card in a nice format the specified {@code Appendable}.
	 * 
	 */
	void writeTo(Appendable out) throws IOException;

	/**
	 * Calls {@link #writeTo(Appendable)} using {@code System.out}. Mostly for
	 * testing.
	 */
	void print();

}
