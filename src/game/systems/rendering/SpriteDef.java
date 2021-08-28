package game.systems.rendering;

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
	public boolean xFlip = false;
	public boolean yFlip = false;


	public SpriteDef( float xOffset, float yOffset, float zOffset, float w, float h, boolean xFlip, boolean yFlip)
	{
		super();
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.zOffset = zOffset;
		this.w = w;
		this.h = h;
		this.xFlip = xFlip;
		this.yFlip = yFlip;

	}
}
