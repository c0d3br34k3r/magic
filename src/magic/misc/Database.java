package magic.misc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.TreeMultiset;

import magic.Card;
import magic.Expansion;

public abstract class Database {

	public abstract Collection<? extends Card> cards();

	public abstract Collection<? extends Expansion> expansions();

	public abstract Card card(String name);

	public abstract Expansion expansion(String nameOrCode);

	public abstract Expansion block(String name);

	public Set<Expansion> getExpansions(String... codes) {
		ImmutableSet.Builder<Expansion> builder = ImmutableSet.builder();
		for (String code : codes) {
			builder.add(expansion(code));
		}
		return builder.build();
	}

	public Set<Card> cardsIn(String... expansionCodes) {
		return cardsIn(getExpansions(expansionCodes));
	}

	public Set<Card> cardsIn(Collection<Expansion> expansions) {
		ImmutableSortedSet.Builder<Card> builder = ImmutableSortedSet.naturalOrder();
		for (Expansion expansion : expansions) {
			builder.addAll(Iterables.concat(expansion.cards().keySet()));
		}
		ImmutableSet<Card> result = builder.build();
		return result;
	}

	public Collection<Card> readCards(Path path) throws IOException {
		Collection<Card> cards = new ArrayList<>();
		Collection<String> notFound = new ArrayList<>();
		for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
			line = line.trim();
			if (!(line.isEmpty() || line.startsWith("#"))) {
				Card card = card(line);
				if (card == null) {
					notFound.add(line);
				}
				cards.add(card);
			}
		}
		if (notFound.isEmpty()) {
			return cards;
		}
		throw new IllegalArgumentException("Cards not found: " + notFound);
	}

	private static final Pattern DECK_LINE = Pattern.compile("(\\d+)\\s+(.+)");

	public Multiset<Card> readDeck(Path path) throws IOException {
		return readDeck(path, TreeMultiset.<Card> create());
	}

	public Multiset<Card> readDeck(Path path, Multiset<Card> deck) throws IOException {
		Collection<String> notFound = new ArrayList<>();
		for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
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
				Card card = card(cardName);
				if (card != null) {
					deck.setCount(card, count);
				} else {
					notFound.add(cardName);
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
				out.write(entry.getElement().name());
				out.newLine();
			}
		}
	}

}
