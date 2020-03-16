package game.systems.light;

import game.systems.rendering.ISystemRenderer;

public class LightSystemRenderer implements ISystemRenderer {
	private LightSystem system;

	public LightSystemRenderer(LightSystem system) 
	{
		this.system = system;
	}

	public void render()
	{
		system.rayHandler.render();
	}
}
