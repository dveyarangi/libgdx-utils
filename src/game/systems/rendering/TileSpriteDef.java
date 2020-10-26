package game.systems.rendering;

import com.badlogic.gdx.graphics.Color;

import game.resources.TextureAtlasName;

public class TileSpriteDef extends RendererDef<TileSpriteComponent>
{

	public TextureAtlasName atlas;
	public String regionName;
	public Color color;
	public float x, y;
	public int tx, ty;
	

	public float w = Float.NaN;
	public float dw = 1;
	public float priority = 0;
	public float xOffset = 0, yOffset = 0, zOffset = 0;
	public boolean xFlip = false;
	public boolean yFlip = false;
	
	public int randomParallaxes = 0;
	public float xRandom = 0;
	public float yRandom = 0;
	public float wRandom = 0;

	public TileSpriteDef( TextureAtlasName atlas, String regionName )
	{
		super( TileSpriteComponent.class );
		this.atlas = atlas;
		this.regionName = regionName;
	}

	public TileSpriteDef(TextureAtlasName atlas, String regionName, float x, float y, float priority, Color color, int tx, int ty, float w, float dw, float xOffset, float yOffset, float zOffset, boolean xFlip, boolean yFlip)
	{
		super(TileSpriteComponent.class);
		this.atlas = atlas;
		this.regionName = regionName;
		this.x = x;
		this.y = y;
		this.priority = priority;
		this.color = color;
		this.tx = tx;
		this.ty = ty;
		this.w = w;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.zOffset = zOffset;
		this.xFlip = xFlip;
		this.yFlip = yFlip;

	}
	
}
