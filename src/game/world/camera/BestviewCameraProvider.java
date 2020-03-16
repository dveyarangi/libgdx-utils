package game.world.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Camera provider that tries to fit the best into viewport, without stretching
 *
 * @author Fima
 */
public class BestviewCameraProvider implements ICameraProvider
{
	/**
	 * Managed camera
	 */
	private OrthographicCamera camera;

	/**
	 * Current viewport, defined by application window size
	 */
	private Viewport viewport;

	/**
	 * dimensions of the displayed world;
	 * only part may be visible at specified time
	 */
	private float worldWidth, worldHeight;

	/**
	 * viewport aspect ratio
	 */
	protected float aspectRatio = 1;

	public BestviewCameraProvider( float worldWidth, float worldHeight, float startX, float startY, float startZoom )
	{
		this.worldHeight = worldHeight;
		this.worldWidth = worldWidth;
		// these values will be overridden at resize
		this.camera = new OrthographicCamera(worldWidth, worldHeight);

		camera.position.x = startX;
		camera.position.y = startY;
		camera.zoom = startZoom;
	}

	@Override public OrthographicCamera getCamera() { return camera; }

	@Override public Viewport getViewport() { return viewport; }

	@Override
	public void resize( int screenWidth, int screenHeight )
	{
		aspectRatio = (float) screenWidth / (float) screenHeight;

		camera.viewportWidth = worldWidth * aspectRatio;// * camera.zoom;
		camera.viewportHeight = worldWidth;// * camera.zoom;

		camera.update();
	}

}
