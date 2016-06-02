package magic.misc;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import magic.Characteristics;
import magic.Expansion;
import magic.Expansion.ReleaseType;
import magic.Layout;
import magic.ManaSymbol;
import magic.PartialPrinting;
import magic.Subtype;
import magic.Type;

/**
 * Utility class for cards. Disclaimer: I have not put much thought into this
 * and it's pretty rough.
 */
public final class Cards {

	private Cards() {}

	public static boolean isPermanent(Characteristics card) {
		for (Type type : card.types()) {
			if (type.isPermanent()) {
				return true;
			}
		}
		return false;
	}

	private static final Predicate<Expansion> PHYSICAL = new Predicate<Expansion>() {
		@Override public boolean apply(Expansion input) {
			return input.type() != ReleaseType.ONLINE;
		}
	};

	public static Multimap<Expansion, ? extends PartialPrinting> physicalPrintings(
			Multimap<Expansion, ? extends PartialPrinting> printings) {
		return Multimaps.filterKeys(printings, PHYSICAL);
	}

	public static Predicate<Characteristics> textContainsNotReminder(final String text) {
		final Pattern pattern =
				Pattern.compile("\\Q" + text + "\\E(?![^(]*\\))",
						Pattern.CASE_INSENSITIVE);
		return new Predicate<Characteristics>() {
			@Override public boolean apply(Characteristics card) {
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

	private static final Set<ManaSymbol> HYBRID = Sets.immutableEnumSet(EnumSet
			.range(ManaSymbol.HYBRID_WHITE_BLUE, ManaSymbol.HYBRID_GREEN_BLUE));
	
	private static final Set<ManaSymbol> PRIMARY = Sets.immutableEnumSet(EnumSet
			.range(ManaSymbol.WHITE, ManaSymbol.GREEN));

	private static Section section(Characteristics c) {
		if (c.link() != null
				&& c.whole().layout() == Layout.SPLIT
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
				if (Collections.disjoint(c.manaCost().symbols(), HYBRID)) {
					return Section.GOLD;
				}
				return Collections.disjoint(c.manaCost().symbols(), PRIMARY)
						? Section.GOLD_HYBRID
						: Section.HYBRID;
		}
	}

	private static final Ordering<String> BASIC_TYPES = Ordering
			.explicit(Subtype.BASIC_LAND_TYPES.keySet().asList());

	public static final Ordering<Characteristics> REGULAR_ORDERING = new Ordering<Characteristics>() {
		@Override public int compare(Characteristics c1, Characteristics c2) {
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
			Multimap<Expansion, ? extends PartialPrinting> printings) {
		StringBuilder builder = new StringBuilder();

		Iterator<? extends Entry<Expansion, ? extends Collection<? extends PartialPrinting>>> it =
				printings.asMap().entrySet().iterator();
		for (;;) {
			Entry<Expansion, ? extends Collection<? extends PartialPrinting>> entry =
					it.next();
			builder.append(entry.getKey().code()).append(':')
					.append(entry.getValue().iterator().next().printing().rarity()
							.code());
			if (entry.getValue().size() > 1) {
				builder.append('(').append(entry.getValue().size()).append(')');
			}
			if (!it.hasNext()) {
				return builder.toString();
			}
			builder.append(", ");
		}
	}

}
