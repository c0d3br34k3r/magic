package magic;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;

public final class Card implements Comparable<Card> {

	private final WholeCard whole;
	private final @Nullable CardLink link;
	private final String name;
	private final ManaCost manaCost;
	private final @Nullable ImmutableSet<Color> colorIndicator;
	private final ImmutableSet<Supertype> supertypes;
	private final ImmutableSet<Type> types;
	private final ImmutableSet<String> subtypes;
	private final String text;
	private final @Nullable Expression power;
	private final @Nullable Expression toughness;
	private final @Nullable Integer loyalty;

	private Card(Builder builder) {
		this.whole = builder.getWhole();
		this.link = builder.buildLink(this);

		this.name = Objects.requireNonNull(builder.name);
		this.manaCost = builder.manaCost;
		this.colorIndicator = builder.colorIndicator;
		this.supertypes = builder.supertypes;
		this.types = Objects.requireNonNull(builder.types);
		this.subtypes = builder.subtypes;
		this.text = builder.text;
		this.power = builder.power;
		this.toughness = builder.toughness;
		this.loyalty = builder.loyalty;
	}

	public String name() {
		return name;
	}

	public ManaCost manaCost() {
		return manaCost;
	}

	public @Nullable Set<Color> colorIndicator() {
		return colorIndicator;
	}

	public ImmutableSet<Supertype> supertypes() {
		return supertypes;
	}

	public ImmutableSet<Type> types() {
		return types;
	}

	public ImmutableSet<String> subtypes() {
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

	public ImmutableSet<Color> colors() {
		return MoreObjects.firstNonNull(colorIndicator, manaCost.colors());
	}

	public WholeCard whole() {
		return whole;
	}

	public @Nullable CardLink link() {
		return link;
	}

	@Override public int compareTo(Card other) {
		return name.compareToIgnoreCase(other.name);
	}

	@Override public String toString() {
		return name;
	}

	private static final Joiner SPACE_JOINER = Joiner.on(' ');

	public void writeTo(Appendable out) throws IOException {
		String newline = System.lineSeparator();
		out.append(name);
		if (!manaCost.isEmpty()) {
			out.append(' ').append(manaCost.toString());
		}
		out.append(newline);
		if (colorIndicator != null && !colorIndicator.isEmpty()) {
			out.append('(');
			for (Color color : colorIndicator) {
				out.append(color.code());
			}
			out.append(") ");
		}
		if (!supertypes.isEmpty()) {
			SPACE_JOINER.appendTo(out, supertypes).append(' ');
		}
		SPACE_JOINER.appendTo(out, types);
		if (!subtypes.isEmpty()) {
			out.append(" — ");
			SPACE_JOINER.appendTo(out, subtypes);
		}
		if (!text.isEmpty()) {
			out.append(newline).append(text);
		}
		if (power != null) {
			out.append(newline).append(power.toString()).append('/')
					.append(toughness.toString());
		} else if (loyalty != null) {
			out.append(newline).append(Integer.toString(loyalty));
		}
		out.append(newline);
	}

	public void print() {
		try {
			writeTo(System.out);
		} catch (IOException impossible) {
			throw new AssertionError(impossible);
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends PartialBuilder<Card, WholeCard, CardLink> {

		private String name;
		private ManaCost manaCost = ManaCost.EMPTY;
		private ImmutableSet<Color> colorIndicator = null;
		private ImmutableSet<Supertype> supertypes = ImmutableSet.of();
		private ImmutableSet<Type> types;
		private ImmutableSet<String> subtypes = ImmutableSet.of();
		private String text = "";
		private @Nullable Expression power = null;
		private @Nullable Expression toughness = null;
		private @Nullable Integer loyalty = null;

		public Builder() {}

		public Builder setName(String name) {
			this.name = Objects.requireNonNull(name);
			return this;
		}

		public Builder setManaCost(ManaCost manaCost) {
			this.manaCost = Objects.requireNonNull(manaCost);
			return this;
		}

		public Builder setColorIndicator(@Nullable ImmutableSet<Color> colorIndicator) {
			this.colorIndicator = colorIndicator;
			return this;
		}

		public Builder setSupertypes(ImmutableSet<Supertype> supertypes) {
			this.supertypes = Objects.requireNonNull(supertypes);
			return this;
		}

		public Builder setTypes(ImmutableSet<Type> types) {
			this.types = Objects.requireNonNull(types);
			return this;
		}

		public Builder setSubtypes(ImmutableSet<String> subtypes) {
			this.subtypes = Objects.requireNonNull(subtypes);
			return this;
		}

		public Builder setText(String text) {
			this.text = Objects.requireNonNull(text);
			return this;
		}

		public Builder setPower(@Nullable Expression power) {
			this.power = power;
			return this;
		}

		public Builder setToughness(@Nullable Expression toughness) {
			this.toughness = toughness;
			return this;
		}

		public Builder setLoyalty(@Nullable Integer loyalty) {
			this.loyalty = loyalty;
			return this;
		}

		@Override Card build() {
			return new Card(this);
		}

		@Override CardLink newLink(Card partial, int index) {
			return new CardLink(partial, index);
		}
	}

}
