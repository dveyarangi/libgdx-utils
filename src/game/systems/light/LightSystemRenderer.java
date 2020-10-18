package game.systems.light;

import game.systems.rendering.IRenderer;
import game.systems.rendering.ISystemRenderer;

public class LightSystemRenderer implements ISystemRenderer {
	private LightSystem system;

	public LightSystemRenderer(LightSystem system) 
	{
		this.system = system;
	}

	public void render(IRenderer renderer)
	{
		system.rayHandler.setCombinedMatrix( system.camera.combined );

		system.rayHandler.update();
		system.rayHandler.render();
	}
}
