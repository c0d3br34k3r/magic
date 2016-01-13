package magic.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import com.google.common.collect.AbstractIterator;

public class LineIterable implements Iterable<String> {

	private final BufferedReader reader;

	public LineIterable(String input) {
		this.reader = new BufferedReader(new StringReader(input));
	}

	@Override public Iterator<String> iterator() {
		return new AbstractIterator<String>() {

			@Override protected String computeNext() {
				try {
					String line = reader.readLine();
					if (line == null) {
						return endOfData();
					}
					return line;
				} catch (IOException e) {
					throw new AssertionError(e);
				}
			}

		};
	}

}
