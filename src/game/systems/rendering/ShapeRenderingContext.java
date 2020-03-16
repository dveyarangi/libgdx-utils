package game.systems.rendering;

import game.resources.ResourceFactory;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ShapeRenderingContext implements IRenderingContext
{
	private int cid;

	private ShapeRenderer shaper;

	public ShapeRenderingContext( int cid, ShapeRenderer shaper )
	{
		this.cid = cid;
		this.shaper = shaper;
	}

	@Override
	public int id()
	{
		return cid;
	}

	@Override
	public void init( ResourceFactory factory, IRenderer renderer )
	{
	}

	@Override
	public void begin()
	{
		shaper.begin();
	}

	@Override
	public void end()
	{
		shaper.end();
	}

}
