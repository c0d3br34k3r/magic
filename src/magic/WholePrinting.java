package magic;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;

import com.google.common.collect.Iterators;

import magic.base.Whole;

public abstract class WholePrinting extends Whole<Printing>
		implements Iterable<Printing> {

	private final WholeCard card;
	private final Expansion expansion;
	private final boolean isTimeshifted;
	private final Rarity rarity;

	private WholePrinting(WholeCard card,
			Expansion expansion,
			Rarity rarity,
			boolean isTimeshifted) {
		this.card = card;
		this.expansion = expansion;
		this.rarity = rarity;
		this.isTimeshifted = isTimeshifted;
	}

	public WholeCard card() {
		return card;
	}

	public Expansion expansion() {
		return expansion;
	}

	public Rarity rarity() {
		return rarity;
	}

	public boolean isTimeshifted() {
		return isTimeshifted;
	}

	public abstract boolean isStandalone();

	public abstract Printing printing();

	public abstract PrintingPair printings();

	public void print() {
		try {
			writeTo(System.out);
		} catch (IOException impossible) {
			throw new AssertionError(impossible);
		}
	}

	public abstract void writeTo(PrintStream out) throws IOException;

	private final class StandalonePrinting extends WholePrinting {

		private final Printing printing;

		@Override public boolean isStandalone() {
			return true;
		}

		@Override public Printing printing() {
			return printing;
		}

		@Override public PrintingPair printings() {
			throw new IllegalStateException();
		}

		@Override public void writeTo(PrintStream out) throws IOException {
			printing.writeTo(out);
		}

		@Override public Iterator<Printing> iterator() {
			return Iterators.singletonIterator(printing);
		}

	}

	private final class CompositePrinting extends WholePrinting {

		private final PrintingPair printings;

		@Override public boolean isStandalone() {
			return false;
		}

		@Override public Printing printing() {
			throw new IllegalStateException();
		}

		@Override public PrintingPair printings() {
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
			return Iterators.forArray(printings.first(), printings.second());
		}
	}

}
