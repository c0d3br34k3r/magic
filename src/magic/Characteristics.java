package magic;

import java.io.IOException;
import java.util.Objects;

import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;

public abstract class Characteristics implements Comparable<Characteristics> {

	private final Card whole;
	private final @Nullable CardLink link;
	private final String name;
	private final ImmutableSet<Supertype> supertypes;
	private final ImmutableSet<Type> types;
	private final ImmutableSet<String> subtypes;
	private final String text;
	private final @Nullable Expression power;
	private final @Nullable Expression toughness;

	Characteristics(Builder builder) {
		this.whole = builder.getWhole();
		this.link = builder.buildLink(this);
		this.name = Objects.requireNonNull(builder.name);
		this.supertypes = builder.supertypes;
		this.types = Objects.requireNonNull(builder.types);
		this.subtypes = builder.subtypes;
		this.text = builder.text;
		this.power = builder.power;
		this.toughness = builder.toughness;
	}

	public Card whole() {
		return whole;
	}

	public CardLink link() {
		return link;
	}

	public String name() {
		return name;
	}

	public abstract ManaCost manaCost();

	public abstract @Nullable ImmutableSet<Color> colorIndicator();

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

	public abstract @Nullable Integer loyalty();

	public ImmutableSet<Color> colors() {
		return MoreObjects.firstNonNull(colorIndicator(), manaCost().colors());
	}

	@Override public int compareTo(Characteristics other) {
		return name.compareToIgnoreCase(other.name);
	}

	private static final Joiner SPACE_JOINER = Joiner.on(' ');

	public void writeTo(Appendable out) throws IOException {
		out.append(name);
		if (!manaCost().isEmpty()) {
			out.append(' ').append(manaCost().toString());
		}
		out.append('\n');
		if (colorIndicator() != null && !colorIndicator().isEmpty()) {
			out.append('(');
			for (Color color : colorIndicator()) {
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
			out.append('\n').append(text);
		}
		if (power != null) {
			out.append('\n').append(power.toString()).append('/')
					.append(toughness.toString());
		} else if (loyalty() != null) {
			out.append('\n').append(Integer.toString(loyalty()));
		}
		out.append('\n');
	}

	public void print() {
		try {
			writeTo(System.out);
		} catch (IOException impossible) {
			throw new AssertionError(impossible);
		}
	}

	@Override public String toString() {
		return name;
	}

	private static final class FullCharacteristics extends Characteristics {

		private final ManaCost manaCost;
		private final @Nullable ImmutableSet<Color> colorIndicator;
		private final @Nullable Integer loyalty;

		private FullCharacteristics(Characteristics.Builder builder) {
			super(builder);
			this.manaCost = builder.manaCost;
			this.colorIndicator = builder.colorIndicator;
			this.loyalty = builder.loyalty;
		}

		@Override public ManaCost manaCost() {
			return manaCost;
		}

		@Override public @Nullable ImmutableSet<Color> colorIndicator() {
			return colorIndicator;
		}

		@Override public @Nullable Integer loyalty() {
			return loyalty;
		}

		@Override public String toString() {
			return name();
		}
	}

	private static class FlippedCharacteristics extends Characteristics {

		public FlippedCharacteristics(Characteristics.Builder builder) {
			super(builder);
		}

		@Override public ManaCost manaCost() {
			return link().get().manaCost();
		}

		@Override public ImmutableSet<Color> colorIndicator() {
			return null;
		}

		@Override public Integer loyalty() {
			return null;
		}
	}

	public static Builder builder() {
		return new Builder(false);
	}

	public static class Builder extends PartialBuilder<Characteristics, Card, CardLink> {

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
		private final boolean flippedSide;

		public Builder(boolean flippedSide) {
			this.flippedSide = flippedSide;
		}

		public void setName(String name) {
			this.name = Objects.requireNonNull(name);
		}

		public void setManaCost(ManaCost manaCost) {
			this.manaCost = Objects.requireNonNull(manaCost);
		}

		public void setColorIndicator(@Nullable ImmutableSet<Color> colorIndicator) {
			this.colorIndicator = colorIndicator;
		}

		public void setSupertypes(ImmutableSet<Supertype> supertypes) {
			this.supertypes = Objects.requireNonNull(supertypes);
		}

		public void setTypes(ImmutableSet<Type> types) {
			this.types = Objects.requireNonNull(types);
		}

		public void setSubtypes(ImmutableSet<String> subtypes) {
			this.subtypes = Objects.requireNonNull(subtypes);
		}

		public void setText(String text) {
			this.text = Objects.requireNonNull(text);
		}

		public void setPower(@Nullable Expression power) {
			this.power = power;
		}

		public void setToughness(@Nullable Expression toughness) {
			this.toughness = toughness;
		}

		public void setLoyalty(@Nullable Integer loyalty) {
			this.loyalty = loyalty;
		}

		@Override CardLink newLink(Characteristics partial, int index) {
			return new CardLink(partial, index);
		}

		@Override Characteristics build() {
			if (flippedSide) {
				return new FlippedCharacteristics(this);
			}
			return new FullCharacteristics(this);
		}
	}

}
