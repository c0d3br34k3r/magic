package magic.misc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import magic.Block;
import magic.Expansion;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class JsonBlockConverter {

	private static final String NAME = "name";
	private static final String EXPANSIONS = "expansions";

	public static void writeBlocks(JsonWriter out, List<Block> blocks) throws IOException {
		out.beginArray();
		for (Block block : blocks) {
			writeBlock(out, block);
		}
		out.endArray();
	}

	private static void writeBlock(JsonWriter out, Block block) throws IOException {
		out.beginObject();
		out.name(NAME).value(block.name());
		out.name(EXPANSIONS).beginArray();
		for (Expansion expansion : block.expansions()) {
			out.value(expansion.code());
		}
		out.endArray();
		out.endObject();
	}

	public static Collection<Block> readBlocks(JsonReader in, Map<String, Expansion> expansions)
			throws IOException {
		List<Block> blocks = new ArrayList<>();
		in.beginArray();
		while (in.hasNext()) {
			blocks.add(readBlock(in, expansions));
		}
		in.endArray();
		return blocks;
	}

	private static Block readBlock(JsonReader in, Map<String, Expansion> expansions)
			throws IOException {
		String name = null;
		Builder<Expansion> builder = ImmutableList.builder();
		in.beginArray();
		while (in.hasNext()) {
			String key = in.nextName();
			switch (key) {
				case NAME:
					name = in.nextString();
					break;
				case EXPANSIONS:
					in.beginArray();
					while (in.hasNext()) {
						builder.add(expansions.get(in.nextString()));
					}
					in.endArray();
					break;
				default:
					throw new IllegalArgumentException(key);
			}
		}
		in.endArray();
		return new Block(name, builder.build());
	}

}
