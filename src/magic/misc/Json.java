package magic.misc;

import java.io.IOException;

import com.google.gson.stream.JsonWriter;

import magic.Card;
import magic.CardPair;
import magic.Color;
import magic.WholeCard;

public class Json {

	public static void write(WholeCard whole, JsonWriter writer)
			throws IOException {
		writer.beginObject();
		writer.name("colorIdentity")
				.value(Color.toString(whole.colorIdentity()));
		if (whole.isStandalone()) {
			writer.name("only");
			write(whole.only(), writer);
		} else {
			CardPair cards = whole.pair();
			writer.name("layout").value(cards.layout().name());
			writer.name("first");
			write(cards.first(), writer);
			writer.name("second");
			write(cards.second(), writer);
		}
		writer.endObject();
	}

	private static void write(Card card, JsonWriter writer) throws IOException {
		writer.beginObject();
		writer.name("name").value(card.name());
		if (!card.manaCost().isEmpty()) {
			writer.name("manaCost").value(card.manaCost().toString());
		}
		if (card.colorOverride() != null) {

		}

		writer.endObject();
	}

}
