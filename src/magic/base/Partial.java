package magic.base;

import javax.annotation.Nullable;

public abstract class Partial<T> {
	
	public abstract @Nullable Link<T> link();
	
	public abstract Whole<T> whole();

}
