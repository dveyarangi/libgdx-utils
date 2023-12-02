package game.util.arrays;

@FunctionalInterface
public interface Filter <T>
{
	public boolean accept(T t);
}
