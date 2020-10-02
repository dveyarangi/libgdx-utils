package game.systems.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import game.debug.Debug;
import game.resources.ResourceFactory;
import game.systems.IComponentDef;
import game.systems.spatial.ISpatialComponent;
import game.world.Level;
import lombok.Getter;

/**
 * Provides sprite rendering method.
 *
 * @author Fima
 */
public class SpriteComponent implements IRenderingComponent
{
	//static { ComponentType.registerFor(IRenderingComponent.class, SpriteRenderingComponent.class); }

	@Getter protected TextureRegion region = new TextureRegion();
	@Getter float ox, oy, sx, sy;
	@Getter float dx, dy;

	protected int [] cid;
	
	
	//protected Decal decal = Decal.newDecal(this.sx, this.sy, this.region);


	public SpriteComponent()
	{
		cid = new int[1];
	}

	@Override
	public void init( Entity entity, IComponentDef def, Level level )
	{
		ResourceFactory factory = level.getModules().getGameFactory();
		if(def instanceof TextureRenderingDef)
		{
			TextureRenderingDef tdef = (TextureRenderingDef) def;
			TextureRegion origRegion = factory.getTextureRegion(tdef.textureName);
			this.region.setRegion(origRegion);
			this.ox = tdef.ox; this.oy = tdef.oy; this.sx = tdef.w; this.sy = tdef.h;
			
		}
		else
		{
			RegionRenderingDef tdef = (RegionRenderingDef) def;
			TextureAtlas atlas = tdef.atlas;
			TextureRegion origRegion;
			if( tdef.regionName == null)
			{
				origRegion = atlas.getRegions().get(0);
				Debug.warn("RegionRenderingDef does not specify regionName");
			}
			else
				origRegion = atlas.findRegion(tdef.regionName);
			
			this.region.setRegion(origRegion);
			this.region.flip(tdef.xFlip, tdef.yFlip);
			
			float rw = region.getRegionWidth();
			float rh = region.getRegionHeight();
		
			float width = tdef.w;
			float height = rh / rw * tdef.w; 
			
			switch(tdef.hAlign)
			{
			case LEFT: dx = 0.5f; break;
			default: case CENTER:dx = 0.5f*width; break;
			case RIGHT: dx = width-0.5f; break;
			}
			
			switch(tdef.vAlign)
			{
			case TOP: dy = height - 0.5f; break;
			default: case CENTER: dy = 0.5f*height; break;
			case BOTTOM: dy =  0.5f; break;
			}
			
			this.ox = tdef.ox; this.oy = tdef.oy; this.sx = tdef.w; this.sy = tdef.h;
		}
		
		



		this.cid[0] = TextureID.genid(region.getTexture());
		
		//this.cid[0] = EntityRenderingSystem.DECAL_ID;
		//boolean hasTransparency = false;
		//this.decal = Decal.newDecal(this.sx, this.sy, this.region, hasTransparency);

	}

	@Override
	public int [] cid() { return cid; }
	
	
	public void setRegion(TextureRegion region)
	{
		this.region = region;
		this.cid[0] = TextureID.genid(region.getTexture());
	}

	@Override
	public void reset()
	{
		region = null;
		cid[0] = IRenderingContext.INVALID_ID;
	}

	@Override
	public void render( Entity entity, IRenderer renderer, IRenderingContext context, float deltaTime )
	{
		ISpatialComponent spatial = ISpatialComponent.get(entity);

		if( useSpatialDimensions())
			this.render( spatial.x(), spatial.y(), spatial.a(), spatial.r(), entity, renderer, context );
		else
		{
			//this.render( spatial.x(), spatial.y(), spatial.a(), spatial.r(), entity, renderer, context );
			
			float rw = region.getRegionWidth();
			float rh = region.getRegionHeight();
			float scale = rw > rh ? (2*spatial.r() / rw) : (2*spatial.r()/rh);
			renderer.sprites().draw(region,
					spatial.x()-dx, // lower right angle
					// position
					spatial.y()-dy,
					ox, oy, // origin
					// coordinate
					sx, sy, // dimensions
					1, 1,// scaling
					0 // orientation
					);
		}
		
		//decal.setPosition(spatial.x(), spatial.y(), 1);
		//renderer.decals().add(decal);

	}

	public boolean render( float x, float y, float a, float r, Entity entity, IRenderer renderer, IRenderingContext context )
	{

		SpriteBatch batch = renderer.sprites();
		float rw = region.getRegionWidth();
		float rh = region.getRegionHeight();
		float scale = rw > rh ? (2*r / rw) : (2*r/rh);

		batch.draw(region,
				x - rw / 2, // lower right angle
				// position
				y - rh / 2,
				rw / 2, rh / 2, // origin
				// coordinate
				rw, rh, // dimensions
				scale, scale,// scaling
				a - 90 // orientation
				);

		return false;
	}
	
	public boolean useSpatialDimensions()
	{
		return Float.isNaN(ox);
	}
}
