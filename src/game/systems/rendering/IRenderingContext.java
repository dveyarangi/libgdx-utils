package game.systems.rendering;

import game.resources.ResourceFactory;

/**
 * Contexts task is to set up parameters for batch of {@link IRenderingComponent}s
 * @author Fima
 *
 */
public interface IRenderingContext
{
	public static int INVALID_ID = -1;
	/**
	 * Context id is used to map rendering components to this context.
	 * @return
	 */
	public int id();

	/**
	 * Initialize renderer
	 *
	 * @param factory
	 */
	public void init( ResourceFactory factory, IRenderer renderer );

	/**
	 * Setup rendering environment before batch rendering begins.
	 */
	public void begin();

	/**
	 * Finalize batch rendering and restore renderer state.
	 */
	public void end();


}
