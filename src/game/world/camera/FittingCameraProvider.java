package game.world.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class FittingCameraProvider implements ICameraProvider
{

	private OrthographicCamera camera;

	private Viewport viewport;

	// int screenWidth, screenHeight;
	public float worldWidth;
	public float worldHeight;

	protected float aspectRatio = 1;

	public FittingCameraProvider( float worldWidth, float worldHeight )
	{

		this.worldHeight = worldHeight;
		this.worldWidth = worldWidth;
		// these values will be overridden at resize
		this.camera = new OrthographicCamera(worldWidth, worldHeight);
		
		this.viewport = new FitViewport(worldWidth, worldHeight);
	}

	@Override
	public OrthographicCamera getCamera()
	{
		return camera;
	}

	@Override
	public Viewport getViewport()
	{
		return viewport;
	}

	@Override
	public void resize( int screenWidth, int screenHeight )
	{
		// this.screenWidth = screenWidth;
		// this.screenHeight = screenHeight;
		// calculating device screen aspect ratio:
		aspectRatio = (float) screenWidth / (float) screenHeight;

		// considering hud height constant and recalculating hud width:
		//worldWidth = worldHeight * aspectRatio;

		// TODO: camera creation is probably redundant:
//		camera = new OrthographicCamera(worldWidth, worldHeight);
		this.camera.setToOrtho(false, worldWidth*aspectRatio, worldHeight);
		camera.position.set(this.camera.viewportWidth/2, this.camera.viewportHeight/2, 0);
		camera.update();
		
		viewport = new FitViewport(this.camera.viewportWidth, this.camera.viewportHeight);
		viewport.setCamera(camera);
		
		//viewport.setWorldWidth(worldWidth);
		//viewport.setWorldHeight(worldHeight);
		// TODO: viewport update calls glViewport which influences the drawing
		// of world renderer
		// viewport.update(screenWidth, screenHeight);
	}

}
