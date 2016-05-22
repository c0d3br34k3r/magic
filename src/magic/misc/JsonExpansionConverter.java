package magic.misc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import magic.CollectorNumber;
import magic.Expansion;
import magic.Expansion.BorderColor;
import magic.Expansion.ReleaseType;
import magic.Printing;
import magic.PrintingPair;
import magic.Rarity;
import magic.WholeCard;
import magic.WholePrinting;

import org.joda.time.LocalDate;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class JsonExpansionConverter {

	private static final String NAME = "name";
	private static final String CODE = "code";
	private static final String RELEASE_DATE = "releaseDate";
	private static final String TYPE = "type";
	private static final String BORDER_COLOR = "borderColor";
	private static final String COLLECTOR_NUMBERS = "collectorNumbers";
	private static final String ONLINE_ONLY = "onlineOnly";
	private static final String BOOSTER = "booster";
	private static final String PRINTINGS = "printings";

	private static final String RARITY = "rarity";
	private static final String TIMESHIFTED = "timeshifted";
	private static final String ONLY = "only";
	private static final String PAIR = "pair";

	private static final String FLAVOR_TEXT = "flavorText";
	private static final String ARTIST = "artist";
	private static final String COLLECTOR_NUMBER = "collectorNumber";
	private static final String WATERMARK = "watermark";

	public static void writeExpansions(JsonWriter out, Iterable<Expansion> expansions) throws IOException {
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
		if (expansion.hasCollectorNumbers()) {
			out.name(COLLECTOR_NUMBERS).value(true);
		}
		if (expansion.onlineOnly()) {
			out.name(ONLINE_ONLY).value(true);
		}
		if (expansion.hasBooster()) {
			out.name(BOOSTER).value(true);
		}
		out.name(PRINTINGS).beginObject();
		for (Entry<WholeCard, Collection<WholePrinting>> entry : expansion.printings().asMap()
				.entrySet()) {
			out.name(entry.getKey().name()).beginArray();
			for (WholePrinting printing : entry.getValue()) {
				writePrinting(out, printing);
			}
			out.endArray();
		}
		out.endObject();
		out.endObject();
	}

	private static void writePrinting(JsonWriter out, WholePrinting printing) throws IOException {
		out.beginObject();
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

	public static Collection<Expansion> readExpansions(JsonReader in, Map<String, WholeCard> cards)
			throws IOException {
		List<Expansion> expansions = new ArrayList<>();
		in.beginArray();
		while (in.hasNext()) {
			expansions.add(readExpansion(in, cards));
		}
		in.endArray();
		return expansions;
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
				case COLLECTOR_NUMBERS:
					builder.setHasCollectorNumbers(in.nextBoolean());
					break;
				case ONLINE_ONLY:
					builder.setOnlineOnly(in.nextBoolean());
					break;
				case BOOSTER:
					builder.setHasBooster(in.nextBoolean());
					break;
				case PRINTINGS:
					builder.setPrintings(readPrintings(in, cards));
					break;
				default:
					throw new IllegalArgumentException(key);
			}
		}
		in.endObject();
		return builder.build();
	}

	private static Iterable<WholePrinting.Builder> readPrintings(JsonReader in,
			Map<String, WholeCard> cards) throws IOException {
		List<WholePrinting.Builder> printings = new ArrayList<>();
		in.beginObject();
		while (in.hasNext()) {
			WholeCard card = cards.get(Diacritics.remove(in.nextName()));
			in.beginArray();
			int index = 0;
			while (in.hasNext()) {
				WholePrinting.Builder builder = WholePrinting.builder();
				builder.setCard(card);
				builder.setVariation(index++);
				in.beginObject();
				while (in.hasNext()) {
					String key = in.nextName();
					switch (key) {
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
				printings.add(builder);
				in.endObject();
			}
			in.endArray();
		}
		in.endObject();
		return printings;
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
