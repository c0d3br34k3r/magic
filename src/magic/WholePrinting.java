package magic;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Objects;

import com.google.common.collect.Iterators;

public abstract class WholePrinting implements Iterable<Printing> {

	private final WholeCard card;
	private final Expansion expansion;
	private final Rarity rarity;
	private final int variation;
	private final boolean isTimeshifted;

	private WholePrinting(Builder builder) {
		this.card = Objects.requireNonNull(builder.card);
		this.expansion = Objects.requireNonNull(builder.expansion);
		this.rarity = Objects.requireNonNull(builder.rarity);
		this.variation = builder.variation;
		this.isTimeshifted = builder.isTimeshifted;
	}

	public abstract boolean hasOnePart();

	public abstract Printing only();

	public abstract PrintingPair pair();

	public final WholeCard card() {
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
			builder.only.setWhole(this);
			builder.only.setCard(builder.card.only());
			this.printing = builder.only.build();
		}

		@Override public boolean hasOnePart() {
			return true;
		}

		@Override public Printing only() {
			return printing;
		}

		@Override public PrintingPair pair() {
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

		private final PrintingPair printings;

		private CompositePrinting(Builder builder) {
			super(builder);
			builder.pair.setCardPair(card().pair());
			this.printings = builder.pair.build(this);
		}

		@Override public boolean hasOnePart() {
			return false;
		}

		@Override public Printing only() {
			throw new IllegalStateException();
		}

		@Override public PrintingPair pair() {
			return printings;
		}

		@Override public void writeTo(PrintStream out) throws IOException {
			printings.first().writeTo(out);
			out.append("* ")
					.append(printings.cards().layout().toString().toUpperCase())
					.append(" *")
					.append(System.lineSeparator());
			printings.second().writeTo(out);
		}

		@Override public Iterator<Printing> iterator() {
			return printings.iterator();
		}
	}

	public static class Builder {

		private WholeCard card;
		private Expansion expansion;
		private Rarity rarity;
		private int variation = 0;
		private boolean isTimeshifted = false;
		private Printing.Builder only;
		private PrintingPair.Builder pair;

		private Builder() {}

		public Builder setCard(WholeCard card) {
			this.card = Objects.requireNonNull(card);
			return this;
		}

		public Builder setOnly(Printing.Builder only) {
			this.only = Objects.requireNonNull(only);
			return this;
		}

		public Builder setPair(PrintingPair.Builder pair) {
			this.pair = Objects.requireNonNull(pair);
			return this;
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

		void setExpansion(Expansion expansion) {
			this.expansion = expansion;
		}

		WholePrinting build() {
			if (!(only == null ^ pair == null)) {
				throw new IllegalArgumentException();
			}
			if (only != null) {
				return new StandalonePrinting(this);
			}
			return new CompositePrinting(this);
		}
	}

}
