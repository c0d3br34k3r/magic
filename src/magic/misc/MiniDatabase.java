package magic.misc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedMap.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.stream.JsonReader;

import magic.FullCharacteristics;
import magic.Expansion;
import magic.Card;
import magic.WholePrinting;

public class MiniDatabase {

	private final ImmutableSortedMap<String, Card> cards;
	private final ImmutableSortedMap<String, Expansion> expansions;
	private final ImmutableSortedMap<Card, ImmutableListMultimap<Expansion, WholePrinting>> printings;

	public MiniDatabase(String filename) throws IOException {
		this(Paths.get(filename));
	}

	public MiniDatabase(Path path) throws IOException {
		Builder<String, Card> cardBuilder =
				ImmutableSortedMap.orderedBy(String.CASE_INSENSITIVE_ORDER);
		Builder<String, Expansion> expansionBuilder =
				ImmutableSortedMap.orderedBy(String.CASE_INSENSITIVE_ORDER);
		try (JsonReader in =
				new JsonReader(Files.newBufferedReader(path, StandardCharsets.UTF_8))) {
			in.beginArray();
			for (Card card : JsonCardConverter.readCards(in)) {
				cardBuilder.put(Diacritics.remove(card.name()), card);
			}
			cards = cardBuilder.build();
			for (Expansion expansion : JsonExpansionConverter.readExpansions(in, cards)) {
				expansionBuilder.put(expansion.code(), expansion);
			}
			expansions = expansionBuilder.build();
			printings = JsonExpansionConverter.readPrintings(in, cards, expansions);
			in.endArray();
		}
	}

	public Card card(String name) {
		return cards.get(name);
	}

	public Collection<Card> wholeCards() {
		return cards.values();
	}

	public Iterable<FullCharacteristics> cards() {
		return Iterables.concat(cards.values());
	}

	public Expansion expansion(String code) {
		return expansions.get(code);
	}

	public Collection<Expansion> expansions() {
		return expansions.values();
	}

	public Iterable<WholePrinting> printings() {
		final Iterator<ImmutableListMultimap<Expansion, WholePrinting>> iter =
				printings.values().iterator();
		return new Iterable<WholePrinting>() {

			@Override public Iterator<WholePrinting> iterator() {
				return new AbstractIterator<WholePrinting>() {

					Iterator<WholePrinting> current = ImmutableSet.<WholePrinting> of().iterator();

					@Override protected WholePrinting computeNext() {
						while (!current.hasNext()) {
							if (!iter.hasNext()) {
								return endOfData();
							}
							current = iter.next().values().iterator();
						}
						return current.next();
					}
				};
			}
		};
	}

	public Collection<Card> readCards(String filename) throws IOException {
		return readCards(Paths.get(filename));
	}

	public Collection<Card> readCards(Path path) throws IOException {
		List<Card> cards = new ArrayList<>();
		List<String> notFound = new ArrayList<>();
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

	public static void writeCards(String filename, Collection<Card> cards) throws IOException {
		writeCards(Paths.get(filename), cards);
	}

	public static void writeCards(Path path, Collection<Card> cards) throws IOException {
		try (BufferedWriter out = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			for (Card card : cards) {
				out.write(Diacritics.remove(card.name()));
				out.newLine();
			}
		}
	}

}
