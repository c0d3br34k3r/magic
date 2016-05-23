package magic.misc;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import magic.Card;
//import magic.Expansion;
import magic.WholeCard;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedMap.Builder;
import com.google.common.collect.Iterables;
import com.google.gson.stream.JsonReader;

public class MiniDatabase {

	private final ImmutableSortedMap<String, WholeCard> cards;
//	private final ImmutableSortedMap<String, Expansion> expansions;

	public MiniDatabase(String filename) throws IOException {
		this(Paths.get(filename));
	}

	public MiniDatabase(Path path) throws IOException {
		Builder<String, WholeCard> cardBuilder =
				ImmutableSortedMap.orderedBy(String.CASE_INSENSITIVE_ORDER);
//		Builder<String, Expansion> expansionBuilder =
//				ImmutableSortedMap.orderedBy(String.CASE_INSENSITIVE_ORDER);
		try (JsonReader in = new JsonReader(Files.newBufferedReader(path, StandardCharsets.UTF_8))) {
//			in.beginArray();
			for (WholeCard card : JsonCardConverter.readCards(in)) {
				cardBuilder.put(Diacritics.remove(card.name()), card);
			}
			cards = cardBuilder.build();
//			for (Expansion expansion : JsonExpansionConverter.readExpansions(in, cards)) {
//				expansionBuilder.put(expansion.code(), expansion);
//			}
//			expansions = expansionBuilder.build();
//			JsonBlockConverter.readBlocks(in, expansions);
//			in.endArray();
		}
	}

	public WholeCard card(String name) {
		return cards.get(name);
	}

	public Collection<WholeCard> wholeCards() {
		return cards.values();
	}

	public Iterable<Card> cards() {
		return Iterables.concat(cards.values());
	}
	
//	public Expansion expansion(String code) {
//		return expansions.get(code);
//	}
	
//	public Collection<Expansion> expansions() {
//		return expansions.values();
//	}

	public Collection<WholeCard> readCards(String filename) throws IOException {
		return readCards(Paths.get(filename));
	}

	public Collection<WholeCard> readCards(Path path) throws IOException {
		List<WholeCard> cards = new ArrayList<>();
		List<String> notFound = new ArrayList<>();
		for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
			line = line.trim();
			if (!(line.isEmpty() || line.startsWith("#"))) {
				WholeCard card = card(line);
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

	public static void writeCards(String filename, Collection<WholeCard> cards) throws IOException {
		writeCards(Paths.get(filename), cards);
	}

	public static void writeCards(Path path, Collection<WholeCard> cards) throws IOException {
		try (BufferedWriter out = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			for (WholeCard card : cards) {
				out.write(Diacritics.remove(card.name()));
				out.newLine();
			}
		}
	}

}
