package game.systems.rendering;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import game.util.rendering.HAlign;
import game.util.rendering.VAlign;

public class RegionRenderingDef extends RendererDef<SpriteComponent>
{
	public TextureAtlas atlas;
	public String regionName;
	
	public float ox = Float.NaN;
	public float oy = Float.NaN;
	public float w = Float.NaN;
	public float h = Float.NaN;
	public boolean xFlip = false;
	public boolean yFlip = false;
	public HAlign hAlign = HAlign.CENTER;
	public VAlign vAlign = VAlign.CENTER;

	public RegionRenderingDef( TextureAtlas atlas, String regionName )
	{
		super( SpriteComponent.class );
		this.atlas = atlas;
		this.regionName = regionName;
	}

	public RegionRenderingDef(TextureAtlas atlas, String regionName, float ox, float oy, float w, float h, boolean xFlip, boolean yFlip, HAlign hAlign, VAlign vAlign)
	{
		super(SpriteComponent.class);
		this.atlas = atlas;
		this.regionName = regionName;
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
