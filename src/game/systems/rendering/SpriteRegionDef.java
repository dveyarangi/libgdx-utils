package game.systems.rendering;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import game.util.rendering.HAlign;
import game.util.rendering.VAlign;

public class SpriteRegionDef extends SpriteDef
{
	public TextureAtlas atlas;
	public String regionName;

	public SpriteRegionDef(TextureAtlas atlas, String regionName, float priority, float ox, float oy, float w, float h, boolean xFlip, boolean yFlip, HAlign hAlign, VAlign vAlign)
	{
		super(priority, ox, oy, w, h, xFlip, yFlip, hAlign, vAlign);
		this.atlas = atlas;
		this.regionName = regionName;
	}
	

}
