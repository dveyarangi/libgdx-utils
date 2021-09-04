package game.systems.rendering;

import com.badlogic.gdx.graphics.Color;

import game.resources.TextureAtlasName;

/**
 *
 * @author fimar
 */
public class TileSpriteDef extends RendererDef<TileSpriteComponent>
{

	public TextureAtlasName atlas;
	public String regionName;
	public Color color;

	/** Sprite size coefficient, applied to entity's spatial size */
	public float sizeCoef = 1;
	/** Sprite position offset, relative to entity's spatial position */
	public float xOffset = 0, yOffset = 0, zOffset = 0;


	public TileSpriteDef( TextureAtlasName atlas, String regionName )
	{
		super(  );
		this.atlas = atlas;
		this.regionName = regionName;
	}

	public TileSpriteDef(TextureAtlasName atlas, String regionName, Color color, float sizeCoef, float xOffset, float yOffset, float zOffset)
	{
		super();
		this.atlas = atlas;
		this.regionName = regionName;

		this.color = color;

		this.sizeCoef = sizeCoef;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.zOffset = zOffset;


	}

	@Override
	public Class<TileSpriteComponent> getComponentClass() { return TileSpriteComponent.class; }


}
