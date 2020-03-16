package game.util.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.ConvexHull;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntArray;

public class DualGraph
{
	public final List <Triangle> triangles = new ArrayList <> ();
	public final List <Edge> edges = new ArrayList <> ();
	public final List <Node> nodes = new ArrayList <> ();
	public final List <Tile> tiles = new ArrayList <> ();

	Map <Node, List<Edge>> nodeEdges = new HashMap <> ();
	public Map <Node, List<Triangle>> nodeTriangles = new HashMap <> ();

	public Node addNode( float x, float y )
	{
		Node node = new Node( x, y );
		if( nodes.contains( node ))
			throw new IllegalArgumentException("Node already exists at " + x + "," + y);

		nodes.add(node);

		nodeEdges.put( node, new ArrayList <Edge> ());
		nodeTriangles.put(node, new ArrayList <Triangle> ());

		return node;
	}

	public Node getNode( short index ) { return nodes.get(index); }

	public Edge addEdge( Node a, Node b )
	{
		List <Edge> edgesOfNodeA = nodeEdges.get(a);
		assert edgesOfNodeA != null : "Not registered node " + a;
		List <Edge> edgesOfNodeB = nodeEdges.get(b);
		assert edgesOfNodeB != null : "Not registered node " + b;

		Edge edge = this.getExistingEdge( a, b);
		if( edge == null )
		{
			edge = new Edge( a, b);
			edges.add( edge );
			edgesOfNodeA.add( edge );
			edgesOfNodeB.add( edge );
		}

		return edge;
	}

	public Edge getExistingEdge( Node a, Node b)
	{
		for(Edge edge : edges)
		{
			if(edge.equals(a, b))
				return edge;
		}

		return null;
	}

	public Triangle addTriangle( Node a, Node b, Node c )
	{
		List <Triangle> trigsOfNodeA = nodeTriangles.get( a ); 		assert trigsOfNodeA != null : "Not registered node " + a;
		List <Triangle> trigsOfNodeB = nodeTriangles.get( b );      assert trigsOfNodeB != null : "Not registered node " + b;
		List <Triangle> trigsOfNodeC = nodeTriangles.get( c );      assert trigsOfNodeC != null : "Not registered node " + c;

		Triangle triangle = this.getExistingTriangle( a, b, c );
		if( triangle == null )
		{
			triangle = new Triangle(a, b, c);

			triangles.add(triangle);
			trigsOfNodeA.add(triangle);
			trigsOfNodeB.add(triangle);
			trigsOfNodeC.add(triangle);
		}

		return triangle;
	}

	private Triangle getExistingTriangle( Node a, Node b, Node c )
	{
		for(Triangle t : triangles)
		{
			if(t.equals(a, b, c))
				return t;
		}

		return null;
	}

	public void computeTiles(float dim)
	{
		for(Node node : nodeTriangles.keySet())
		{
			List <Triangle> trianglesOfNode = nodeTriangles.get( node );
			FloatArray flatCentroids = new FloatArray();
			for(int idx = 0; idx < trianglesOfNode.size(); idx ++)
			{
				Triangle t = trianglesOfNode.get(idx);

				// prevent tile vertices go to far out of screen:
				// otherwise, triangulation algorithm starts choking
				if(Math.abs(t.circumcenter.x) > 2*dim
				|| Math.abs(t.circumcenter.y) > 2*dim)
					continue;

				flatCentroids.add(t.circumcenter.x);
				flatCentroids.add(t.circumcenter.y);

			}

			ConvexHull hull = new ConvexHull();

			IntArray indices = hull.computeIndices(flatCentroids, false, false);
			float [] vertices = new float[2*indices.size];
			for(int idx = 0; idx < indices.size; idx ++)
			{
				vertices[2*idx] = flatCentroids.get(2*indices.get(idx));
				vertices[2*idx+1] = flatCentroids.get(2*indices.get(idx)+1);
			}
			if(vertices.length > 6) // drop invalid tiles
				tiles.add( new Tile( new Polygon(vertices) ) );


		}

	}

}
