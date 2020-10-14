package game.systems.rendering;

import game.util.rendering.HAlign;
import game.util.rendering.VAlign;

public abstract class SpriteDef  extends RendererDef<SpriteComponent>
{
	public float priority = Float.NaN;
	public float ox = Float.NaN;
	public float oy = Float.NaN;
	public float w = Float.NaN;
	public float h = Float.NaN;
	public boolean xFlip = false;
	public boolean yFlip = false;
	public HAlign hAlign = HAlign.CENTER;
	public VAlign vAlign = VAlign.CENTER;
	
	public SpriteDef(float priority, float ox, float oy, float w, float h, boolean xFlip, boolean yFlip, HAlign hAlign, VAlign vAlign)
	{
		super(SpriteComponent.class);
		this.priority = priority;
		this.ox = ox;
		this.oy = oy;
		this.w = w;
		this.h = h;
		this.xFlip = xFlip;
		this.yFlip = yFlip;
		this.hAlign = hAlign;
		this.vAlign = vAlign;
	}
}
