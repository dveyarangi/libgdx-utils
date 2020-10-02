package game.systems.rendering;

import com.badlogic.gdx.graphics.Color;

import game.resources.NamedTextureAtlas;
import game.util.rendering.HAlign;
import game.util.rendering.VAlign;

public class TileSpriteDef extends RendererDef<TileSpriteComponent>
{

	public NamedTextureAtlas atlas;
	public String regionName;
	public Color color;
	public int tx, ty;
	

	public float w = Float.NaN;
	public float h = Float.NaN;
	public boolean xFlip = false;
	public boolean yFlip = false;
	public VAlign vAlign;
	public HAlign hAlign;

	public TileSpriteDef( NamedTextureAtlas atlas, String regionName )
	{
		super( TileSpriteComponent.class );
		this.atlas = atlas;
		this.regionName = regionName;
	}

	public TileSpriteDef(NamedTextureAtlas atlas, String regionName, Color color, int tx, int ty, float w, float h, boolean xFlip, boolean yFlip, HAlign hAlign, VAlign vAlign)
	{
		super(TileSpriteComponent.class);
		this.atlas = atlas;
		this.regionName = regionName;
		this.color = color;
		this.tx = tx;
		this.ty = ty;
		this.w = w;
		this.h = h;
		this.xFlip = xFlip;
		this.yFlip = yFlip;
		this.hAlign = hAlign;
		this.vAlign = vAlign;
	}
	
}
