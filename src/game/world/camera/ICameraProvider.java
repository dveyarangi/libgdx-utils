package game.world.camera;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Controls viewport TODO: maybe just using the viewport class is sufficient
 * 
 * @author Fima
 *
 */
public interface ICameraProvider
{
	public Camera getCamera();

	public Viewport getViewport();
	
	public float zoom();
	public void zoom(float zoom);


	public void resize( int screenWidth, int screenHeight );

	public abstract void unproject(float screenX, float screenY, Vector2 out);

	// public float getMinZoom();
	// public float getMaxZoom();

}
