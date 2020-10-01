package game.systems.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import game.config.GraphicOptions;
import game.world.camera.ICameraProvider;

/**
 * Rendering helper
 *
 */
public class Renderer implements IRenderer
{

	public final SpriteBatch sprites;

	public final ShapeRenderer shapes;
	
	public final DecalBatch decals;

	private ICameraProvider cameraProvider;

	public Renderer( ICameraProvider cameraProvider, GraphicOptions options )
	{

		this.cameraProvider = cameraProvider;

		sprites = new SpriteBatch(options.spriteBatchSize);

		shapes = new ShapeRenderer();
		shapes.setAutoShapeType(true);
		
		decals = new DecalBatch(new CameraGroupStrategy(cameraProvider.getCamera()));
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
		shapes.setProjectionMatrix(cameraProvider.getCamera().combined);

		sprites.setProjectionMatrix(cameraProvider.getCamera().combined);
	}

	@Override
	public SpriteBatch sprites()
	{
		return sprites;
	}
	
	@Override
	public DecalBatch decals()
	{
		return decals;
	}
	@Override
	public ShapeRenderer shaper()
	{
		return shapes;
	}

	@Override
	public void resize( int screenWidth, int screenHeight )
	{
		cameraProvider.resize(screenWidth, screenHeight);
	}

	@Override
	public void dispose()
	{
		shapes.dispose();
		sprites.dispose();
		decals.dispose();
	}

}
