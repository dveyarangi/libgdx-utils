package game.systems.rendering;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class RegionRenderingDef extends RendererDef<SpriteComponent>
{
	public TextureAtlas atlas;
	public String regionName;
	
	public float ox = Float.NaN;
	public float oy = Float.NaN;
	public float w = Float.NaN;
	public float h = Float.NaN;

	public RegionRenderingDef( TextureAtlas atlas, String regionName )
	{
		super( SpriteComponent.class );
		this.atlas = atlas;
		this.regionName = regionName;
	}

	public RegionRenderingDef(TextureAtlas atlas, String regionName, float ox, float oy, float w, float h)
	{
		super(SpriteComponent.class);
		this.atlas = atlas;
		this.regionName = regionName;
		this.ox = ox;
		this.oy = oy;
		this.w = w;
		this.h = h;
	}
	

}
