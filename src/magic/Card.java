package magic;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;

public abstract class Card implements Comparable<Card>, Iterable<Characteristics> {

	private final ImmutableSet<Color> colorIdentity;
	private final Layout layout;

	private Card(Builder builder) {
		builder.first.setWhole(this);
		this.colorIdentity = builder.colorIdentity;
		this.layout = Objects.requireNonNull(builder.layout);
	}

	public ImmutableSet<Color> colorIdentity() {
		return colorIdentity;
	}

	public Layout layout() {
		return layout;
	}

	public abstract String name();

	public abstract boolean hasOnePart();

	public abstract Characteristics only();

	public abstract Pair<Characteristics> pair();

	@Override public String toString() {
		return name();
	}

	@Override public int compareTo(Card other) {
		return name().compareToIgnoreCase(other.name());
	}

	public abstract void writeTo(Appendable out) throws IOException;

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

	private static class StandaloneCard extends Card {

		private final Characteristics charcs;

		StandaloneCard(Builder builder) {
			super(builder);
			this.charcs = builder.first.build();
		}

		@Override public String name() {
			return charcs.name();
		}

		@Override public Characteristics only() {
			return charcs;
		}

		@Override public Pair<Characteristics> pair() {
			throw new IllegalStateException();
		}

		@Override public Iterator<Characteristics> iterator() {
			return Iterators.singletonIterator(charcs);
		}

		@Override public void writeTo(Appendable out) throws IOException {
			charcs.writeTo(out);
		}

		@Override public boolean hasOnePart() {
			return true;
		}
	}

	private static class CompositeCard extends Card {

		private final Pair<Characteristics> pair;

		CompositeCard(Builder builder) {
			super(builder);
			builder.second.setWhole(this);
			builder.first.prepareLink(builder.second);
			this.pair = new Pair<Characteristics>(builder.first.build(), builder.first.getOther());
		}

		@Override public String name() {
			return layout().format(pair.get(0), pair.get(1));
		}

		@Override public Characteristics only() {
			throw new IllegalStateException();
		}

		@Override public Pair<Characteristics> pair() {
			return pair;
		}

		@Override public boolean hasOnePart() {
			return false;
		}

		@Override public Iterator<Characteristics> iterator() {
			return pair.iterator();
		}

		@Override public void writeTo(Appendable out) throws IOException {
			pair.first().writeTo(out);
			out.append("* ")
					.append(layout().toString().toUpperCase())
					.append(" *")
					.append('\n');
			pair.second().writeTo(out);
		}
	}

	public static class Builder {

		private Characteristics.Builder first;
		private Characteristics.Builder second;
		public Layout layout;
		private ImmutableSet<Color> colorIdentity = ImmutableSet.of();

		private Builder() {}

		public void setColorIdentity(ImmutableSet<Color> colorIdentity) {
			this.colorIdentity = Objects.requireNonNull(colorIdentity);
		}

		public void setLayout(Layout layout) {
			this.layout = Objects.requireNonNull(layout);
		}

		public void setOnly(Characteristics.Builder only) {
			this.first = only;
			this.second = null;
		}

		public void setPair(Characteristics.Builder first, Characteristics.Builder second) {
			this.first = first;
			this.second = second;
		}

		public void setPair(List<Characteristics.Builder> pair) {
			this.first = pair.get(0);
			this.second = pair.get(1);
		}

		public Card build() {
			if (first == null) {
				throw new IllegalArgumentException();
			}
			if (second == null) {
				return new StandaloneCard(this);
			}
			return new CompositeCard(this);			
		}
	}

}
