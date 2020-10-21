package game.systems.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;

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
	@Getter float priority;

	protected int [] cid;
	
	
	protected Decal decal = Decal.newDecal(this.sx, this.sy, this.region, true);


	public SpriteComponent()
	{
		cid = new int[1];
	}

	@Override
	public void init( Entity entity, IComponentDef def, Level level )
	{
		SpriteDef sdef = (SpriteDef) def;
		ResourceFactory factory = level.getModules().getGameFactory();
		TextureRegion origRegion;
		if(def instanceof SpriteTextureDef)
		{
			SpriteTextureDef tdef = (SpriteTextureDef) def;
			
			origRegion = factory.getTextureRegion(tdef.textureName.getName());
			
		}
		else
		{
			SpriteRegionDef tdef = (SpriteRegionDef) def;
			TextureAtlas atlas = tdef.atlas;
			if( tdef.regionName == null)
			{
				origRegion = atlas.getRegions().get(0);
				Debug.warn("RegionRenderingDef does not specify regionName");
			}
			else
				origRegion = atlas.findRegion(tdef.regionName);
			
		}
		
		
		this.region.setRegion(origRegion);
		this.region.flip(sdef.xFlip, sdef.yFlip);
		
		float rw = region.getRegionWidth();
		float rh = region.getRegionHeight();
		region.getTexture().setFilter(TextureFilter.MipMap, TextureFilter.MipMap);
	
		float width = sdef.w;
		float height = rh / rw * sdef.w; 
		
		/* FOR SPRITE BATCH:
		switch(sdef.hAlign)
		{
		case LEFT: dx = 0.5f; break;
		default: case CENTER:dx = 0.5f*width; break;
		case RIGHT: dx = width-0.5f; break;
		}
		
		switch(sdef.vAlign)
		{
		case TOP: dy = height - 0.5f; break;
		default: case CENTER: dy = 0.5f*height; break;
		case BOTTOM: dy =  0.5f; break;
		}*/
		
		// FOR DECAL BATCH:
		/*switch(sdef.hAlign)
		{
		case LEFT: dx = -0.5f*width+0.5f; break;
		default: case CENTER:dx = 0; break;
		case RIGHT: dx = 0.5f*width-0.5f; break;
		}
		
		switch(sdef.vAlign)
		{
		case TOP: dy = height-0.5f; break;
		default: case CENTER: dy =0; break;
		case BOTTOM: dy = -0.5f*height+0.5f; break;
		}		*/
		this.ox = sdef.ox; this.oy = sdef.oy; this.sx = sdef.w; this.sy = sdef.h;
		
		
		this.dx = width*ox;
		this.dy = height*oy;
		priority = sdef.priority;
		this.cid[0] = EntityRenderingSystem.DECAL_ID;
		//boolean hasTransparency = false;
		//this.decal.setPosition(spatial.x()-dx, spatial.y()-dy, -50);
		this.decal.setDimensions(this.sx, this.sy);
		this.decal.setTextureRegion(region);



	}

	@Override
	public int [] cid() { return cid; }
	
	
	public void setRegion(TextureRegion region)
	{
		this.region = region;
		this.decal.setTextureRegion(region);
		//this.cid[0] = TextureID.genid(region.getTexture());
	}

	@Override
	public void reset()
	{
		region = new TextureRegion();
		cid[0] = IRenderingContext.INVALID_ID;
	}

	@Override
	public void render( Entity entity, IRenderer renderer, IRenderingContext context, float deltaTime )
	{
		ISpatialComponent spatial = ISpatialComponent.get(entity);

		/*if( useSpatialDimensions())
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
		}*/
		renderer.decals();
		decal.setPosition(spatial.x()-dx, spatial.y()-dy, priority);

		renderer.decals().add(decal);

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
	
	public void directRight()
	{
		if( this.region.isFlipX())
			return;
		this.region.flip(true, false);
		decal.setTextureRegion(region);
	}
	
	public void directLeft()
	{
		if( !this.region.isFlipX())
			return;
		this.region.flip(true, false);
		decal.setTextureRegion(this.region);
		//renderer.entityUpdated(this);
	}

}
