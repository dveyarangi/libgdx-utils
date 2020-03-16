package game.systems.control;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import game.world.camera.ICameraProvider;

/**
 * controls camera
 *
 * @author Ni
 *
 */
public class FreeCameraController extends ICameraController
{
	/**
	 * Camera we control
	 */
	private final ICameraProvider cameraProvider;

	/**
	 * Target state, with (x,y) are world coordinates and z scale coef
	 */
	private Vector3 target = new Vector3(0, 0, 1);

	/**
	 * TODO: from world dims
	 */
	private float maxZoom = 2;
	private float minZoom = 0.1f;

	private static float CAMERA_SPEEED = 0.3f;

	public FreeCameraController( ICameraProvider cameraProvider, float initZoom, float minZoom, float maxZoom )
	{

		this.cameraProvider = cameraProvider;

		target.x = this.camera().position.x;
		target.y = this.camera().position.y;
		target.z = initZoom;
		
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
		
	}

	@Override
	public OrthographicCamera camera()
	{
		return cameraProvider.getCamera();
	}

	/**
	 * will step the camera +call camera.update()
	 *
	 * @param delta
	 */
	@Override
	public void update( final float delta )
	{

		// moving camera toward target state:
		float dx = this.camera().position.x - this.target.x;
		float dy = this.camera().position.y - this.target.y;
		float dz = this.camera().zoom - this.target.z;
		this.camera().position.x = this.camera().position.x - CAMERA_SPEEED * dx;
		this.camera().position.y = this.camera().position.y - CAMERA_SPEEED * dy;
		this.camera().zoom = this.camera().zoom - CAMERA_SPEEED * dz;

		// recalculating matrices:
		this.camera().update();

	}

	/**
	 * @param width
	 * @param height
	 */
	@Override
	public void resize( final int screenWidth, final int screenHeight )
	{
	}

	@Override
	public void moveBy( final float dx, final float dy )
	{
		this.target.x += dx;
		this.target.y += dy;
	}

	@Override
	public void zoomTo( final float x, final float y, final float zoom )
	{
		this.zomove(x, y, zoom, this.camera().zoom);

	}

	/**
	 * Adjust camera target, while trying to retain world coordinates at cursor.
	 *
	 * @param x
	 * @param y
	 * @param zoom
	 * @param prevZoom
	 */
	private void zomove( final float x, final float y, final float zoom, final float prevZoom )
	{
		// adjust zoom:
		this.target.z += zoom;
		if( target.z > maxZoom )
			target.z = maxZoom;
		if( target.z < minZoom )
			target.z = minZoom;

		// calculating camera center displacement after zoom:
		float pOffsetX = ( x - this.camera().position.x ) / ( prevZoom / target.z );
		float pOffsetY = ( y - this.camera().position.y ) / ( prevZoom / target.z );

		this.target.x = x - pOffsetX / 1.0f;
		this.target.y = y - pOffsetY / 1.0f;

	}
}
