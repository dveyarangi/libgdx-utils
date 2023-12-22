package game.systems.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
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

	private Environment environment;
	
	public Renderer( ICameraProvider cameraProvider, GraphicOptions options )
	{

		this.cameraProvider = cameraProvider;

		sprites = new SpriteBatch(options.spriteBatchSize);

		shapes = new ShapeRenderer();
		shapes.setAutoShapeType(true);
		
		DecalGroupStrategy strategy = new DecalGroupStrategy(cameraProvider.getCamera(), this);
		decals = new DecalBatch(strategy);
		
		this.environment = new Environment();
		
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
		shapes.setProjectionMatrix(camera().projection);
		shapes.setTransformMatrix(camera().view);

		sprites.setProjectionMatrix(camera().combined);
		sprites.setTransformMatrix(camera().view);
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
	public Camera camera() { return cameraProvider.getCamera(); }
	
	@Override
	public Environment environment() { return environment; }

	@Override
	public void resize( int screenWidth, int screenHeight )
	{
		cameraProvider.resize(screenWidth, screenHeight);
	}
	
	Color ambientColor = new Color();
	@Override
	public Color getAmbientColor()
	{
		//var ambientAttr = (ColorAttribute)environment.get(ColorAttribute.AmbientLight);
		
		//ambientColor.set(ambientAttr.color);
		ambientColor.set(1,1,1,1);
		var lights = (DirectionalLightsAttribute) environment.get(DirectionalLightsAttribute.Type);
		
		for(DirectionalLight light : lights.lights)
			ambientColor.mul(light.color);
		return ambientColor;
		
	}
	

	@Override
	public void dispose()
	{
		shapes.dispose();
		sprites.dispose();
		decals.dispose();
	}

}
