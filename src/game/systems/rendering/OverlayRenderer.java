package game.systems.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import game.world.camera.FittingCameraProvider;

/**
 * Rendering helper
 *
 */
public class OverlayRenderer implements IRenderer
{

	private FittingCameraProvider cameraProvider;
	
	public final SpriteBatch sprites;

	public final ShapeRenderer shaper;

	public OverlayRenderer()
	{
		cameraProvider = new FittingCameraProvider(100, 100);

		sprites = new SpriteBatch();

		shaper = new ShapeRenderer();
		shaper.setAutoShapeType(true);
	}


	@Override
	public void init()
	{
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void update( float delta )
	{

		// debugMatrix.set(cameraProvider.getCamera().combined);
		// TODO: ineffective? matrices are copied every frame
		shaper.setProjectionMatrix(cameraProvider.getCamera().combined);

		sprites.setProjectionMatrix(cameraProvider.getCamera().combined);
	}


	@Override
	public ShapeRenderer shaper()
	{
		return shaper;
	}

	@Override
	public void resize( int screenWidth, int screenHeight )
	{
		cameraProvider.resize(screenWidth, screenHeight);
	}

	@Override
	public void dispose()
	{
		shaper.dispose();
		sprites.dispose();
	}


	@Override
	public SpriteBatch sprites()
	{
		return sprites;
	}


	@Override
	public DecalBatch decals()
	{
		throw new UnsupportedOperationException();
	}


	@Override
	public Camera camera() { return cameraProvider.getCamera(); }
}
