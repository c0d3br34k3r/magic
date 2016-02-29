package magic;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Objects;

import com.google.common.collect.Iterators;

public abstract class WholePrinting extends Whole<Printing> {

	private final WholeCard card;
	private final Expansion expansion;
	private final boolean isTimeshifted;
	private final Rarity rarity;

	private WholePrinting(Builder builder) {
		this.card = builder.card;
		this.expansion = builder.expansion;
		this.rarity = builder.rarity;
		this.isTimeshifted = builder.isTimeshifted;
	}

	@Override public abstract PrintingPair pair();

	public final WholeCard card() {
		return card;
	}

	public final Expansion expansion() {
		return expansion;
	}

	public final Rarity rarity() {
		return rarity;
	}

	public final boolean isTimeshifted() {
		return isTimeshifted;
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

		@Override public String toString() {

			return card().toString();
		}

	}

	private static final class CompositePrinting extends WholePrinting {

		private final PrintingPair printings;

		private CompositePrinting(Builder builder) {
			super(builder);
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

		private boolean isTimeshifted = false;
		private Rarity rarity;
		private Expansion expansion;
		private WholeCard card;
		private Printing.Builder only;
		private PrintingPair.Builder pair;

		private Builder() {}

		public Builder setCard(WholeCard card) {
			this.card = Objects.requireNonNull(card);
			return this;
		}

		public Builder setTimeshifted(boolean isTimeshifted) {
			this.isTimeshifted = isTimeshifted;
			return this;
		}

		public Builder setRarity(Rarity rarity) {
			this.rarity = Objects.requireNonNull(rarity);
			return this;
		}

		void setExpansion(Expansion expansion) {
			this.expansion = expansion;
		}

		public Builder setOnly(Printing.Builder only) {
			this.only = Objects.requireNonNull(only);
			return this;
		}

		public Builder setPair(PrintingPair.Builder pair) {
			this.pair = Objects.requireNonNull(pair);
			return this;
		}

		public WholePrinting build() {
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
