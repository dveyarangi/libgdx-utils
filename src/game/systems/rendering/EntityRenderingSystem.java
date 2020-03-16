package game.systems.rendering;

import java.util.List;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectMap.Entries;
import com.badlogic.gdx.utils.ObjectMap.Entry;

import game.resources.ResourceFactory;
import game.resources.TextureHandle;

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
	private IntMap<ObjectMap<IRenderingComponent, Entity>> entities = new IntMap<>();

	/**
	 * Mapping of context to entities that use it
	 */
//	private IntMap<PooledLinkedList<Entity>> contextSwitchEntities = new IntMap<PooledLinkedList<Entity>>();

	/**
	 *
	 */
	private ResourceFactory factory;

	public static final int PROJECTED_SHAPER_ID = 1;

	/**
	 * Actual rendering toolbox
	 */
	private IRenderer renderer;

	private Class <IRenderingComponent> [] rendererTypes;

	public EntityRenderingSystem( IRenderer renderer, ResourceFactory factory, List <Class <? extends IRenderingComponent>> rendererTypes )
	{
		this.factory = factory;
		this.renderer = renderer;
		this.rendererTypes = (Class<IRenderingComponent>[]) new Class [rendererTypes.size()];
		this.rendererTypes = rendererTypes.toArray(this.rendererTypes);
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
		entities.put(ctx.id(), new ObjectMap<IRenderingComponent, Entity>());
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
		contextOrder = new int [factory.getTextures().size()+1];
		int idx = 0;
		for( TextureHandle textureHandle : factory.getTextures() )
		{
			Texture texture = factory.getTexture( textureHandle.getTextureName() );
			TextureRenderingContext ctx = new TextureRenderingContext(texture);
			this.registerContext(ctx);
			contextOrder[idx ++] = ctx.id();
		}

		// TODO: this is debugging context, should be removed or ignored when in
		// production:
		this.registerContext(new ShapeRenderingContext(PROJECTED_SHAPER_ID, renderer.shaper()));
		contextOrder[idx ++] = PROJECTED_SHAPER_ID;

		// registering as listener for all entities with renderer component:
		Family family = Family.one( rendererTypes ).get();

		for( IRenderingContext context : contexts.values() )
		{
			context.init(factory, renderer);
		}

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
			for(int cid : cids)
				this.entityAdded(cid, component, entity);

		}
	}

	private void entityAdded( int cid, IRenderingComponent component, Entity entity )
	{
		// add ing the entity to appropriate sub-list of the entity mapping:
		ObjectMap<IRenderingComponent, Entity> elist = entities.get(cid);
		if(elist == null)
			throw new IllegalStateException("No rendering context with id " + cid + " found.");

		elist.put( component, entity );
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

		// iterating over contexts:
		for( int idx = 0; idx < contextOrder.length; idx ++ )
		{

			int cid = contextOrder[idx];
			// getting context object:
			IRenderingContext context = contexts.get(cid);
			assert context != null;

			// rendering all units in this context scope:
			ObjectMap<IRenderingComponent, Entity> elist = entities.get(cid);
			if( elist.size == 0 )
				continue;

			// starting context:
			context.begin();

			//elist.iterator();

			Entries<IRenderingComponent, Entity> iterator = elist.iterator();
			while( iterator.hasNext )
			{
				Entry<IRenderingComponent, Entity> entry = iterator.next();
				IRenderingComponent rend = entry.key;
				Entity entity = entry.value;

				rend.render(entity, renderer, context, delta);
			}
			// finishing context task:
			context.end();
		}	
	}

	/**
	 * Called when entity with rendering component is removed from engine
	 */
	@Override
	public void entityRemoved( Entity entity )
	{
		ObjectMap<IRenderingComponent, Entity> elist = null;

		for(Class <IRenderingComponent> type : rendererTypes)
		{
			IRenderingComponent component = entity.getComponent( type );
			if( component == null) continue;

			int [] cids = component.cid();

			for( int cid : cids)
			{
				elist = entities.get(cid);
				elist.remove( component );
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
		entities.clear();
		entities = null;
		contexts.clear();
		contexts = null;

	}

	@Override
	public SpriteBatch batch()
	{
		return renderer.batch();
	}

	@Override
	public ShapeRenderer shaper()
	{
		return renderer.shaper();
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

}
