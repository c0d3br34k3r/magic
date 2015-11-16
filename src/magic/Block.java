package magic;

import java.util.Objects;

import org.joda.time.LocalDate;

import com.google.common.collect.ImmutableList;

public final class Block implements Comparable<Block> {

	private final String name;
	private final LocalDate date;
	private final ImmutableList<Expansion> expansions;

	public Block(String name, 
			LocalDate date,
			ImmutableList<Expansion> expansions) {
		this.name = Objects.requireNonNull(name);
		this.date = Objects.requireNonNull(date);
		this.expansions = Objects.requireNonNull(expansions);
	}

	public String name() {
		return name;
	}

	public LocalDate date() {
		return date;
	}

	public ImmutableList<Expansion> expansions() {
		return expansions;
	}

	@Override public String toString() {
		return name;
	}

	@Override public int compareTo(Block o) {
		return date.compareTo(o.date);
	}

}
