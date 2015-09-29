package magic;

import java.io.IOException;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class Card implements Comparable<Card> {

	private final WholeCard whole;
	private final Link link;

	private final String name;
	private final ManaCost manaCost;
	private final ImmutableSet<Color> colorIndicator;
	private final ImmutableSet<Supertype> supertypes;
	private final ImmutableSet<Type> types;
	private final ImmutableSet<String> subtypes;
	private final String text;
	@Nullable private final Expression power;
	@Nullable private final Expression toughness;
	@Nullable private final Integer loyalty;
	private final ImmutableSet<Color> colors;

	private Card(Builder builder) {
		this.whole = builder.whole;

		this.name = builder.name;
		this.manaCost = builder.manaCost;
		this.colorIndicator = builder.colorIndicator;
		this.supertypes = builder.supertypes;
		this.types = builder.types;
		this.subtypes = builder.subtypes;
		this.text = builder.text;
		this.power = builder.power;
		this.toughness = builder.toughness;
		this.loyalty = builder.loyalty;
		this.colors = builder.calculateColors();

		this.link = builder.buildLink(this);
	}

	public String name() {
		return name;
	}

	public ManaCost manaCost() {
		return manaCost;
	}

	public Set<Color> colorIndicator() {
		return colorIndicator;
	}

	public Set<Supertype> supertypes() {
		return supertypes;
	}

	public Set<Type> types() {
		return types;
	}

	public Set<String> subtypes() {
		return subtypes;
	}

	public String text() {
		return text;
	}

	public Expression power() {
		return power;
	}

	public Expression toughness() {
		return toughness;
	}

	public Integer loyalty() {
		return loyalty;
	}

	public Set<Color> colors() {
		return colors;
	}

	public WholeCard whole() {
		return whole;
	}

	@Nullable public Link link() {
		return link;
	}

	@Override public int compareTo(Card other) {
		return String.CASE_INSENSITIVE_ORDER.compare(name, other.name);
	}

	private static final Joiner SPACE_JOINER = Joiner.on(' ');

	public void writeTo(Appendable out) throws IOException {
		out.append(name());
		if (!manaCost().isEmpty()) {
			out.append(' ').append(manaCost().toString());
		}
		// if (link() != null) {
		// out.append(" [").append(link().toString()).append(']');
		// }
		out.append('\n');
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
		out.append('\n');
		if (!text().isEmpty()) {
			out.append(text()).append('\n');
		}
		if (power() != null) {
			out.append(power().toString()).append('/')
					.append(toughness().toString()).append('\n');
		} else if (loyalty() != null) {
			out.append(Integer.toString(loyalty())).append('\n');
		}
	}

	@Override public String toString() {
		return name;
	}

	public void print() {
		try {
			writeTo(System.out);
		} catch (IOException impossible) {
			throw new AssertionError(impossible);
		}
	}

	public static final class CardPair {

		private CardPair(Layout layout, Card firstCard, Card secondCard) {
			this.layout = layout;
			this.firstCard = firstCard;
			this.secondCard = secondCard;
		}

		private final Card firstCard;
		private final Card secondCard;
		private final Layout layout;

		public final Layout layout() {
			return layout;
		}

		public Card first() {
			return firstCard;
		}

		public Card second() {
			return secondCard;
		}

		public String names() {
			return layout.formatNames(firstCard.name, secondCard.name);
		}
	}

	public static class Builder {

		private WholeCard whole;

		private String name;
		private ManaCost manaCost = ManaCost.EMPTY;
		private ImmutableSet<Color> colorIndicator = ImmutableSet.of();
		private ImmutableSet<Supertype> supertypes = ImmutableSet.of();
		private ImmutableSet<Type> types;
		private ImmutableSet<String> subtypes = ImmutableSet.of();
		private String text = "";
		@Nullable private Expression power = null;
		@Nullable private Expression toughness = null;
		@Nullable private Integer loyalty = null;
		private boolean isColorless = false;

		private Builder linked;
		private Card firstHalf;
		private Card secondHalf;

		public void setName(String name) {
			this.name = name;
		}

		public void setManaCost(ManaCost manaCost) {
			this.manaCost = manaCost;
		}

		public void setColorIndicator(Set<Color> colorIndicator) {
			this.colorIndicator = Sets.immutableEnumSet(colorIndicator);
		}

		public void setSupertypes(Set<Supertype> supertypes) {
			this.supertypes = Sets.immutableEnumSet(supertypes);
		}

		public void setTypes(Set<Type> types) {
			this.types = Sets.immutableEnumSet(types);
		}

		public void setSubtypes(Set<String> subtypes) {
			this.subtypes = ImmutableSet.copyOf(subtypes);
		}

		public void setText(String text) {
			this.text = text;
		}

		public void setPower(Expression power) {
			this.power = power;
		}

		public void setToughness(Expression toughness) {
			this.toughness = toughness;
		}

		public void setLoyalty(Integer loyalty) {
			this.loyalty = loyalty;
		}

		public void setColorless() {
			this.isColorless = true;
		}

		private ImmutableSet<Color> calculateColors() {
			if (isColorless) {
				return ImmutableSet.of();
			} else if (!colorIndicator.isEmpty()) {
				return colorIndicator;
			} else {
				return manaCost.colors();
			}
		}

		void setWhole(WholeCard whole) {
			this.whole = whole;
		}

		Card build() {
			return new Card(this);
		}

		private Link buildLink(Card partiallyBuilt) {
			if (linked != null) {
				firstHalf = partiallyBuilt;
				Card secondHalf = linked.build();
				this.secondHalf = secondHalf;
				return Link.create(secondHalf, true);
			}
			if (firstHalf != null) {
				return Link.create(firstHalf, false);
			}
			return null;
		}

		CardPair buildLinkedTo(Layout layout, Builder linked) {
			this.linked = linked;
			Card built = build();
			return new CardPair(layout, built, secondHalf);
		}
	}

}
