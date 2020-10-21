package game.systems.rendering;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import game.resources.ResourceFactory;
import game.systems.IComponentDef;
import game.world.Level;

public class TileSpriteComponent implements IRenderingComponent
{

	static ComponentMapper<TileSpriteComponent> MAPPER = ComponentMapper.getFor(TileSpriteComponent.class);
	
	
	TileSpriteDef def;
	
	TextureRegion region = new TextureRegion();
	float dx;
	float dy;
	
	float width;
	float height;
	
	private int [] cid = new int [] {};


	public TileSpritesRenderer renderer;

	/**
	 * Size scale
	 */
	float dw = 1;


	public static TileSpriteComponent get( Entity entity )
	{
		return MAPPER.get(entity);
	}

	@Override
	public void reset()
	{
		def = null;
	}

	@Override
	public int[] cid() { return cid; }

	@Override
	public void init(Entity entity, IComponentDef<?> rendererDef, Level level)
	{
		this.def = (TileSpriteDef) rendererDef;
		
		TextureAtlas atlas = ResourceFactory.getTextureAtlas(def.atlas.getName());
		TextureRegion origRegion = atlas.findRegion(def.regionName);
		
		region.setRegion(origRegion);
		region.flip(def.xFlip, def.yFlip);
		
		
		float rw = region.getRegionWidth();
		float rh = region.getRegionHeight();
	
		width = def.w;
		height = rh / rw * def.w; 
		
		/*switch(def.hAlign)
		{
		case LEFT: dx = 0.5f; break;
		default: case CENTER:dx = 0.5f*width; break;
		case RIGHT: dx = width-0.5f; break;
		}*/
		dx = (0.5f - def.xOffset)*width;
	
		/*switch(def.vAlign)
		{
		case TOP: dy = height - 0.5f; break;
		default: case CENTER: dy = 0.5f*height; break;
		case BOTTOM: dy =  0.5f; break;
		}*/
		dy = (0.5f - def.yOffset)*height;
		
		this.dw = def.dw;

	}

	@Override
	public void render(Entity entity, IRenderer renderer, IRenderingContext context, float deltaTime)
	{
		// TODO Auto-generated method stub
		
	}
	
	public void scale(float dw)
	{
		this.dw = dw;
		
		dx = (0.5f - def.xOffset)*dw*width;
		
		dy = (0.5f - def.yOffset)*dw*height;
		
		renderer.entityUpdated(this);
	}

	public void setRegion(TextureRegion region)
	{
		this.region.setRegion(region);;
		
		renderer.entityUpdated(this);
	}

	public void directRight()
	{
		if( this.region.isFlipX())
			return;
		this.region.flip(true, false);
		renderer.entityUpdated(this);
	}
	
	public void directLeft()
	{
		if( !this.region.isFlipX())
			return;
		this.region.flip(false, false);
		renderer.entityUpdated(this);
	}

}
