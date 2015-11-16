package magic;

abstract class Whole<P extends Partial<P>> implements Iterable<P> {

	public abstract boolean isStandalone();
	
	public abstract P only();
	
	public abstract Pair<P> pair();
	
}
