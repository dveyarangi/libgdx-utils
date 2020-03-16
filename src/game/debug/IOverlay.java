package game.debug;

import game.systems.rendering.IRenderer;

/**
 * Unit decorator
 *
 * @author Fima
 *
 */
public interface IOverlay<E>
{

	public void draw( final IRenderer renderer );

	/***
	 * If false, overlay will be rendered in absolute screen coordinates system
	 *
	 * @return
	 */
	public boolean isProjected();
}
