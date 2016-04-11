package magic;

import java.util.Objects;

import org.joda.time.LocalDate;

import com.google.common.collect.ImmutableList;

public final class Block implements Comparable<Block> {

	private final String name;
	private final ImmutableList<Expansion> expansions;
	private final LocalDate date;

	public Block(String name, ImmutableList<Expansion> expansions) {
		this.name = Objects.requireNonNull(name);
		this.expansions = Objects.requireNonNull(expansions);
		this.date = expansions.get(0).releaseDate();
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
