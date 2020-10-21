package game.systems.rendering;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class SpriteRegionDef extends SpriteDef
{
	public TextureAtlas atlas;
	public String regionName;

	public SpriteRegionDef(TextureAtlas atlas, String regionName, float priority, float ox, float oy, float w, float h, boolean xFlip, boolean yFlip)
	{
		super(priority, ox, oy, w, h, xFlip, yFlip);
		this.atlas = atlas;
		this.regionName = regionName;
	}
	

}
