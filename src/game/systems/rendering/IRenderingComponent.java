package game.systems.rendering;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

import game.systems.IComponentDef;
import game.world.Level;

/**
 * Interface for renderable part of an entity.
 *
 * The {@link EntityRenderingSystem} registers the renderer components and calls
 * their {@link #render(Entity, IRenderer, IRenderingContext, float)} method.
 *
 * Renderer must define {@link #cid()} - context id that matches a {@link IRenderingContext}.
 *
 * @author Fima
 */
public interface IRenderingComponent extends Component, Poolable
{
	/**
	 * Retrieve ID of rendering context.
	 * Components with same context are rendered in same batch
	 *
	 * @return
	 */
	public abstract int [] cid();

	/**
	 * prepare rendering environment, get resource references
	 * @param entity
	 * @param def
	 * @param factory
	 */
	public abstract void init( Entity entity, IComponentDef<?>def, Level level );

	/**
	 * @param entity
	 * @param renderer
	 * @param context
	 * @param deltaTime
	 * @return whether the provided context is wrong; happens if renderer uses more than one context id.
	 */
	public abstract void render( Entity entity, IRenderer renderer, IRenderingContext context, float deltaTime );


}
