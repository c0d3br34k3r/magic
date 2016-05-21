package magic.misc;

import java.io.IOException;
import java.util.Collection;
import java.util.Map.Entry;

import org.joda.time.LocalDate;

import magic.Expansion;
import magic.Expansion.Builder;
import magic.Printing;
import magic.PrintingPair;
import magic.WholeCard;
import magic.WholePrinting;

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
	
	public static Expansion readExpansion(JsonReader in) throws IOException {
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
				default:
					throw new IllegalArgumentException(key);
			}
		}
		in.endObject();
		return builder.build();
	}

}
