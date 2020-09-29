package game.systems.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import game.world.camera.FittingCameraProvider;

/**
 * Rendering helper
 *
 */
public class OverlayRenderer implements IRenderer
{

	private FittingCameraProvider cameraProvider;
	
	public final SpriteBatch batch;

	public final ShapeRenderer shaper;

	public OverlayRenderer()
	{
		cameraProvider = new FittingCameraProvider(100, 100);

		batch = new SpriteBatch();

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

		batch.setProjectionMatrix(cameraProvider.getCamera().combined);
	}

	@Override
	public SpriteBatch batch()
	{
		return batch;
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
		batch.dispose();
	}

}
