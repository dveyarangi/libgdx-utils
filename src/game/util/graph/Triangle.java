package game.util.graph;

import com.badlogic.gdx.math.GeometryUtils;
import com.badlogic.gdx.math.Vector2;

public class Triangle
{
	public Node A, B, C;
	public Vector2 circumcenter;
	public Vector2 centroid;
	public Triangle( Node a, Node b, Node c )
	{
		this.A = a; this.B = b; this.C = b;
		this.circumcenter = new Vector2();
		this.centroid = new Vector2();

		GeometryUtils.triangleCentroid    ( a.x, a.y, b.x, b.y, c.x, c.y,  centroid);
		GeometryUtils.triangleCircumcenter( a.x, a.y, b.x, b.y, c.x, c.y,  circumcenter);

	}
	public boolean equals( Node a, Node b, Node c )
	{
		return (A.equals(a) && B.equals(b) && C.equals(c))
			|| (A.equals(a) && B.equals(c) && C.equals(b))
			|| (A.equals(b) && B.equals(a) && C.equals(c))
			|| (A.equals(b) && B.equals(c) && C.equals(a))
			|| (A.equals(c) && B.equals(a) && C.equals(c))
			|| (A.equals(c) && B.equals(c) && C.equals(a));
	}

	@Override
	public int hashCode()
	{
		// node order does not matter:
		return A.hashCode() + B.hashCode() + C.hashCode();

	}

}
