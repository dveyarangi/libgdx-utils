package game.systems.rendering;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Defines interface for set of rendering tools provided to rendering components.
 * @author Fima
 *
 */
public interface IRenderer
{
	/**
	 * Sprites rendering batch
	 * @return
	 */
	SpriteBatch sprites();

	/**
	 * Shapes rendering batch
	 * @return
	 */
	ShapeRenderer shaper();
	
	/**
	 * Decals rendering batch
	 * @return
	 */
	DecalBatch decals();
	
	
	Camera camera();
	
	Environment environment();

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

	Color getAmbientColor();

}
