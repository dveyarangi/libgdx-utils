package game.systems.rendering;


import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import game.debug.Debug;
import game.resources.ResourceFactory;
import game.systems.rendering.IRenderingContext.DecalContext;
import game.systems.rendering.IRenderingContext.VoidContext;
import game.world.Transient;

/**
 * <P>This system iterates over entities with {@link IRenderingComponent} and
 * invokes component's render method.
 *
 * <P>The iteration is performed by batches of components that share the same
* {@link IRenderingContext}, for example, same texture, so that texture binding methods
 * are only called once for a group of entities.
 *
 * <P>The rendering context is determined by {@link IRenderingComponent#cid()}
 * value, that maps the id to a resource context, loaded by resource factory or
 * provided by other modules.
 *
 * <P>
 *
 * @author Fima
 *
 */
@Transient
public class EntityRenderingSystem extends EntitySystem implements EntityListener, IRenderer
{
	/**
	 * Accessor to entity rendering component
	 */
	//private ComponentMapper<IRenderingComponent> props = ComponentMapper.getFor(IRenderingComponent.class);


	
	/**
	 * Indexed set of rendering context.
	 */
	private IntMap<IRenderingContext> contexts = new IntMap<>();

	private Array <IRenderingContext> contextOrder = new Array <> ();

	/**
	 * Mapping of context to entities that use it
	 */
	private IntMap<ObjectMap<IRenderingComponent, Entity>> contextRenderables = new IntMap<>();

	/**
	 * Mapping of context to entities that use it
	 */
//	private IntMap<PooledLinkedList<Entity>> contextSwitchEntities = new IntMap<PooledLinkedList<Entity>>();

	/**
	 *
	 */
	private ResourceFactory factory;

	public static final int PRE_RENDERING = 0;
	public static final int DECAL_ID = 50; 
	public static final int POST_RENDERING = 100;
	public static final int PROJECTED_SHAPER_ID = 150;
	public static final int DEBUG_ID = 151;
	/**
	 * Actual rendering toolbox
	 */
	private IRenderer renderer;


	public EntityRenderingSystem( IRenderer renderer, ResourceFactory factory, List <IRenderingContext> customContexts)
	{
		this.factory = factory;
		
		this.renderer = renderer;
			
		
		// dummy context for entities without a context
		this.registerContext(new VoidContext(PRE_RENDERING));
		
		for(IRenderingContext context : customContexts)
		{
			int priority = context.id();
			if( priority >= PRE_RENDERING && priority < DECAL_ID)
				this.registerContext(context);
				
		}
		
		/*for( TextureHandle textureHandle : factory.getTextures() )
		{
			Texture texture = factory.getTexture( textureHandle.getTextureName() );
			TextureRenderingContext ctx = new TextureRenderingContext(textureHandle.getTextureName(), texture);
			this.registerContext(ctx);
			contextOrder[idx ++] = ctx.id();
		}*/
		
		// dummy context for entities without a context
		this.registerContext(new DecalContext(DECAL_ID));
		
		for(IRenderingContext context : customContexts)
		{
			int priority = context.id();
			if( priority >= POST_RENDERING && priority < PROJECTED_SHAPER_ID)
				this.registerContext(context);
				
		}
		
		// context for rendering shapes
		this.registerContext(new ShapeRenderingContext(PROJECTED_SHAPER_ID, renderer.shaper()));
		
		// TODO: this is debugging context, should be removed or ignored when in
		// production:
		this.registerContext(new DebugRenderingContext(DEBUG_ID));
		
		debugPrintContexts();
		
	}

	@Override
	public void init()
	{
		renderer.init();
	}

	/**
	 * Register a rendering context.
	 *
	 * @param ctx
	 */
	public void registerContext( IRenderingContext ctx )
	{
		assert !contexts.containsKey(ctx.id());
		
		
		contexts.put(ctx.id(), ctx);
		contextOrder.add(ctx);
		
		if( !ctx.isEntityless() )
			contextRenderables.put(ctx.id(), new ObjectMap<IRenderingComponent, Entity>());
	}

	/**
	 * Set this system to listen to entity injection events
	 */
	@Override
	public void addedToEngine( Engine engine )
	{
		super.addedToEngine(engine);

		this.init();

		// TODO: contexts should have array of cids

		// TODO: context rendering priorities

		
		// TODO: load and use several textures in the same time;
		// may use combined contexts.

		//Family family = Family.one( rendererTypes ).get();

		// registering as listener for all entities with renderer component:
		for( IRenderingContext context : contexts.values() )
			context.init(factory, renderer);

		engine.addEntityListener(this);
	}


