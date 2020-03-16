package game.world.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Controls viewport TODO: maybe just using the viewport class is sufficient
 * 
 * @author Fima
 *
 */
public interface ICameraProvider
{
	public OrthographicCamera getCamera();

	public Viewport getViewport();

	public void resize( int screenWidth, int screenHeight );

	// public float getMinZoom();
	// public float getMaxZoom();

}
