package magic;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterators;

public abstract class WholePrinting implements Comparable<WholePrinting>, Iterable<Printing> {

	private final Card card;
	private final Expansion expansion;
	private final Rarity rarity;
	private final int variation;
	private final boolean isTimeshifted;

	private WholePrinting(Builder builder) {
		builder.first.setWhole(this);
		this.card = Objects.requireNonNull(builder.card);
		this.expansion = Objects.requireNonNull(builder.expansion);
		this.rarity = Objects.requireNonNull(builder.rarity);
		this.variation = builder.variation;
		this.isTimeshifted = builder.isTimeshifted;
	}

	public abstract boolean hasOnePart();

	public abstract Printing only();

	public abstract Pair<Printing> pair();

	public final Card card() {
		return card;
	}

	public final Expansion expansion() {
		return expansion;
	}

	public final Rarity rarity() {
		return rarity;
	}

	public int variation() {
		return variation;
	}

	public final boolean isTimeshifted() {
		return isTimeshifted;
	}

	@Override public String toString() {
		return card().name() + " (" + expansion().code() + ":" + rarity().code() + ")";
	}

	@Override public int compareTo(WholePrinting o) {
		return ComparisonChain.start()
				.compare(card, o.card)
				.compare(expansion, o.expansion)
				.compare(variation, o.variation)
				.result();
	}

	public final void print() {
		try {
			writeTo(System.out);
		} catch (IOException impossible) {
			throw new AssertionError(impossible);
		}
	}

	public abstract void writeTo(PrintStream out) throws IOException;

	public static Builder builder() {
		return new Builder();
	}

	private static final class StandalonePrinting extends WholePrinting {

		private final Printing printing;

		private StandalonePrinting(Builder builder) {
			super(builder);
			builder.first.setCard(builder.card.only());
			this.printing = builder.first.build();
		}

		@Override public boolean hasOnePart() {
			return true;
		}

		@Override public Printing only() {
			return printing;
		}

		@Override public Pair<Printing> pair() {
			throw new IllegalStateException();
		}

		@Override public void writeTo(PrintStream out) throws IOException {
			printing.writeTo(out);
		}

		@Override public Iterator<Printing> iterator() {
			return Iterators.singletonIterator(printing);
		}
	}

	private static final class CompositePrinting extends WholePrinting {

		private final Pair<Printing> printings;

		private CompositePrinting(Builder builder) {
			super(builder);
			builder.second.setWhole(this);
			builder.first.setCard(card().pair().first());
			builder.second.setCard(card().pair().second());
			builder.first.prepareLink(builder.second);
			this.printings = new Pair<>(builder.first.build(), builder.first.getOther());
		}

		@Override public boolean hasOnePart() {
			return false;
		}

		@Override public Printing only() {
			throw new IllegalStateException();
		}

		@Override public Pair<Printing> pair() {
			return printings;
		}

		@Override public void writeTo(PrintStream out) throws IOException {
			printings.first().writeTo(out);
			out.append("* ")
					.append(card().layout().toString().toUpperCase())
					.append(" *")
					.append(System.lineSeparator());
			printings.second().writeTo(out);
		}

		@Override public Iterator<Printing> iterator() {
			return printings.iterator();
		}
	}

	public static class Builder {

		private Card card;
		private Expansion expansion;
		private Rarity rarity;
		private int variation = 0;
		private boolean isTimeshifted = false;
		private Printing.Builder first;
		private Printing.Builder second;

		private Builder() {}

		public void setCard(Card card) {
			this.card = Objects.requireNonNull(card);
		}

		public void setOnly(Printing.Builder only) {
			this.first = Objects.requireNonNull(only);
			this.second = null;
		}
		
		public void setPair(Printing.Builder first, Printing.Builder second) {
			this.first = first;
			this.second = second;
		}

		public void setPair(List<Printing.Builder> pair) {
			this.first = pair.get(0);
			this.second = pair.get(1);
		}

		public void setExpansion(Expansion expansion) {
			this.expansion = Objects.requireNonNull(expansion);
		}

		public Builder setRarity(Rarity rarity) {
			this.rarity = Objects.requireNonNull(rarity);
			return this;
		}

		public Builder setVariation(int variation) {
			this.variation = variation;
			return this;
		}

		public Builder setTimeshifted(boolean isTimeshifted) {
			this.isTimeshifted = isTimeshifted;
			return this;
		}

		public WholePrinting build() {
			if (first == null) {
				throw new IllegalArgumentException();
			}
			if (second == null) {
				return new StandalonePrinting(this);
			}
			return new CompositePrinting(this);
		}
	}

}
