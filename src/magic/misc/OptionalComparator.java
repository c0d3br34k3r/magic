package magic.misc;

import java.util.Comparator;

import com.google.common.base.Optional;

public abstract class OptionalComparator {

	private static <T extends Comparable<T>> int compareEither(
			Optional<T> o1, Optional<T> o2, int sign) {
		if (o1.isPresent() && o2.isPresent()) {
			return o1.get().compareTo(o2.get());
		}
		return sign * Boolean.compare(o1.isPresent(), o2.isPresent());
	}

	private static class AbsentFirst<T extends Comparable<T>>
	implements Comparator<Optional<T>> {
		@Override public int compare(Optional<T> o1, Optional<T> o2) {
			return compareEither(o1, o2, 1);
		}
	}

	private static class AbsentLast<T extends Comparable<T>>
	implements Comparator<Optional<T>> {
		@Override public int compare(Optional<T> o1, Optional<T> o2) {
			return compareEither(o1, o2, -1);
		}
	}

	private static final AbsentFirst<?> ABSENT_FIRST = new AbsentFirst<>();

	private static final AbsentLast<?> ABSENT_LAST = new AbsentLast<>();

	@SuppressWarnings("unchecked") 
	public static 
	<T extends Comparable<T>> Comparator<Optional<T>> absentFirst() {
		return (AbsentFirst<T>) ABSENT_FIRST;
	}

	@SuppressWarnings("unchecked") 
	public static 
	<T extends Comparable<T>> Comparator<Optional<T>> absentLast() {
		return (AbsentLast<T>) ABSENT_LAST;
	}

}
