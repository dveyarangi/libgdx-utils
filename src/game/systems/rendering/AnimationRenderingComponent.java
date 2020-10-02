package game.systems.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import game.systems.IComponentDef;
import game.systems.spatial.ISpatialComponent;
import game.util.IntArrayPool;
import game.world.Level;

/**
 * Provides definition and rendering method for animated entities.
 *
 * @author Fima
 */
public class AnimationRenderingComponent implements IRenderingComponent
{
	static
	{   // map as rendering component:
		//ComponentType.registerFor(IRenderingComponent.class, AnimationRenderingComponent.class);
	}
	
	static IntArrayPool cidPool = new IntArrayPool();

	/**
	 * Rendering context id; defines the texture scope for this animation;
	 */
	private int [] cid;

	private float lifetime;

	/**
	 * Animation atlas and timers
	 */
	private Animation <TextureRegion> animation;

	/**
	 * Animation speed modifier
	 */
	private float timeModifier = 1;

	public AnimationRenderingComponent()
	{
	}

	@Override
	public void init( Entity entity, IComponentDef def, Level level )
	{
		
		
		AnimationRenderingDef adef = (AnimationRenderingDef) def;
		this.animation = level.getModules().getGameFactory().getAnimation(adef.atlasName);
		//		this.animation = adef.animation;

		this.timeModifier = adef.timeModifier;

		// TODO: unpooled, probably animation object should provide premade cid array
		/*Set <Texture> textures = new HashSet <Texture> ();

		for(Object region : animation.getKeyFrames())
		{
			TextureRegion reg = (TextureRegion) region;
			textures.add( reg.getTexture() );
		}
			

		this.cid = new int[textures.size()];
		int idx = 0;
		for(Texture texture : textures)
			cid[idx++] = TextureID.genid( texture );*/
		Object [] regions = animation.getKeyFrames();
		this.cid = cidPool.obtain(regions.length);
		for(int idx = 0; idx < regions.length; idx ++)
			this.cid[idx] = TextureID.genid(((TextureRegion)regions[idx]).getTexture());
	}

	@Override public int [] cid() { return cid; }

	@Override
	public void render( Entity entity, IRenderer renderer, IRenderingContext context, float deltaTime )
	{
		ISpatialComponent spatial = ISpatialComponent.get(entity);

		SpriteBatch sprites = renderer.sprites();

		lifetime += deltaTime;

		float animationTime = timeModifier * lifetime;

		TextureRegion region = animation.getKeyFrame(animationTime, true);
		// verifying that currently bound texture is the one for this animation step:
		if( context.id() != TextureID.genid(region.getTexture()))
			return;

		sprites.draw(region,
				spatial.x() - region.getRegionWidth() / 2, // lower right angle
				spatial.y() - region.getRegionHeight() / 2,

				region.getRegionWidth() / 2,
				region.getRegionHeight() / 2, // origin coordinate

				region.getRegionWidth(),
				region.getRegionHeight(), // dimensions

				spatial.r() / region.getRegionWidth(),
				spatial.r() / region.getRegionWidth(), // scaling

				spatial.a() // orientation
		);

		return;
	}

	@Override
	public void reset()
	{
		animation = null;
		timeModifier = 1;
		lifetime = 0;
		cidPool.free(cid);
		this.cid = null;
		
	}


}
