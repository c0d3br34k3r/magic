package magic.misc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import magic.Card;
import magic.Expansion;
import magic.Printing;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.ImmutableSortedSet.Builder;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.TreeMultiset;

public abstract class Database {

	public abstract Collection<? extends Card> cards();

	public abstract Collection<? extends Expansion> expansions();

	public abstract Card getCard(String name);

	public abstract Expansion getExpansion(String name);

	public ImmutableSortedSet<Expansion> getBlock(String blockName) {
		Builder<Expansion> builder = ImmutableSortedSet.naturalOrder();
		for (Expansion expansion : expansions()) {
			if (blockName.equals(expansion.blockName())) {
				builder.add(expansion);
			}
		}
		return builder.build();
	}

	public ListMultimap<Card, Printing> printingsIn(String expansionName) {
		return printingsIn(getExpansion(expansionName));
	}

	public ListMultimap<Card, Printing> printingsIn(Expansion expansion) {
		ImmutableListMultimap.Builder<Card, Printing> builder = ImmutableListMultimap.builder();
		for (Card card : cards()) {
			builder.putAll(card, card.printings().get(expansion));
		}
		return builder.build();
	}

	public Set<Expansion> getExpansions(String... codes) {
		ImmutableSet.Builder<Expansion> builder = ImmutableSet.builder();
		for (String code : codes) {
			builder.add(getExpansion(code));
		}
		return builder.build();
	}

	public Set<Card> cardsIn(String... expansionCodes) {
		List<Expansion> expansions = new ArrayList<>();
		for (String code : expansionCodes) {
			expansions.add(getExpansion(code));
		}
		return cardsIn(expansions);
	}

	public Set<Card> cardsIn(Collection<Expansion> expansions) {
		ImmutableSortedSet.Builder<Card> builder = ImmutableSortedSet.naturalOrder();
		for (Expansion expansion : expansions) {
			builder.addAll(printingsIn(expansion).keySet());
		}
		ImmutableSet<Card> result = builder.build();
		return result;
	}

	public Collection<Card> readCards(Path path) throws IOException {
		Collection<Card> cards = new ArrayList<>();
		Collection<String> notFound = new ArrayList<>();
		try (BufferedReader in = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			for (;;) {
				String line = in.readLine();
				if (line == null) {
					break;
				}
				line = line.trim();
				if (!(line.isEmpty() || line.startsWith("#"))) {
					Card card = getCard(line);
					if (card == null) {
						notFound.add(line);
					}
					cards.add(card);
				}
			}
		}
		if (notFound.isEmpty()) {
			return cards;
		}
		throw new IllegalArgumentException("Cards not found: " + notFound);
	}

	private static final Pattern DECK_LINE = Pattern.compile("(\\d{0,3})\\s+(.+)");

	public Multiset<Card> readDeck(Path path) throws IOException {
		return readDeck(path, TreeMultiset.<Card> create());
	}

	public Multiset<Card> readDeck(Path path, Multiset<Card> deck) throws IOException {
		Collection<String> notFound = new ArrayList<>();
		try (BufferedReader in = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			for (;;) {
				String line = in.readLine();
				if (line == null) {
					break;
				}
				line = line.trim();
				if (!(line.isEmpty() || line.startsWith("#"))) {
					Matcher matcher = DECK_LINE.matcher(line);
					String cardName;
					int count;
					if (matcher.matches()) {
						count = Integer.parseInt(matcher.group(1));
						cardName = matcher.group(2);
					} else {
						count = 1;
						cardName = line;
					}
					Card card = getCard(cardName);
					if (card != null) {
						deck.setCount(card, count);
					} else {
						notFound.add(cardName);
					}
				}
			}
		}
		if (notFound.isEmpty()) {
			return deck;
		}
		throw new IllegalArgumentException("Cards not found: " + notFound);
	}

	public static void writeCards(Path path, Collection<? extends Card> cards) throws IOException {
		try (BufferedWriter out = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			for (Card card : cards) {
				out.write(card.name());
				out.newLine();
			}
		}
	}

	public static void writeDeck(Path path, Multiset<? extends Card> cards) throws IOException {
		try (BufferedWriter out = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			for (Entry<? extends Card> entry : cards.entrySet()) {
				out.write(Integer.toString(entry.getCount()));
				out.write(' ');
				out.write(entry.getElement().toString());
				out.newLine();
			}
		}
	}

	public ListMultimap<Card, Printing> printingsIn(Collection<Expansion> expansions) {
		ArrayListMultimap<Card, Printing> printings = ArrayListMultimap.create();
		for (Expansion expansion : expansions) {
			printings.putAll(printingsIn(expansion));
		}
		return printings;
	}

}
