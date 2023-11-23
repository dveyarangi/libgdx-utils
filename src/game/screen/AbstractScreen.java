package game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;

import game.config.GraphicOptions;
import game.systems.rendering.Renderer;
import game.util.LoadableModule;
import game.world.camera.FittingCameraProvider;
import game.world.camera.ICameraProvider;
import lombok.Getter;

/**
 * The base class for all game screens.
 */
public abstract class AbstractScreen<G extends AbstractGame> implements Screen
{
	private static final String TAG = "screen";
	// the fixed viewport dimensions (ratio: 1.6)
	// protected static final int GAME_VIEWPORT_WIDTH = 512,
	// GAME_VIEWPORT_HEIGHT = 1024;
	// protected static final int MENU_VIEWPORT_WIDTH = 1024,
	// MENU_VIEWPORT_HEIGHT = 512;

	@Getter protected final G game;
	protected final Stage stage;

	protected Renderer renderer;


	protected AbstractScreen<G> targetScreen;

	private ICameraProvider cameraProvider;


	protected int worldWidth, worldHeight;

	public AbstractScreen( final G game )
	{
		this(game, 500,500 );
	}

	public AbstractScreen( final G game, int worldWidth, int worldHeight )
	{
		this.game = game;
		this.stage = new Stage();

		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;

		this.cameraProvider = new FittingCameraProvider(worldWidth, worldHeight);

	}

	protected String getName()
	{
		return this.getClass().getSimpleName();
	}
	// Screen implementation

	@Override
	public void show()
	{
		Gdx.app.debug(TAG, "Showing screen " + this);

		this.renderer = new Renderer(cameraProvider, new GraphicOptions());
		renderer.init();

		// set the stage as the input processor
		Gdx.input.setInputProcessor(stage);

		IGameOverlay overlay = this.getGame().getTransitionOverlay();
		if( overlay != null)
			overlay.setFadeIn(this);
	}

	@Override
	public void resize( final int screenWidth, final int screenHeight )
	{
		Gdx.app.debug(TAG, "Resizing screen to: " + screenWidth + " x " + screenHeight);

		cameraProvider.resize(screenWidth, screenHeight);

		renderer.resize(screenWidth, screenHeight);

		stage.setViewport(cameraProvider.getViewport());
	}

	@Override
	public void render( final float delta )
	{

		renderer.update(delta);

		// update the actors
		stage.act(delta);

		// draw the actors
		stage.draw();

		if( targetScreen != null )
			this.getGame().setScreen(targetScreen);

		/*
		 * IGameOverlay overlay = getGame().getOverlay();
		 *
		 *
		 * overlay.updateOverlay( delta );
		 *
		 * if(overlay.isTotallyFaded()) { getGame().setScreen( targetScreen ); }
		 * else { overlay.drawOverlay( renderer ); }
		 */
	}

	public void setScreen( AbstractScreen <G>screen )
	{
		this.targetScreen = screen;
	}

	@Override
	public void hide()
	{
		Gdx.app.debug(TAG, "Hiding screen");
		// dispose the screen when leaving the screen;
		// note that the dipose() method is not called automatically by the
		// framework, so we must figure out when it's appropriate to call it
		this.dispose();
	}

	@Override
	public void pause()
	{
		Gdx.app.debug(TAG, "Pausing screen");
	}

	@Override
	public void resume()
	{
		Gdx.app.debug(TAG, "Resuming screen");
	}

	@Override
	public void dispose()
	{
		Gdx.app.debug(TAG, "Disposing screen");

		renderer.dispose();

		stage.clear();

	}

	/**
	 * Allows specifying pre-loadable module
	 * @return
	 */
	protected LoadableModule getLoadable() { return null; };


	// /////////////////////////////////////////////////////////////////////////

}