package game.world.environment.nav;


public class NavMeshGenerator
{

//	private ITriangulator triangulator;

	public NavMeshGenerator()
	{
		//triangulator = new Delaunay2D();
	}

	/**
	 * Filter for triangulated stuff
	 */
	/*
	 * private static class AsteroidFilter implements MeshFilter { private
	 * Vector2 minCorner, maxCorner; private List<Polygon> polygons;
	 *
	 * public AsteroidFilter(final List<Polygon> polygons, final Vector2
	 * minCorner, final Vector2 maxCorner) { this.maxCorner = maxCorner;
	 * this.minCorner = minCorner; this.polygons = polygons; }
	 *
	 * @Override public boolean accept( final Edge edge ) { return true; }
	 *
	 * @Override public boolean accept( final IVector2D node ) { if( node.x() <
	 * minCorner.x || node.y() < minCorner.y || node.x() > maxCorner.x ||
	 * node.y() > maxCorner.y ) return false;
	 *
	 * for( Polygon polygon : polygons ) { if( polygon.contains(
	 * (float)node.x(), (float)node.y() )) return false; } return true; } }
	 */

	/*
	 * public NavMesh generateMesh( final Collection <Asteroid> asteroids, final
	 * Vector2 minCorner, final Vector2 maxCorner ) { NavMesh resultMesh = new
	 * AirNavMesh();
	 *
	 * final List<Polygon> polygons = new LinkedList<Polygon>();
	 *
	 * // collecting points for triangulation // TODO: get rid of either
	 * IVector2D or Vector2 Set<IVector2D> points = new HashSet<IVector2D>();
	 * for( Asteroid asteroid : asteroids ) { float[] polyv = new float[2 *
	 * asteroid.getModel().getVertices().length]; int idx = 0; for( Vector2
	 * vertex : asteroid.getModel().getVertices() ) { polyv[idx++] = vertex.x;
	 * polyv[idx++] = vertex.y; points.add( Vector2D.R( vertex.x, vertex.y ) );
	 * } polygons.add( new Polygon( polyv ) ); }
	 *
	 * // looks prettier this way: points.add( Vector2D.R( minCorner.x,
	 * minCorner.y ) ); points.add( Vector2D.R( minCorner.x, maxCorner.y ) );
	 * points.add( Vector2D.R( maxCorner.x, maxCorner.y ) ); points.add(
	 * Vector2D.R( maxCorner.x, minCorner.y ) );
	 *
	 * // triangulating: // TriangleStore store1 = triangulator.triangulate(
	 * points );
	 *
	 * // inverting the triangulation to get regions // Mesh mesh =
	 * VoronoiDiagram.invertTriangleStore( store1, new AsteroidFilter( polygons,
	 * minCorner, maxCorner ));
	 *
	 * /* for( Asteroid asteroid : asteroids ) { for( Vector2 vertex :
	 * asteroid.getModel().getVertices() ) { mesh.getNodes().add( Vector2D.R(
	 * vertex.x, vertex.y ) ); } }
	 */
	// TriangleStore store2 = triangulator.triangulate( mesh1.getNodes() );

	/*
	 * Mesh mesh2 = VoronoiDiagram.invertTriangleStore( store2, new
	 * AsteroidFilter( polygons, minCorner, maxCorner ));
	 *
	 * /* for(Edge edge : store2.getEdges()) { NavNode node1 = nodes.get( new
	 * Vector2((float)edge.first().x(), (float)edge.first().y() ) ); NavNode
	 * node2 = nodes.get( new Vector2((float)edge.second().x(),
	 * (float)edge.second().y() ) ); mesh.linkNodes( node1, node2, Type.AIR ); }
	 *
	 *
	 * // converting voronoi regions to nav mesh:
	 *
	 * Map<Vector2, NavNode> nodes = new HashMap<Vector2, NavNode>(); edges:
	 * for( Edge edge : mesh.getEdges() ) {
	 *
	 * Vector2 c1 = new Vector2( (float) edge.first().x(), (float)
	 * edge.first().y() ); Vector2 c2 = new Vector2( (float) edge.second().x(),
	 * (float) edge.second().y() );
	 *
	 * NavNode node1; if( nodes.containsKey( c1 ) ) { node1 = nodes.get( c1 ); }
	 * else { nodes.put( c1, node1 = resultMesh.insertNode( null, c1 ) ); }
	 * NavNode node2;
	 *
	 * if( nodes.containsKey( c2 ) ) { node2 = nodes.get( c2 ); } else {
	 * nodes.put( c2, node2 = resultMesh.insertNode( null, c2 ) ); }
	 *
	 * resultMesh.linkNodes( node1, node2, Type.AIR );
	 *
	 * }
	 *
	 * return resultMesh; }
	 */
}
