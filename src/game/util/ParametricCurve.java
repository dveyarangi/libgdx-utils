/**
 * 
 */
package game.util;

import com.badlogic.gdx.math.Vector2;

/**
 * @author dveyarangi
 *
 */

public interface ParametricCurve
{
	public Vector2 at( Vector2 target, float t );
}
