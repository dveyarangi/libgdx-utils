package game.systems.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Adapter class for rendering based on {@link ShapeRenderer}.
 *
 * @author Fima
 *
 */
public abstract class ShapeRenderingComponent implements IRenderingComponent
{

	private static int [] PROJECTED_CID = new int [] { EntityRenderingSystem.PROJECTED_SHAPER_ID };
	/**
	 * Render the specified entity.
	 * @return false if rendering should be delayed for context switch.
	 */
	@Override
	public abstract void render( Entity entity, IRenderer renderer, IRenderingContext context, float deltaTime );

	/**
	 * TODO: All shapers use the same shape-based context?
	 */
	@Override
	public int [] cid() { return PROJECTED_CID; }

}
