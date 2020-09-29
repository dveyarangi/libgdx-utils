package game.systems.rendering;

import game.resources.ResourceFactory;
import lombok.AllArgsConstructor;

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
	 * Common use is to batch together rendering of entities, 
	 * that use same texture/atlas.
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

	/**
	 * Dummy context for rendering components that do not specify context id
	 */
	@AllArgsConstructor
	public static class VoidContext implements IRenderingContext
	{
		private int id;
		
		@Override public int id() { return id;}
		@Override public void init(ResourceFactory factory, IRenderer renderer) { }
		@Override public void begin() { }
		@Override public void end() { }
		@Override public String toString() { return "void context"; }
	}
}
