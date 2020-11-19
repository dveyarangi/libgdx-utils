package game.world.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Camera provider that tries to fit the best into viewport, without stretching
 *
 * @author Fima
 */
public class OrthoCameraProvider implements ICameraProvider
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
	private float worldWidth;

	/**
	 * viewport aspect ratio
	 */
	protected float aspectRatio = 1;

	public OrthoCameraProvider( float worldWidth, float worldHeight, float startX, float startY, float startZoom )
	{
		this.worldWidth = worldWidth;
		// these values will be overridden at resize
		this.camera = new OrthographicCamera(worldWidth, worldHeight);

		camera.position.x = startX;
		camera.position.y = startY;
		camera.near = 100f;
		camera.far = -100f;
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

	@Override
	public float zoom()
	{
		return camera.zoom;
	}

	@Override
	public void zoom(float zoom)
	{
		camera.zoom = zoom;
	}

	Vector3 tmp = new Vector3();
	
	@Override
	public void unproject(float screenX, float screenY, Vector2 out)
	{
		tmp.x = screenX;
		tmp.y = screenY;
		tmp.z = 0;
		camera.unproject(tmp);
		out.x = tmp.x;
		out.y = tmp.y;
	}

}
