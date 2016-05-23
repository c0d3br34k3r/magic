package magic.misc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import magic.CollectorNumber;
import magic.Expansion;
import magic.Expansion.BorderColor;
import magic.Expansion.ReleaseType;
import magic.Printing;
import magic.PrintingPair;
import magic.Rarity;
import magic.WholeCard;
import magic.WholePrinting;

public class JsonExpansionConverter {

	private static final String NAME = "name";
	private static final String CODE = "code";
	private static final String RELEASE_DATE = "releaseDate";
	private static final String TYPE = "type";
	private static final String BORDER_COLOR = "borderColor";
	private static final String SIZE = "size";

	private static final String EXPANSION = "expansion";
	private static final String RARITY = "rarity";
	private static final String TIMESHIFTED = "timeshifted";
	private static final String ONLY = "only";
	private static final String PAIR = "pair";

	private static final String FLAVOR_TEXT = "flavorText";
	private static final String ARTIST = "artist";
	private static final String COLLECTOR_NUMBER = "collectorNumber";
	private static final String WATERMARK = "watermark";

	public static void writeExpansions(JsonWriter out, Iterable<Expansion> expansions)
			throws IOException {
		out.beginArray();
		for (Expansion expansion : expansions) {
			writeExpansion(out, expansion);
		}
		out.endArray();
	}

	public static void writeExpansion(JsonWriter out, Expansion expansion) throws IOException {
		out.beginObject();
		out.name(NAME).value(expansion.name());
		out.name(CODE).value(expansion.code());
		out.name(RELEASE_DATE).value(expansion.releaseDate().toString());
		out.name(TYPE).value(expansion.type().name());
		out.name(BORDER_COLOR).value(expansion.borderColor().name());
		if (expansion.size() != null) {
			out.name(SIZE).value(expansion.size());
		}
		out.endObject();
	}

	public static void writePrintings(JsonWriter out, Multimap<WholeCard, WholePrinting> printings)
			throws IOException {
		out.beginObject();
		for (Map.Entry<WholeCard, Collection<WholePrinting>> entry : Multimaps.asMap(printings)
				.entrySet()) {
			out.name(entry.getKey().name());
			out.beginArray();
			for (WholePrinting printing : entry.getValue()) {
				writePrinting(out, printing);
			}
			out.endArray();
		}
		out.endObject();
	}

	private static void writePrinting(JsonWriter out, WholePrinting printing) throws IOException {
		out.beginObject();
		out.name(EXPANSION).value(printing.expansion().code());
		out.name(RARITY).value(printing.rarity().name());
		if (printing.isTimeshifted()) {
			out.name(TIMESHIFTED).value(true);
		}
		if (printing.hasOnePart()) {
			out.name(ONLY);
			writePartial(out, printing.only());
		} else {
			PrintingPair pair = printing.pair();
			out.name(PAIR).beginArray();
			writePartial(out, pair.first());
			writePartial(out, pair.second());
			out.endArray();
		}
		out.endObject();
	}

	private static void writePartial(JsonWriter out, Printing partial) throws IOException {
		out.beginObject();
		if (!partial.flavorText().isEmpty()) {
			out.name(FLAVOR_TEXT).value(partial.flavorText());
		}
		out.name(ARTIST).value(partial.artist());
		if (partial.collectorNumber() != null) {
			out.name(COLLECTOR_NUMBER).value(partial.collectorNumber().toString());
		}
		if (partial.watermark() != null) {
			out.name(WATERMARK).value(partial.watermark());
		}
		out.endObject();
	}

	public static ImmutableList<Expansion> readExpansions(JsonReader in,
			Map<String, WholeCard> cards) throws IOException {
		Builder<Expansion> builder = ImmutableList.builder();
		in.beginArray();
		while (in.hasNext()) {
			builder.add(readExpansion(in, cards));
		}
		in.endArray();
		return builder.build();
	}

	public static Expansion readExpansion(JsonReader in, Map<String, WholeCard> cards)
			throws IOException {
		Expansion.Builder builder = Expansion.builder();
		in.beginObject();
		while (in.hasNext()) {
			String key = in.nextName();
			switch (key) {
				case NAME:
					builder.setName(in.nextString());
					break;
				case CODE:
					builder.setCode(in.nextString());
					break;
				case RELEASE_DATE:
					builder.setReleaseDate(LocalDate.parse(in.nextString()));
					break;
				case TYPE:
					builder.setType(ReleaseType.valueOf(in.nextString()));
					break;
				case BORDER_COLOR:
					builder.setBorderColor(BorderColor.valueOf(in.nextString()));
					break;
				case SIZE:
					builder.setSize(in.nextInt());
					break;
				default:
					throw new IllegalArgumentException(key);
			}
		}
		in.endObject();
		return builder.build();
	}

	public static SortedSetMultimap<WholeCard, WholePrinting> readPrintings(
			JsonReader in,
			Map<String, WholeCard> cards, 
			Map<String, Expansion> expansions) throws IOException {
		SortedSetMultimap<WholeCard, WholePrinting> mapBuilder = TreeMultimap.create();
		while (in.hasNext()) {
			WholeCard card = cards.get(Diacritics.remove(in.nextName()));
			List<WholePrinting> printings = new ArrayList<>();
			in.beginArray();
			int index = 0;
			while (in.hasNext()) {
				printings.add(readPrinting(in, expansions, card, index++));
			}
			in.endArray();
		}
		in.endObject();
		return Multimaps.unmodifiableSortedSetMultimap(mapBuilder);
	}

	private static WholePrinting readPrinting(
			JsonReader in, 
			Map<String, Expansion> expansions,
			WholeCard card, 
			int index) throws IOException {
		WholePrinting.Builder builder = WholePrinting.builder();
		builder.setCard(card);
		builder.setVariation(index);
		in.beginObject();
		while (in.hasNext()) {
			String key = in.nextName();
			switch (key) {
				case EXPANSION:
					builder.setExpansion(expansions.get(in.nextString()));
					break;
				case RARITY:
					builder.setRarity(Rarity.valueOf(in.nextString()));
					break;
				case TIMESHIFTED:
					builder.setTimeshifted(in.nextBoolean());
					break;
				case ONLY:
					builder.setOnly(readPartial(in));
					break;
				case PAIR:
					in.beginArray();
					builder.setPair(PrintingPair.builder()
							.setFirst(readPartial(in))
							.setSecond(readPartial(in)));
					in.endArray();
					break;
				default:
					throw new IllegalArgumentException(key);
			}
		}
		in.endObject();
		return builder.build();
	}

	private static Printing.Builder readPartial(JsonReader in) throws IOException {
		Printing.Builder builder = Printing.builder();
		in.beginObject();
		while (in.hasNext()) {
			String key = in.nextName();
			switch (key) {
				case FLAVOR_TEXT:
					builder.setFlavorText(in.nextString());
					break;
				case ARTIST:
					builder.setArtist(in.nextString());
					break;
				case COLLECTOR_NUMBER:
					builder.setCollectorNumber(CollectorNumber.parse(in.nextString()));
					break;
				case WATERMARK:
					builder.setWatermark(in.nextString());
					break;
				default:
					throw new IllegalArgumentException(key);
			}
		}
		in.endObject();
		return builder;
	}

}
