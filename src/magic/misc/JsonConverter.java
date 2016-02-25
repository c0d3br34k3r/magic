package magic.misc;

import static magic.misc.JsonKeys.COLOR_IDENTITY;
import static magic.misc.JsonKeys.COLOR_OVERRIDE;
import static magic.misc.JsonKeys.LAYOUT;
import static magic.misc.JsonKeys.LOYALTY;
import static magic.misc.JsonKeys.MANA_COST;
import static magic.misc.JsonKeys.NAME;
import static magic.misc.JsonKeys.ONLY;
import static magic.misc.JsonKeys.PAIR;
import static magic.misc.JsonKeys.PARTS;
import static magic.misc.JsonKeys.POWER;
import static magic.misc.JsonKeys.SUBTYPES;
import static magic.misc.JsonKeys.SUPERTYPES;
import static magic.misc.JsonKeys.TEXT;
import static magic.misc.JsonKeys.TOUGHNESS;
import static magic.misc.JsonKeys.TYPES;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import magic.Card;
import magic.CardPair;
import magic.Color;
import magic.Expression;
import magic.ManaCost;
import magic.Supertype;
import magic.Type;
import magic.WholeCard;
import magic.WholeCard.Builder;

public class JsonConverter {

	public static void writeCard(JsonWriter out, WholeCard wholeCard)
			throws IOException {
		out.beginObject();
		out.name(COLOR_IDENTITY);
		writeEnums(out, wholeCard.colorIdentity());
		if (wholeCard.hasOnePart()) {
			out.name(ONLY);
			writePartial(out, wholeCard.only());
		} else {
			CardPair pair = wholeCard.pair();
			out.name(PAIR);
			out.beginObject();
			out.name(LAYOUT).value(pair.layout().name());
			out.name(PARTS).beginArray();
			writePartial(out, pair.first());
			writePartial(out, pair.second());
			out.endArray();
			out.endObject();
		}
		out.endArray();
		out.endObject();
	}

	private static void writePartial(JsonWriter out, Card card)
			throws IOException {
		out.beginObject();
		out.name(NAME).value(card.name());
		out.name(MANA_COST).value(card.manaCost().toString());
		if (card.colorOverride() != null) {
			out.name(COLOR_OVERRIDE);
			writeEnums(out, card.colorOverride());
		}
		out.name(SUPERTYPES);
		writeEnums(out, card.supertypes());
		out.name(TYPES);
		writeEnums(out, card.types());
		out.name(SUBTYPES);
		writeStrings(out, card.subtypes());
		out.name(TEXT).value(card.text());
		if (card.loyalty() != null) {
			out.name(LOYALTY).value(card.loyalty());
		}
		if (card.power() != null) {
			out.name(POWER).value(card.power().toString());
		}
		if (card.toughness() != null) {
			out.name(TOUGHNESS).value(card.loyalty().toString());
		}
		out.endObject();
	}

	private static <E extends Enum<E>> void writeEnums(JsonWriter out,
			Set<E> enums) throws IOException {
		out.beginArray();
		for (Enum<E> e : enums) {
			out.value(e.name());
		}
		out.endArray();
	}

	private static void writeStrings(JsonWriter out,
			Set<String> strings) throws IOException {
		out.beginArray();
		for (String s : strings) {
			out.value(s);
		}
		out.endArray();
	}

	public static void readCard(JsonReader in) throws IOException {
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
				case COLOR_OVERRIDE:
					builder.setColorOverride(readEnums(in, Color.class));
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

	private static magic.CardPair.Builder readPair(JsonReader in) {
		// TODO Auto-generated method stub
		return null;
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
