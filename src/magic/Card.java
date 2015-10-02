package magic;

import java.io.IOException;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import magic.WholeCard.CompositeCard;
import magic.WholeCard.StandaloneCard;

public final class Card implements Comparable<Card> {

	private final WholeCard whole;
	private final @Nullable Link link;

	private final String name;
	private final ManaCost manaCost;
	private final ImmutableSet<Color> colorIndicator;
	private final ImmutableSet<Supertype> supertypes;
	private final ImmutableSet<Type> types;
	private final ImmutableSet<String> subtypes;
	private final String text;
	private final @Nullable Expression power;
	private final @Nullable Expression toughness;
	private final @Nullable Integer loyalty;
	private final ImmutableSet<Color> colors;

	private Card(Builder builder) {
		this.whole = builder.whole;
		this.link = builder.buildLink(this);

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

	public @Nullable Expression power() {
		return power;
	}

	public @Nullable Expression toughness() {
		return toughness;
	}

	public @Nullable Integer loyalty() {
		return loyalty;
	}

	public Set<Color> colors() {
		return colors;
	}

	public WholeCard whole() {
		return whole;
	}

	public @Nullable Link link() {
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
		if (link() != null) {
			out.append(' ').append(link().toString());
		}
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

		private String name;
		private ManaCost manaCost = ManaCost.EMPTY;
		private ImmutableSet<Color> colorIndicator = ImmutableSet.of();
		private ImmutableSet<Supertype> supertypes = ImmutableSet.of();
		private ImmutableSet<Type> types;
		private ImmutableSet<String> subtypes = ImmutableSet.of();
		private String text = "";
		private @Nullable Expression power = null;
		private @Nullable Expression toughness = null;
		private @Nullable Integer loyalty = null;

		private Set<Color> symbolColorsInText = Collections.emptySet();
		private boolean isColorless = false;

		private WholeCard whole;
		private @Nullable Builder linked;
		private @Nullable Card firstHalf;
		private @Nullable Card secondHalf;

		public Builder() {}

		public Builder setName(String name) {
			this.name = name;
			return this;
		}

		public Builder setManaCost(ManaCost manaCost) {
			this.manaCost = manaCost;
			return this;
		}

		public Builder setColorIndicator(Set<Color> colorIndicator) {
			this.colorIndicator = Sets.immutableEnumSet(colorIndicator);
			return this;
		}

		public Builder setSupertypes(Set<Supertype> supertypes) {
			this.supertypes = Sets.immutableEnumSet(supertypes);
			return this;
		}

		public Builder setTypes(Set<Type> types) {
			this.types = Sets.immutableEnumSet(types);
			return this;
		}

		public Builder setSubtypes(Set<String> subtypes) {
			this.subtypes = ImmutableSet.copyOf(subtypes);
			return this;
		}

		public Builder setText(String text) {
			this.text = text;
			return this;
		}

		public Builder setPower(Expression power) {
			this.power = power;
			return this;
		}

		public Builder setToughness(Expression toughness) {
			this.toughness = toughness;
			return this;
		}

		public Builder setLoyalty(Integer loyalty) {
			this.loyalty = loyalty;
			return this;
		}

		public Builder setColorless() {
			this.isColorless = true;
			return this;
		}

		public Builder setSymbolColorsInText(Set<Color> symbolColorsInText) {
			this.symbolColorsInText = symbolColorsInText;
			return this;
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

		Card buildCard() {
			return new Card(this);
		}

		private Link buildLink(Card partiallyBuilt) {
			if (linked != null) {
				firstHalf = partiallyBuilt;
				Card secondHalf = linked.buildCard();
				this.secondHalf = secondHalf;
				return new Link(secondHalf, true);
			}
			if (firstHalf != null) {
				return new Link(firstHalf, false);
			}
			return null;
		}

		CardPair buildLinkedTo(Layout layout, Builder linked) {
			this.linked = linked;
			Card built = buildCard();
			return new CardPair(layout, built, secondHalf);
		}

		Set<Color> calculateColorIdentity() {
			EnumSet<Color> colorIdentity = EnumSet.copyOf(manaCost.colors());
			colorIdentity.addAll(colorIndicator);
			colorIdentity.addAll(symbolColorsInText);
			return colorIdentity;
		}

		public WholeCard build() {
			return new StandaloneCard(this);
		}

		public WholeCard buildWith(Layout layout, Builder other) {
			return new CompositeCard(layout, this, other);
		}
	}

	public static Builder builder() {
		return new Builder();
	}

}
