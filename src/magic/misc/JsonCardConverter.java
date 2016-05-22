package magic.misc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import magic.Card;
import magic.CardPair;
import magic.Color;
import magic.Expression;
import magic.Layout;
import magic.ManaCost;
import magic.Supertype;
import magic.Type;
import magic.WholeCard;
import magic.WholeCard.Builder;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class JsonCardConverter {

	private static final String NAME = "name";
	private static final String MANA_COST = "manaCost";
	private static final String COLOR_INDICATOR = "colorIndicator";
	// private static final String COLORLESS = "colorless";
	private static final String SUPERTYPES = "supertypes";
	private static final String TYPES = "types";
	private static final String SUBTYPES = "subtypes";
	private static final String TEXT = "text";
	private static final String POWER = "power";
	private static final String TOUGHNESS = "toughness";
	private static final String LOYALTY = "loyalty";
	private static final String COLOR_IDENTITY = "colorIdentity";
	private static final String ONLY = "only";
	private static final String PAIR = "pair";
	private static final String LAYOUT = "layout";
	private static final String PARTS = "parts";

	public static void writeCards(JsonWriter out, Iterable<WholeCard> cards) throws IOException {
		out.beginArray();
		for (WholeCard card : cards) {
			writeCard(out, card);
		}
		out.endArray();
	}

	public static void writeCard(JsonWriter out, WholeCard wholeCard)
			throws IOException {
		out.beginObject();
		if (!wholeCard.colorIdentity().isEmpty()) {
			out.name(COLOR_IDENTITY);
			writeEnums(out, wholeCard.colorIdentity());
		}
		if (wholeCard.hasOnePart()) {
			out.name(ONLY);
			writePartial(out, wholeCard.only());
		} else {
			CardPair pair = wholeCard.pair();
			out.name(PAIR).beginObject()
					.name(LAYOUT).value(pair.layout().name())
					.name(PARTS).beginArray();
			writePartial(out, pair.first());
			writePartial(out, pair.second());
			out.endArray().endObject();
		}
		out.endObject();
	}

	private static void writePartial(JsonWriter out, Card card)
			throws IOException {
		out.beginObject();
		out.name(NAME).value(card.name());
		if (!card.manaCost().isEmpty()) {
			out.name(MANA_COST).value(card.manaCost().toString());
		}
		if (card.colorIndicator() != null) {
			out.name(COLOR_INDICATOR);
			writeEnums(out, card.colorIndicator());
		}
		if (!card.supertypes().isEmpty()) {
			out.name(SUPERTYPES);
			writeEnums(out, card.supertypes());
		}
		out.name(TYPES);
		writeEnums(out, card.types());
		if (!card.subtypes().isEmpty()) {
			out.name(SUBTYPES);
			writeStrings(out, card.subtypes());
		}
		if (!card.text().isEmpty()) {
			out.name(TEXT).value(card.text());
		}
		if (card.loyalty() != null) {
			out.name(LOYALTY).value(card.loyalty());
		}
		if (card.power() != null) {
			out.name(POWER).value(card.power().toString());
		}
		if (card.toughness() != null) {
			out.name(TOUGHNESS).value(card.toughness().toString());
		}
		out.endObject();
	}

	private static <E extends Enum<E>> void writeEnums(JsonWriter out, Set<E> enums)
			throws IOException {
		out.beginArray();
		for (Enum<E> e : enums) {
			out.value(e.name());
		}
		out.endArray();
	}

	private static void writeStrings(JsonWriter out, Set<String> strings) throws IOException {
		out.beginArray();
		for (String s : strings) {
			out.value(s);
		}
		out.endArray();
	}

	public static Collection<WholeCard> readCards(JsonReader in) throws IOException {
		List<WholeCard> cards = new ArrayList<>();
		in.beginArray();
		while (in.hasNext()) {
			cards.add(readCard(in));
		}
		in.endArray();
		return cards;
	}

	public static WholeCard readCard(JsonReader in) throws IOException {
		Builder builder = WholeCard.builder();
		in.beginObject();
		while (in.hasNext()) {
			String key = in.nextName();
			switch (key) {
				case COLOR_IDENTITY:
					builder.setColorIdentity(readEnums(in, Color.class));
					break;
				case ONLY:
					builder.setOnly(readPartial(in));
					break;
				case PAIR:
					builder.setPair(readPair(in));
					break;
				default:
					throw new IllegalArgumentException(key);
			}
		}
		in.endObject();
		return builder.build();
	}

	private static Card.Builder readPartial(JsonReader in)
			throws IOException {
		Card.Builder builder = Card.builder();
		in.beginObject();
		while (in.hasNext()) {
			String key = in.nextName();
			switch (key) {
				case NAME:
					builder.setName(in.nextString());
					break;
				case MANA_COST:
					builder.setManaCost(ManaCost.parse(in.nextString()));
					break;
				case COLOR_INDICATOR:
					builder.setColorIndicator(readEnums(in, Color.class));
					break;
				case SUPERTYPES:
					builder.setSupertypes(readEnums(in, Supertype.class));
					break;
				case TYPES:
					builder.setTypes(readEnums(in, Type.class));
					break;
				case SUBTYPES:
					builder.setSubtypes(readStrings(in));
					break;
				case TEXT:
					builder.setText(in.nextString());
					break;
				case LOYALTY:
					builder.setLoyalty(in.nextInt());
					break;
				case POWER:
					builder.setPower(Expression.of(in.nextString()));
					break;
				case TOUGHNESS:
					builder.setToughness(Expression.of(in.nextString()));
					break;
				default:
					throw new IllegalArgumentException(key);
			}
		}
		in.endObject();
		return builder;
	}

	private static CardPair.Builder readPair(JsonReader in) throws IOException {
		CardPair.Builder builder = CardPair.builder();
		in.beginObject();
		while (in.hasNext()) {
			String key = in.nextName();
			switch (key) {
				case LAYOUT:
					builder.setLayout(Layout.valueOf(in.nextString()));
					break;
				case PARTS:
					in.beginArray();
					builder.setFirst(readPartial(in));
					builder.setSecond(readPartial(in));
					in.endArray();
					break;
				default:
					throw new IllegalArgumentException(key);
			}
		}
		in.endObject();
		return builder;
	}

	private static <E extends Enum<E>> ImmutableSet<E> readEnums(JsonReader in,
			Class<E> type) throws IOException {
		EnumSet<E> enums = EnumSet.noneOf(type);
		in.beginArray();
		while (in.hasNext()) {
			enums.add(Enum.valueOf(type, in.nextString()));
		}
		in.endArray();
		return Sets.immutableEnumSet(enums);
	}

	private static ImmutableSet<String> readStrings(JsonReader in)
			throws IOException {
		ImmutableSet.Builder<String> builder = ImmutableSet.builder();
		in.beginArray();
		while (in.hasNext()) {
			builder.add(in.nextString());
		}
		in.endArray();
		return builder.build();
	}

}
