package game.systems.rendering;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Defines interface for set of rendering tools provided to rendering components.
 * @author Fima
 *
 */
public interface IRenderer
{
	/**
	 * Texture rendering batch
	 * @return
	 */
	SpriteBatch batch();

	/**
	 * Shapes rendering batch
	 * @return
	 */
	ShapeRenderer shaper();

	/**
	 * Prepare rendering stuff
	 */
	void init();

	/**
	 * Process pending rendering operations, if any
	 * @param deltaTime
	 */
	void update( float deltaTime );

	/**
	 * Called when viewport size changes
	 * @param screenWidth
	 * @param screenHeight
	 */
	void resize( int screenWidth, int screenHeight );

	/**
	 * Clean up
	 */
	void dispose();

}
