package magic.misc;

import java.nio.charset.StandardCharsets;

public class Test {

	public static void main(String[] args) {
		System.out.println(new String(new byte[]{(byte) 0x21, (byte) 0xff}, StandardCharsets.UTF_8));
	}

}
