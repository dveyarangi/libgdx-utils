package game.world.environment.nav;

import com.badlogic.gdx.math.Vector2;

/**
 * calculates all shortest routes for a given nav mesh
 * 
 * @author Ni
 *
 */
public abstract class FloydWarshal extends NavMesh<NavNode>
{
	// protected NavNode[][] routes;

	protected int[][] next;

	protected float[][] dist;

	public FloydWarshal()
	{
	}

	@Override
	public void init()
	{
		calculate();
	}

	public void calculate()
	{
		int n = nodes.size();

		dist = new float[n][n];
		next = new int[n][n];
		initArrays(dist, next, n);

		for( int k = 0; k < n; k++ )
		{

			for( int i = 0; i < n; i++ )
			{
				for( int j = 0; j < n; j++ )
				{
					if( dist[i][k] + dist[k][j] < dist[i][j] )
					{
						dist[i][j] = dist[i][k] + dist[k][j];
						next[i][j] = k;
					}
				}
			}

		}

		for( int i = 0; i < n; i++ )
		{
			for( int j = 0; j < n; j++ )
			{
				if( i == j )
				{
					continue;
				}
				if( !hasPath(i, j) )
				{
					continue;
				}

				int prevNextIdx = j, nextIdx = next[i][j];

				while( !isNeighbor(i, nextIdx) && prevNextIdx != nextIdx )
				{
					prevNextIdx = nextIdx;
					nextIdx = next[i][nextIdx];
					next[i][j] = nextIdx;
				}

			}
		}
	}

	@Override
	public void update()
	{
		// TODO Auto-generated method stub

	}

	private void initArrays( final float[][] dist, final int[][] next, final int n )
	{
		for( int i = 0; i < n; i++ )
		{
			for( int j = 0; j < n; j++ )
			{
				if( i != j )
				{
					dist[i][j] = Float.POSITIVE_INFINITY;
					next[i][j] = -1;
				}
				else
				{
					dist[i][j] = 0;
					next[i][j] = i;
				}
			}
		}

		for( NavNode node : nodes )
		{
			for( NavNode neigh : node.getNeighbors() )
			{
				dist[node.idx][neigh.idx] = dist[node.idx][neigh.idx] = node.getPoint().dst(neigh.getPoint());
				next[node.idx][neigh.idx] = neigh.idx;
				next[neigh.idx][node.idx] = node.idx;
			}
		}

	}

	public boolean hasPath( final int fromIdx, final int toIdx )
	{
		return !Float.isInfinite(dist[fromIdx][toIdx]);
	}

	public boolean isNeighbor( final int idx, final int jdx )
	{
		NavNode node = getNode(idx);
		for( NavNode neigh : node.getNeighbors() )
		{
			if( neigh.idx == jdx )
				return true;
		}

		return false;
	}

	/**
	 * find the shortest route between node from and to
	 * 
	 * @return ordered list starting at a and ending at b using shortest route /
	 *         null if no route
	 */
	@Override
	public FloydWarshalRoute getShortestRoute( final NavNode from, final NavNode to )
	{
		FloydWarshalRoute r = FloydWarshalRoute.routesPool.obtain();
		// r.set(this, from, to);
		return r;
	}

	public int getNextNode( final int currIdx, final int targetIdx )
	{
		return next[currIdx][targetIdx];
	}

	@Override
	protected abstract NavNode createNavNode( final NavNodeDescriptor descriptor, final Vector2 point, final int nodeIdx );
}
