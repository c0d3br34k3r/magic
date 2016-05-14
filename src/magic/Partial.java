package magic;

import javax.annotation.Nullable;

abstract class Partial<P extends Partial<P>> {

	public abstract @Nullable Link<P> link();

	public abstract Whole<P> whole();

}
