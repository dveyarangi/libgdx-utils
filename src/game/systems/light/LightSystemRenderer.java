package game.systems.light;

import game.resources.ResourceFactory;
import game.systems.rendering.EntityRenderingSystem;
import game.systems.rendering.IRenderer;
import game.systems.rendering.IRenderingContext;

public class LightSystemRenderer implements IRenderingContext 
{
	private LightSystem system;

	public LightSystemRenderer(LightSystem system) 
	{
		this.system = system;
	}

	@Override
	public void init(ResourceFactory factory, IRenderer renderer)
	{
	}
	public void begin()
	{
		system.rayHandler.setCombinedMatrix( system.camera.combined );

		system.rayHandler.update();
		system.rayHandler.render();
	}

	@Override
	public int id()
	{
		// TODO:
		return EntityRenderingSystem.POST_RENDERING;
	}



	@Override
	public void end()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isEntityless() { return false; }
}
