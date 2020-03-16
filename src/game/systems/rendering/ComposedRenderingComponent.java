package game.systems.rendering;

import com.badlogic.ashley.core.ComponentType;
import com.badlogic.ashley.core.Entity;

import game.world.Level;

public class ComposedRenderingComponent implements IRenderingComponent
{
	static
	{   // map as rendering component:
		ComponentType.registerFor(IRenderingComponent.class, ComposedRenderingComponent.class);
	}

	/**
	 * Rendering context id; defines the texture scope for this animation;
	 */
	private int [] cid;

	public ComposedRenderingComponent(IRenderingComponent ... components)
	{

	}

	@Override
	public void reset()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void init( Entity entity, RendererDef def, Level leve )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void render( Entity entity, IRenderer renderer, IRenderingContext context, float deltaTime )
	{
		// TODO Auto-generated method stub

	}

	@Override public int[] cid() { return cid; }

}
