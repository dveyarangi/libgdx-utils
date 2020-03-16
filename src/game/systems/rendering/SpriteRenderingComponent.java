package game.systems.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import game.resources.ResourceFactory;
import game.systems.spatial.ISpatialComponent;
import game.world.Level;

/**
 * Provides sprite rendering method.
 *
 * @author Fima
 */
public class SpriteRenderingComponent implements IRenderingComponent
{
	//static { ComponentType.registerFor(IRenderingComponent.class, SpriteRenderingComponent.class); }

	protected TextureRegion region;


	protected int [] cid;

	public SpriteRenderingComponent()
	{
		cid = new int[1];
	}

	@Override
	public void init( Entity entity, RendererDef def, Level level )
	{
		ResourceFactory factory = level.getModules().getGameFactory();
		if(def instanceof TextureRenderingDef)
		{
			TextureRenderingDef tdef = (TextureRenderingDef) def;
			this.region = factory.getTextureRegion(tdef.textureName);
		}
		else
		{
			RegionRenderingDef tdef = (RegionRenderingDef) def;
			TextureAtlas atlas = factory.getTextureAtlas(tdef.atlasName);
			this.region = atlas.findRegion(tdef.regionName);
		}



		this.cid[0] = TextureID.genid(region.getTexture());
	}

	@Override
	public int [] cid() { return cid; }

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

		this.render( spatial.x(), spatial.y(), spatial.a(), spatial.r(), entity, renderer, context );
	}

	public boolean render( float x, float y, float a, float r, Entity entity, IRenderer renderer, IRenderingContext context )
	{

		SpriteBatch batch = renderer.batch();
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
}
