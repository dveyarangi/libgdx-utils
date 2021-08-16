package game.systems.rendering;

public abstract class SpriteDef  extends RendererDef<SpriteComponent>
{
	public float ox = Float.NaN;
	public float oy = Float.NaN;
	public float zOffset;
	public float w = Float.NaN;
	public float h = Float.NaN;
	public boolean xFlip = false;
	public boolean yFlip = false;


	public SpriteDef(float ox, float oy, float zOffset, float w, float h, boolean xFlip, boolean yFlip)
	{
		super(SpriteComponent.class);
		this.ox = ox;
		this.oy = oy;
		this.zOffset = zOffset;
		this.w = w;
		this.h = h;
		this.xFlip = xFlip;
		this.yFlip = yFlip;

	}
}
