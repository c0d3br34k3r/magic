package magic;

import java.io.IOException;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class Card implements Comparable<Card> {

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

		if (builder.isColorless) {
			colors = ImmutableSet.of();
		} else if (!colorIndicator.isEmpty()) {
			colors = colorIndicator;
		} else {
			colors = manaCost.colors();
		}
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

	@Override
	public int compareTo(Card other) {
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

	public static class Builder {

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

		Card build() {
			return new Card(this);
		}
	}

}
