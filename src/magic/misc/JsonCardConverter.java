package magic.misc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import magic.Card;
import magic.Card.Builder;
import magic.Characteristics;
import magic.Color;
import magic.Expression;
import magic.Layout;
import magic.ManaCost;
import magic.Pair;
import magic.Supertype;
import magic.Type;

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

	public static void writeCards(JsonWriter out, Iterable<Card> cards) throws IOException {
		out.beginArray();
		for (Card card : cards) {
			writeCard(out, card);
		}
		out.endArray();
	}

	public static void writeCard(JsonWriter out, Card card) throws IOException {
		out.beginObject();
		out.name(LAYOUT).value(card.layout().name());
		if (!card.colorIdentity().isEmpty()) {
			out.name(COLOR_IDENTITY);
			writeEnums(out, card.colorIdentity());
		}
		if (card.hasOnePart()) {
			out.name(ONLY);
			writeCharacteristics(out, card.only());
		} else {
			Pair<Characteristics> pair = card.pair();
			out.name(PAIR).beginArray();
			writeCharacteristics(out, pair.first());
			writeCharacteristics(out, pair.second());
			out.endArray();
		}
		out.endObject();
	}

	private static void writeCharacteristics(JsonWriter out, Characteristics characs)
			throws IOException {
		out.beginObject();
		out.name(NAME).value(characs.name());
		System.out.println(characs.whole());
		if (!characs.manaCost().isEmpty()
				&& !(characs.whole().layout() == Layout.FLIP && !characs.link().isFirst())) {
			out.name(MANA_COST).value(characs.manaCost().toString());
		}
		if (characs.colorIndicator() != null) {
			out.name(COLOR_INDICATOR);
			writeEnums(out, characs.colorIndicator());
		}
		if (!characs.supertypes().isEmpty()) {
			out.name(SUPERTYPES);
			writeEnums(out, characs.supertypes());
		}
		out.name(TYPES);
		writeEnums(out, characs.types());
		if (!characs.subtypes().isEmpty()) {
			out.name(SUBTYPES);
			writeStrings(out, characs.subtypes());
		}
		if (!characs.text().isEmpty()) {
			out.name(TEXT).value(characs.text());
		}
		if (characs.loyalty() != null) {
			out.name(LOYALTY).value(characs.loyalty());
		}
		if (characs.power() != null) {
			out.name(POWER).value(characs.power().toString());
			out.name(TOUGHNESS).value(characs.toughness().toString());
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

	public static Collection<Card> readCards(JsonReader in) throws IOException {
		List<Card> cards = new ArrayList<>();
		in.beginArray();
		while (in.hasNext()) {
			cards.add(readCard(in));
		}
		in.endArray();
		return cards;
	}

	public static Card readCard(JsonReader in) throws IOException {
		Builder builder = Card.builder();
		in.beginObject();
		while (in.hasNext()) {
			String key = in.nextName();
			switch (key) {
				case LAYOUT:
					builder.setLayout(Layout.valueOf(in.nextString()));
					break;
				case COLOR_IDENTITY:
					builder.setColorIdentity(readEnums(in, Color.class));
					break;
				case ONLY:
					builder.setOnly(readCharacteristics(in));
					break;
				case PAIR:
					in.beginArray();
					builder.setPair(readCharacteristics(in), readCharacteristics(in));
					in.endArray();
					break;
				default:
					throw new IllegalArgumentException(key);
			}
		}
		in.endObject();
		return builder.build();
	}

	private static Characteristics.Builder readCharacteristics(JsonReader in)
			throws IOException {
		Characteristics.Builder builder = Characteristics.builder();
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
