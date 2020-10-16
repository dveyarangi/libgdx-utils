package game.systems.control;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import game.systems.rendering.IRenderer;
import game.world.Level;

/**
 * Control mode determines input handling some subset of game functionality
 *
 * @author Fima
 *
 */
public interface IControlMode extends EntityListener
{
	/**
	 * Called before the mode is activated.
	 */
	void reset( Level level );

	/**
	 * Inform control mode of touch event
	 * @param worldX
	 * @param worldY
	 * @param scale
	 * @param unit
	 * @param button
	 * @return true, if the event is consumed
	 */
	boolean touchUp(   float worldX, float worldY, float scale, Entity pickedObject, int button );
	boolean touchDown( float worldX, float worldY, float scale, Entity pickedObject, int button );

	boolean drag(      float worldX, float worldY, float scale, Entity pickedObject, int button );
	/**
	 * Game entity mouse hover callback
	 *
	 * @param pickedObjects
	 */
	// ISpatialObject objectPicked( float x, float y, List<ISpatialObject>
	// pickedObjects );
	/**
	 * Game entity mouse unhover callback
	 *
	 * @param pickedObject
	 */
	void objectPicked( Entity pickedObject );


	/**
	 * Renderer for mode-specific UI
	 *
	 * @param renderer
	 */
	void render( IRenderer renderer );

	void keyDown( int keycode );

	void keyUp( int keycode );

	void keyTyped( char keycode );

	void untouch();

	void setWorldPointer( Vector2 worldPos, float scale );

	public void update( float delta );

	IControl control();

	Table createUI();


}
