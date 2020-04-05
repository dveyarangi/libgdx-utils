package game.world.camera;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Camera provider that tries to fit the best into viewport, without stretching
 *
 * @author Fima
 */
public class PerspectiveCameraProvider implements ICameraProvider
{
	/**
	 * Managed camera
	 */
	private PerspectiveCamera camera;

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
	
	protected float zoom;

	public PerspectiveCameraProvider( float worldWidth, float worldHeight, float startX, float startY, float startZoom )
	{
		this.worldHeight = worldHeight;
		this.worldWidth = worldWidth;
		// these values will be overridden at resize
		this.camera = new PerspectiveCamera( 45, worldWidth, worldHeight);
		this.zoom = startZoom;
		this.camera.near = 1;
		this.camera.far = 1000;

		//camera.zoom = startZoom;
		camera.position.x = startX;
		camera.position.y = startY;
		camera.position.z = 0;
		//camera.lookAt(0,0,1);
		//camera.near = 0.0001f;
		//camera.far = 300f;
		camera.update();
	}

	@Override public Camera getCamera() { return camera; }

	@Override public Viewport getViewport() { return viewport; }

	@Override
	public void resize( int screenWidth, int screenHeight )
	{
		aspectRatio = (float) screenWidth / (float) screenHeight;
		
		camera.viewportWidth = screenWidth;//worldWidth * aspectRatio* zoom;
		camera.viewportHeight = screenHeight;//worldWidth* zoom;
		zoom(zoom);
		camera.update();
	}

	@Override
	public float zoom()
	{
		return zoom;
	}

	@Override
	public void zoom(float zoom)
	{
		this.zoom = zoom;
		camera.up.set(0,1,0);
		camera.update();
		//camera.lookAt(0,1,0);
		camera.position.z = 100*zoom;

		//System.out.println(camera.position.z);
		camera.update();
	}
	private static float PICTURE_PLANE = 0;
	public static Plane plane = new Plane(new Vector3(0,0, PICTURE_PLANE), new Vector3(10,10, PICTURE_PLANE), new Vector3(10,0, PICTURE_PLANE));
	Vector3 temp = new Vector3();
	@Override
	public void unproject(float screenX, float screenY, Vector2 out)
	{
		Ray ray = camera.getPickRay(screenX, screenY);
		//ray.getEndPoint(temp, 1);
		Intersector.intersectRayPlane(ray, plane, temp);
		out.x = temp.x;
		out.y = temp.y;
	}

}
