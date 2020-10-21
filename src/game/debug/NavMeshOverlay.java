package game.debug;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntMap.Entry;

import game.systems.rendering.IRenderer;
import game.world.environment.nav.NavEdge;
import game.world.environment.nav.NavMesh;
import game.world.environment.nav.NavNode;

public class NavMeshOverlay implements IOverlay<NavMesh>
{

	private NavMesh navMesh;

	public NavMeshOverlay( NavMesh<NavNode> navMesh )
	{

		this.navMesh = navMesh;
	}

	@Override
	public void draw( IRenderer renderer )
	{

		if( navMesh.getNodesNum() == 0 )
			return;
		ShapeRenderer shape = renderer.shaper();
		SpriteBatch sprites = renderer.sprites();
		NavNode srcNode;

		shape.begin(ShapeType.Filled);
		for( int fidx = 0; fidx < navMesh.getNodesNum(); fidx++ )
		{
			shape.setColor(0, 1, 0, 1f);
			srcNode = navMesh.getNode(fidx);

			shape.circle(srcNode.getPoint().x, srcNode.getPoint().y, 1);
		}
		shape.end();

		sprites.begin();
		for( int fidx = 0; fidx < navMesh.getNodesNum(); fidx++ )
		{
			srcNode = navMesh.getNode(fidx);
			Debug.FONT.draw(sprites, String.valueOf(fidx), srcNode.getPoint().x + 1, srcNode.getPoint().y + 1);
		}
		sprites.end();

		shape.begin(ShapeType.Line);
		shape.setColor(0, 1, 0, 0.5f);

		for( Object entry : navMesh.getEdges().entries() )
		{
			NavEdge e = (NavEdge) ( (Entry) entry ).value;
			Vector2 p1 = e.getNode1().getPoint();
			Vector2 p2 = e.getNode2().getPoint();
			shape.line(p1.x, p1.y, p2.x, p2.y);

		}
		// for(int fidx = 0; fidx < navMesh.getNodesNum(); fidx ++)
		// {
		// srcNode = navMesh.getNode( fidx );
		//
		// shape.setColor( 0, 1, 0, 0.5f );
		// for(NavNode tarNode : srcNode.getNeighbors())
		// {
		// shape.line( srcNode.getPoint().x, srcNode.getPoint().y,
		// tarNode.getPoint().x, tarNode.getPoint().y);
		// }
		// }
		shape.end();
	}

	@Override
	public boolean useWorldCoordinates()
	{
		// TODO Auto-generated method stub
		return true;
	}


}
