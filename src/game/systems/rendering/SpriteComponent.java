package game.systems.rendering;

import com.badlogic.ashley.core.ComponentType;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;

import game.systems.IComponentDef;
import game.systems.spatial.ISpatialComponent;
import game.util.Equals;
import game.world.Level;
import game.world.saves.Props;
import lombok.Getter;

/**
 * Provides sprite rendering method.
 *
 * @author Fima
 */
public abstract class SpriteComponent implements ISpriteComponent
{
	
	static
	{   // map as rendering component:
		ComponentType.registerFor(ISpriteComponent.class, SpriteComponent.class);
	}
	
	public static final String PROP_SX = "sx";
	public static final String PROP_SY = "sy";
	public static final String PROP_DX = "dx";
	public static final String PROP_DY = "dy";
	public static final String PROP_DZ = "dz";
	public static final float DEFAULT_SX = 1;
	public static final float DEFAULT_SY = 1;
	public static final float DEFAULT_DX = 0;
	public static final float DEFAULT_DY = 0;
	public static final float DEFAULT_DZ = 0;
	/**
	 * Origin point for scaling (TODO: and rotation) transformation
	 * STATIC property
	 */
	@Getter float ox, oy;

	/**
	 * Sprite width on x and y axes
	 * DYNAMIC property
	 */
	@Getter float sx, sy;

	/**
	 * Offsets on x, y and z position (defined by by SpatialComponent)
	 * STATIC/DERIVED property
	 */
	@Getter float dx, dy, dz;

	/**
	 * DYNAMIC property
	 * Region area and x/y flip
	 */
	@Getter protected TextureRegion region = new TextureRegion();
	
	@Getter protected Color color = new Color();

	protected Decal decal = Decal.newDecal(this.sx, this.sy, this.region, false);

	protected int [] cid;



	public SpriteComponent()
	{
		cid = new int[1];
	}

	@Override
	public void init( Entity entity, IComponentDef def, Level level )
	{
		SpriteDef sdef = (SpriteDef) def;

		//this.region.flip(sdef.xFlip, sdef.yFlip);

		float rw = region.getRegionWidth();
		float rh = region.getRegionHeight();
		region.getTexture().setFilter(TextureFilter.MipMap, TextureFilter.MipMap);



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
		//this.ox = sdef.ox; this.oy = sdef.oy;
		this.sx = sdef.w; this.sy = sdef.h;
		float width = sdef.w;
		float height = rh / rw * sdef.w;

		this.dx = width*sdef.xOffset;
		this.dy = height*sdef.yOffset;
		this.dz = sdef.zOffset;

		this.cid[0] = EntityRenderingSystem.DECAL_ID;
		//boolean hasTransparency = false;
		//this.decal.setPosition(spatial.x()-dx, spatial.y()-dy, -50);
		this.decal.setDimensions(this.sx, this.sy);

		this.decal.setTextureRegion(region);

		this.decal.setColor(color);

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
		decal.setPosition(spatial.x()-dx, spatial.y()-dy, spatial.z()-dz);
		decal.setScale(spatial.s());
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
	/*
	public boolean useSpatialDimensions()
	{
		return Float.isNaN(ox);
	}*/

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


	//@Getter float ox, oy, sx, sy;
	//@Getter float dx, dy;
	//@Getter private float dz;
	//@Getter float priority;

	//@Getter protected TextureRegion region = new TextureRegion();

	//protected Decal decal = Decal.newDecal(this.sx, this.sy, this.region, false);

	//protected int [] cid;

	@Override
	public void scale(float size)
	{
		// TODO set dw multiplier for spatial.s()
		
	}

	public void save(SpriteDef def, Props props)
	{

		if(!Equals.eq(def.w, sx)) props.put(PROP_SX, sx);
		if(!Equals.eq(def.h, sy)) props.put(PROP_SY, sy);
	}

	public void load(SpriteDef def, Props props)
	{
		this.sx = props.get(PROP_SX, def.w);
		this.sy = props.get(PROP_SY, def.h);

	}

}
