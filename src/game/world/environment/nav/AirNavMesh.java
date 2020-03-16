package game.world.environment.nav;

import com.badlogic.gdx.math.Vector2;

public class AirNavMesh extends NavMesh<AirNavNode>
{

	@Override
	public void init()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void update()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Route<AirNavNode> getShortestRoute( final AirNavNode from, final AirNavNode to )
	{
		return null;
	}

	@Override
	protected AirNavNode createNavNode( final NavNodeDescriptor descriptor, final Vector2 point, final int nodeIdx )
	{
		return new AirNavNode(descriptor, point, nodeIdx);
	}

}