	/**
	 * Called when entity with rendering component is added to the engine
	 */
	@Override
	public void entityAdded( Entity entity )
	{
//		IRenderingComponent c = entity.getComponent(IRenderingComponent.class);
		for(Component component : entity.getComponents())
		{
			if(!(component instanceof IRenderingComponent))
				continue;
			
			IRenderingComponent rcomp = (IRenderingComponent) component;
			// adding the entity to appropriate sub-list of the entity mapping:
			int [] cids = rcomp.cid();
			if( cids.length == 0)
				this.addEntityToContext(POST_RENDERING, rcomp, entity);
			else
				for(int cid : cids)
				{
					this.addEntityToContext(cid, rcomp, entity);
				}

		}
	}

	private void addEntityToContext( int cid, IRenderingComponent component, Entity entity )
	{
		IRenderingContext context = contexts.get(cid);
		if( context.isEntityless())
			return;

		//context.entityAdded(entity);
		// add ing the entity to appropriate sub-list of the entity mapping:
		ObjectMap<IRenderingComponent, Entity> renderables = contextRenderables.get(cid);
		if(renderables == null)
			throw new IllegalStateException(
					"No rendering context with id " + cid + " found:\n" + Debug.entityToString(entity));

		renderables.put( component, entity );
	}

	/**
	 * We do nothing on update, the {@link #draw} is called explicitly by
	 * {@link Level}
	 */
	@Override
	public void update( float deltaTime )
	{
		renderer.update(deltaTime);

		super.update(deltaTime);
	}

	/**
	 * Iterates over the contexts and evokes renderers in the contexts.
	 * TODO: evaluate performance tradeoff if instead
	 * first check renderers and sort them by new cids, and then render
	 *
	 * @param entities
	 * @param switchEntities
	 */
	public void draw( float delta )
	{
		// rendering all units, grouped by rendering context:

		for( int idx = 0; idx < contextOrder.size; idx ++ )
		{

			IRenderingContext context = contextOrder.get(idx);
			assert context != null;
			if( context.isEntityless())
			{
				context.begin();
				context.end();
			}
			else
			{
				int cid = context.id();
				Entries<IRenderingComponent, Entity> renderables = contextRenderables.get(cid).iterator();
				if( ! renderables.hasNext )
					continue;
	
				context.begin();
				while( renderables.hasNext )
				{
					Entry<IRenderingComponent, Entity> entry = renderables.next();
					IRenderingComponent rend = entry.key;
					Entity entity = entry.value;
	
					rend.render(entity, renderer, context, delta);
				}
				context.end();
			}
		}	
		
	}

	/**
	 * Called when entity with rendering component is removed from engine
	 */
	@Override
	public void entityRemoved( Entity entity )
	{
		for(Component component : entity.getComponents())
		{
			if(!(component instanceof IRenderingComponent))
				continue;
			
			IRenderingComponent rcomp = (IRenderingComponent) component;
			int [] cids = rcomp.cid();

			for( int cid : cids)
			{
				contextRenderables.get(cid).remove( rcomp );
				//IRenderingContext context = contexts.get(cid);
				//context.entityRemoved(entity);

			}
		}
	}

	/**
	 * Cleanup
	 */
	@Override
	public void removedFromEngine( Engine engine )
	{
		engine.removeEntityListener(this);
		contextRenderables.clear();
		contextRenderables = null;
		contexts.clear();
		contexts = null;
		super.removedFromEngine(engine);
	}

	@Override
	public SpriteBatch sprites()
	{
		return renderer.sprites();
	}

	@Override
	public ShapeRenderer shaper()
	{
		return renderer.shaper();
	}
	
	@Override
	public DecalBatch decals()
	{
		return renderer.decals();
	}

	@Override
	public void dispose()
	{
		renderer.dispose();
	}

	@Override
	public void resize( int screenWidth, int screenHeight )
	{
		renderer.resize(screenWidth, screenHeight);
	}
	
	private void debugPrintContexts()
	{
		Debug.log("=== rendering contexts ==================");
		for(int i = 0; i < contextOrder.size; i ++)
			Debug.log(String.format(" * context id %12d: %s", contextOrder.get(i).id(), contextOrder.get(i)));
		Debug.log("=========================================");
	}

	@Override
	public Camera camera() { return renderer.camera(); }

}
