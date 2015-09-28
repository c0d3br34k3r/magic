package magic;

public interface Link2 {

	public enum Order {
		ONLY,
		FIRST,
		SECOND;
	}
	
	boolean isPresent();
	
	Card get();
	
}
