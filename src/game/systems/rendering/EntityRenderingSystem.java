package game.systems.rendering;

import java.util.List;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import game.debug.Debug;
import game.resources.ResourceFactory;
import game.systems.rendering.IRenderingContext.DecalContext;
import game.systems.rendering.IRenderingContext.VoidContext;

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
public class EntityRenderingSystem extends EntitySystem implements EntityListener, IRenderer
{
	/**
	 * Accessor to entity rendering component
	 */
	//private ComponentMapper<IRenderingComponent> props = ComponentMapper.getFor(IRenderingComponent.class);


	
	/**
	 * Indexed set of rendering context.
	 */
	private IntMap<IRenderingContext> contexts = new IntMap<IRenderingContext>();

	private int [] contextOrder;

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
	public static final int POST_RENDERING = 1;
	public static final int PROJECTED_SHAPER_ID = 2;
	public static final int DEBUG_ID = 3;
	public static final int DECAL_ID = 4; 
	/**
	 * Actual rendering toolbox
	 */
	private IRenderer renderer;

	private Class <IRenderingComponent> [] rendererTypes;


	public EntityRenderingSystem( IRenderer renderer, ResourceFactory factory, List <Class <? extends IRenderingComponent>> rendererTypes )
	{
		this.factory = factory;
		
		this.renderer = renderer;
		
		this.rendererTypes = rendererTypes.toArray(new Class [rendererTypes.size()]);
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
		contextRenderables.put(ctx.id(), new ObjectMap<IRenderingComponent, Entity>());
		//contextSwitchEntities.put(ctx.id(), new PooledLinkedList<Entity>( Constants.RENDERER_POOL_SIZE ));
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
		contextOrder = new int [5];
		int idx = 0;
		
		// dummy context for entities without a context
		this.registerContext(new VoidContext(PRE_RENDERING));
		contextOrder[idx ++] = PRE_RENDERING;
		
		/*for( TextureHandle textureHandle : factory.getTextures() )
		{
			Texture texture = factory.getTexture( textureHandle.getTextureName() );
			TextureRenderingContext ctx = new TextureRenderingContext(textureHandle.getTextureName(), texture);
			this.registerContext(ctx);
			contextOrder[idx ++] = ctx.id();
		}*/

		// dummy context for entities without a context
		this.registerContext(new VoidContext(POST_RENDERING));
		contextOrder[idx ++] = POST_RENDERING;
		
		// dummy context for entities without a context
		this.registerContext(new DecalContext(DECAL_ID));
		contextOrder[idx ++] = DECAL_ID;
		
		
		// context for rendering shapes
		this.registerContext(new ShapeRenderingContext(PROJECTED_SHAPER_ID, renderer.shaper()));
		contextOrder[idx ++] = PROJECTED_SHAPER_ID;
		
		// TODO: this is debugging context, should be removed or ignored when in
		// production:
		this.registerContext(new DebugRenderingContext(DEBUG_ID));
		contextOrder[idx ++] = DEBUG_ID;
		
		debugPrintContexts();
		
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
		for(Class <IRenderingComponent> type : rendererTypes)
		{
			IRenderingComponent component = entity.getComponent( type );
			if( component == null) continue;
			// adding the entity to appropriate sub-list of the entity mapping:
			int [] cids = component.cid();
			if( cids.length == 0)
				this.entityAdded(POST_RENDERING, component, entity);
			else
				for(int cid : cids)
				{
					this.entityAdded(cid, component, entity);
				}

		}
	}

	private void entityAdded( int cid, IRenderingComponent component, Entity entity )
	{
		//IRenderingContext context = contexts.get(cid);
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

		for( int idx = 0; idx < contextOrder.length; idx ++ )
		{

			int cid = contextOrder[idx];
			
			IRenderingContext context = contexts.get(cid);
			assert context != null;

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

	/**
	 * Called when entity with rendering component is removed from engine
	 */
	@Override
	public void entityRemoved( Entity entity )
	{
		for(Class <IRenderingComponent> type : rendererTypes)
		{
			IRenderingComponent component = entity.getComponent( type );
			if( component == null) continue;

			int [] cids = component.cid();

			for( int cid : cids)
			{
				contextRenderables.get(cid).remove( component );
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
		for(int i = 0; i < contextOrder.length; i ++)
			Debug.log(String.format(" * context id %12d: %s", contextOrder[i], contexts.get(contextOrder[i])));
		Debug.log("=========================================");
	}

}
