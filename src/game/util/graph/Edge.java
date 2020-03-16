package game.util.graph;

public class Edge
{
	public final Node nodeA, nodeB;

	public Edge( Node nodeA, Node nodeB )
	{
		this.nodeA = nodeA;
		this.nodeB = nodeB;
	}

	public Node a() { return nodeA; }
	public Node b() { return nodeB; }

	@Override
	public boolean equals(Object o)
	{
		if( o == null ) return false;
		if( o == this ) return true;
		if( ! ( o instanceof Edge ) ) return false;

		Edge that = (Edge) o;
		return this.equals(that.a(), that.b());
	}

	public boolean equals( Node a, Node b )
	{
		return (this.a().equals( a ) && this.b().equals( b ))
			|| (this.a().equals( b ) && this.b().equals( a ));
	}
	@Override
	public int hashCode()
	{
		// node order does not matter:
		return nodeA.hashCode() + nodeB.hashCode();
	}
}
