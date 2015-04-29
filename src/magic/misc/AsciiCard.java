package magic.misc;

import java.io.IOException;

import com.google.common.annotations.Beta;

import magic.Card;

public interface AsciiCard extends Card {

	String asciiName();

	String asciiText();

	@Beta void writeToAscii(Appendable out) throws IOException;

	@Beta void printAscii();

}
