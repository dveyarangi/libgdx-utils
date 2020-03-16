/**
 *
 */
package game.systems.control;

import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Defines camera behavior.
 * 
 */
public abstract class ICameraController
{

	public abstract OrthographicCamera camera();

	/**
	 * Move camera to target coordinate and zoom value
	 * 
	 * @param tx
	 * @param ty
	 * @param tz
	 */
	public abstract void zoomTo( float tx, float ty, float tz );

	/**
	 * Move camera to target coordinate
	 * 
	 * @param tx
	 * @param ty
	 */
	public abstract void moveBy( float tx, float ty );

	public void zoomTo( float tz )
	{
		zoomTo(camera().position.x, camera().position.y, tz);
	}

	/**
	 * Perform camera movement stuff
	 * 
	 * @param delta
	 */
	abstract void update( float delta );

	/**
	 * 
	 * 
	 * @param width
	 * @param height
	 */
	abstract void resize( int width, int height );

}
