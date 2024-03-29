package game.systems.rendering;

import com.badlogic.gdx.graphics.Color;

public abstract class SpriteDef <R extends SpriteComponent> extends RendererDef<R>
{
	/**
	 *
	 */
	public float xOffset = Float.NaN;
	public float yOffset = Float.NaN;
	public float zOffset = Float.NaN;
	public float w = Float.NaN;
	public float h = Float.NaN;
	
	public Color tint = Color.WHITE;
	//public boolean xFlip = false;
	//public boolean yFlip = false;


	public SpriteDef( float xOffset, float yOffset, float zOffset, float w, float h, Color tint)
	{
		super();
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.zOffset = zOffset;
		this.w = w;
		this.h = h;
		this.tint = tint;
	}
}
