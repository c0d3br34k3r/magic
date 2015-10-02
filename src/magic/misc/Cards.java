package magic.misc;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import magic.Card;
import magic.Expansion;
import magic.Link;
import magic.Link.Layout;
import magic.Printing;
import magic.Rarity;
import magic.Subtype;
import magic.Symbol.Group;
import magic.Type;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

/**
 * Utility class for cards. Disclaimer: I have not put much thought into this
 * and it's pretty rough.
 */
public final class Cards {

	private Cards() {}

	public static boolean isRepresentative(Card card) {
		return card.link() == null || card.link().isFirstHalf();
	}

	public static Printing linkedPrinting(Printing printing) {
		Link link = printing.card().link();
		if (link == null) {
			throw new IllegalArgumentException("not a linking card");
		}
		return link.get()
				.printings()
				.get(printing.expansion())
				.get(printing.variationIndex());
	}

	private static final Set<Type> PERMANENT_TYPES = Sets.immutableEnumSet(
			Type.ENCHANTMENT,
			Type.ARTIFACT,
			Type.LAND,
			Type.CREATURE,
			Type.PLANESWALKER);

	public static boolean isPermanent(Card card) {
		return !Collections.disjoint(card.types(), PERMANENT_TYPES);
	}

	private static Predicate<Expansion> PHYSICAL = new Predicate<Expansion>() {
		@Override public boolean apply(Expansion input) {
			return input.isPhysical();
		}
	};

	public static Multimap<Expansion, ? extends Printing> physicalPrintings(
			Multimap<Expansion, ? extends Printing> printings) {
		return Multimaps.filterKeys(printings, PHYSICAL);
	}

	public static Multimap<Expansion, ? extends Printing> physicalPrintings(Card card) {
		return physicalPrintings(card.printings());
	}

	public static Predicate<Card> textContainsNotReminder(final String text) {
		final Pattern pattern =
				Pattern.compile("\\Q" + text + "\\E(?![^(]*\\))",
						Pattern.CASE_INSENSITIVE);
		return new Predicate<Card>() {
			@Override public boolean apply(Card card) {
				return pattern.matcher(card.text()).find();
			}
		};
	}

	private enum Section {
		CLEAR,
		MONOCOLORED,
		GOLD,
		HYBRID,
		GOLD_HYBRID,
		SPLIT,
		ARTIFACT,
		NONBASIC_LAND,
		BASIC_LAND;
	}

	private static Section section(Card c) {
		if (c.link() != null
				&& c.link().layout() == Layout.SPLIT
				&& !c.colors().equals(c.link().get().colors())) {
			return Section.SPLIT;
		}
		switch (c.colors().size()) {
			case 1:
				return Section.MONOCOLORED;
			case 0:
				if (Subtype.BASIC_LAND_TYPES.containsKey(c.name())) {
					return Section.BASIC_LAND;
				}
				if (c.types().contains(Type.LAND)) {
					return Section.NONBASIC_LAND;
				}
				if (c.types().contains(Type.ARTIFACT)) {
					return Section.ARTIFACT;
				}
				return Section.CLEAR;
			default:
				if (!c.manaCost().containsAnyOf(Group.HYBRID)) {
					return Section.GOLD;
				}
				return c.manaCost().containsAnyOf(Group.PRIMARY)
						? Section.GOLD_HYBRID
						: Section.HYBRID;
		}
	}

	private static final Ordering<String> BASIC_TYPES = Ordering
			.explicit(Subtype.BASIC_LAND_TYPES.keySet().asList());

	public static Ordering<Card> REGULAR_ORDERING = new Ordering<Card>() {
		@Override public int compare(Card c1, Card c2) {
			Section section = section(c1);
			int sectionCmp = section.compareTo(section(c2));
			if (sectionCmp != 0) {
				return sectionCmp;
			}
			if (section == Section.MONOCOLORED) {
				int monoColorCmp =
						Iterables.getOnlyElement(c1.colors()).compareTo(
								Iterables.getOnlyElement(c2.colors()));
				if (monoColorCmp != 0) {
					return monoColorCmp;
				}
			}
			if (section == Section.BASIC_LAND) {
				return BASIC_TYPES.compare(
						Iterables.getOnlyElement(c1.subtypes()),
						Iterables.getOnlyElement(c2.subtypes()));
			}
			return c1.name().compareTo(c2.name());
		}
	};

	public static String formatPrintings(
			Multimap<Expansion, ? extends Printing> printings) {
		StringBuilder builder = new StringBuilder();

		Iterator<? extends Entry<Expansion, ? extends Collection<? extends Printing>>> it =
				printings.asMap().entrySet().iterator();
		for (;;) {
			Entry<Expansion, ? extends Collection<? extends Printing>> entry = it.next();
			builder.append(entry.getKey().code()).append(':')
					.append(entry.getValue().iterator().next().rarity().code());
			if (entry.getValue().size() > 1) {
				builder.append('(').append(entry.getValue().size()).append(')');
			}
			if (!it.hasNext()) {
				return builder.toString();
			}
			builder.append(", ");
		}
	}

	public static boolean isRarity(Card card, Rarity rarity) {
		for (Printing printing : card.printings().values()) {
			if (printing.rarity() == rarity) {
				return true;
			}
		}
		return false;
	}
	
	public static Rarity minRarity(Card card) {
		return minOrMaxRarity(card, -1);
	}
	
	public static Rarity maxRarity(Card card) {
		return minOrMaxRarity(card, 1);
	}
	
	private static Rarity minOrMaxRarity(Card card, final int sign) {
		Iterator<? extends Printing> it = card.printings().values().iterator();
		Rarity result = it.next().rarity();
		while (it.hasNext()) {
			Rarity next = it.next().rarity();
			if (Integer.signum(next.compareTo(result)) == sign) {
				result = next;
			}
		}
		return result;
	}

}
