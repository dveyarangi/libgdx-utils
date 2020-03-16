package game.util.graph;

public class Node
{
	/**
	 * Node coordinates
	 */
	public final float x, y;
	/**
	 * Rounded integer values using for node comparison
	 */
	public final int compx, compy;
	public Node( float x, float y )
	{
		this.x = x;
		this.y = y;

		// calc comparison values:
		this.compx = Math.round(x * 10000);
		this.compy = Math.round(y * 10000);
	}
	public float x() { return x; }
	public float y() { return y; }

	@Override
	public boolean equals(Object o)
	{
		if( o == null ) return false;
		if( o == this ) return true;
		if( ! ( o instanceof Node ) ) return false;

		Node that = (Node) o;
		return this.compx == that.compx && this.compy == that.compy;
	}

	@Override
	public int hashCode()
	{
		int hash = compx;
		hash = (hash % 17) + compy;
		return hash;
	}

}
