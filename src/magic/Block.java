package magic;

import java.util.List;

import org.joda.time.LocalDate;

public interface Block extends Comparable<Block> {

	String name();
	
	LocalDate date();
	
	List<Expansion> expansions();
	
}
