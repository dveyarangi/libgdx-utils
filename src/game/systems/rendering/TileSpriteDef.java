package game.systems.rendering;

import com.badlogic.gdx.graphics.Color;

import game.resources.TextureAtlasName;
import game.util.rendering.HAlign;
import game.util.rendering.VAlign;

public class TileSpriteDef extends RendererDef<TileSpriteComponent>
{

	public TextureAtlasName atlas;
	public String regionName;
	public Color color;
	public float x, y;
	public int tx, ty;
	

	public float w = Float.NaN;
	public float priority = 0;
	public float xOffset = 0, yOffset = 0;
	public boolean xFlip = false;
	public boolean yFlip = false;
	public VAlign vAlign;
	public HAlign hAlign;

	public TileSpriteDef( TextureAtlasName atlas, String regionName )
	{
		super( TileSpriteComponent.class );
		this.atlas = atlas;
		this.regionName = regionName;
	}

	public TileSpriteDef(TextureAtlasName atlas, String regionName, float x, float y, float priority, Color color, int tx, int ty, float w, float xOffset, float yOffset,boolean xFlip, boolean yFlip, HAlign hAlign, VAlign vAlign)
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
		this.xFlip = xFlip;
		this.yFlip = yFlip;
		this.hAlign = hAlign;
		this.vAlign = vAlign;
	}
	
}
