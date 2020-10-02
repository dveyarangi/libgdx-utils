package game.systems.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import game.resources.ResourceFactory;

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
		Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
		shaper.begin();
	}

	@Override
	public void end()
	{
		shaper.end();
	}
	
	@Override public String toString() { return "shape rendering context"; }

}
