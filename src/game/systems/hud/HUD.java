package game.systems.hud;

import game.systems.rendering.IRenderer;

public abstract class HUD
{

	private int screenWidth, screenHeight;

	public abstract void init();

	public abstract void update( IRenderer renderer );

	public void resize( int width, int height )
	{
		this.screenWidth = width;
		this.screenHeight = height;
	}

	public int getScreenHeight()
	{
		return screenHeight;
	}

	public int getScreenWidth()
	{
		return screenWidth;
	}

}
