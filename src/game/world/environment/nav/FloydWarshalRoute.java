package game.world.environment.nav;

import java.util.NoSuchElementException;

import com.badlogic.gdx.utils.Pool;

/**
 * represents a route across a graph <br>
 * 
 * @author Ni
 *
 */
public class FloydWarshalRoute extends Route
{
	public static final Pool<FloydWarshalRoute> routesPool =
			new Pool<FloydWarshalRoute>()
			{
				@Override
				protected FloydWarshalRoute newObject()
				{
					return new FloydWarshalRoute();
				}
			};

	private NavNode from;
	private NavNode to;
	private boolean hasNext;

	private FloydWarshalRoute()
	{
		this.from = null;
		this.to = null;
		this.hasNext = false;
	}

	/**
	 * prepare for acquire!
	 * 
	 * @param navMesh
	 * @param from
	 * @param to
	 */
	protected void set(final NavNode from, final NavNode to )
	{
		this.from = from;
		this.to = to;
		hasNext = false;

		if( from == null )
			throw new IllegalArgumentException("cannot generate path when origin node is null");

		if( to == null )
			throw new IllegalArgumentException("cannot generate path when target node is null");

		/*if( from.aIdx == to.aIdx || navMesh.routes[from.aIdx][to.aIdx] != null )
		{
			hasNext = true;
		}*/
	}

	@Override
	public boolean hasNext()
	{
		return hasNext;
	}

	/**
	 * Return current node
	 */
	@Override
	public NavNode curr()
	{
		return from;
	}

	/**
	 * Advances the current node in route and returns it;
	 *
	 */
	@Override
	public NavNode next()
	{
		// next does some redundant things in order to avoid being overly
		// stateful

		if( !hasNext )
			throw new NoSuchElementException("no more elements on this route");

		if( from == to )
		{
			hasNext = false;
			return from;
		}

		/*SurfaceNavNode tmpto = navMesh.routes[from.aIdx][to.aIdx];

		// if from==tmpto we've reached the web node
		if( from == tmpto )
		{
			from = navMesh.routes[navMesh.fwIdx[from.idx]][to.aIdx];
			return from;
		}

		if( tmpto == null )
		{
			tmpto = to;
		}

		// otherwise, from and tmpto are on the same asteroid
		int[] range = navMesh.indexRange.get(from.aIdx);
		int dir = navMesh.cwDistance(from, tmpto) < navMesh.ccwDistance(from, tmpto) ? 1 : -1;

		// stupid modulo for negatives is negative so i added this
		int mod = from.idx + dir - range[0];
		int rangesize = range[1] - range[0] + 1;
		mod = mod < 0 ? mod + rangesize : mod;

		from = navMesh.getNode(mod % rangesize + range[0]);
*/
		return from;
	}

	@Override
	public void reset()
	{
		this.from = null;
		this.to = null;
		this.hasNext = false;
	}

	@Override
	public void recycle()
	{
		routesPool.free(this);
	}

}
