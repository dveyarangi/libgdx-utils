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

	public default void onShow() {}
	public default void onHide() {}
	
	public void draw( final IRenderer renderer );

	/***
	 * If false, overlay will be rendered in absolute screen coordinates system
	 *
	 * @return
	 */
	public default boolean useWorldCoordinates() { return false; }

}
