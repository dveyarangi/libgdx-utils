package game.systems.rendering;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class RegionRenderingDef extends RendererDef<SpriteComponent>
{
	public TextureAtlas atlas;
	public String regionName;

	public RegionRenderingDef( TextureAtlas atlas, String regionName )
	{
		super( SpriteComponent.class );
		this.atlas = atlas;
		this.regionName = regionName;
	}
}
