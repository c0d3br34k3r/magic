package magic.misc;

public class CaseInsensitive {
	
	public static boolean contains(String source, String target) {
		return contains(source, target, 0);
	}
	
	public static boolean contains(String source, String target, int start) {
		return indexOf(source, target, start) != -1;
	}
	
	public static int indexOf(String source, String target) {
		return indexOf(source, target, 0);
	}
	
	public static int indexOf(String source, String target, int start) {
		int limit = source.length() - target.length() + 1;
		for (int i = start; i < limit; i++) {
			if (source.regionMatches(true, i, target, 0, target.length())) {
				return i;
			}
		}
		return -1;
	}
	
	public static boolean containsTokens(String source, String terms) {
		String[] tokens = terms.split(" ");
		for (String token : tokens) {
			if (!token.isEmpty() && !contains(source, token)) {
				return false;
			}
		}
		return true;
	}
	
}
