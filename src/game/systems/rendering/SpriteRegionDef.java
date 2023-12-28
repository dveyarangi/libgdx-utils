package game.systems.rendering;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class SpriteRegionDef extends SpriteDef <SpriteRegionComponent>
{
	public TextureAtlas atlas;
	public String regionName;

	public SpriteRegionDef(TextureAtlas atlas, String regionName, float priority, float ox, float oy, float zOffset, float w, float h, Color tint)
	{
		super(ox, oy, zOffset, w, h, tint);
		this.atlas = atlas;
		this.regionName = regionName;
	}

	@Override
	public Class <SpriteRegionComponent> getComponentClass() { return SpriteRegionComponent.class; }


}
