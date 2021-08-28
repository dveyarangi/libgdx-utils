package game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import game.config.GraphicOptions;
import game.debug.Debug;
import game.resources.ResourceFactory;
import game.systems.control.GameInputProcessor;
import game.systems.rendering.EntityRenderingSystem;
import game.util.LoadableModule;
import game.util.LoadableThread;
import game.util.LoadingProgress;
import game.world.GameboardModules;
import game.world.IFabric;
import game.world.Level;
import game.world.LevelDef;
import game.world.LevelInitialSettings;
import game.world.camera.ICameraProvider;
import game.world.camera.OrthoCameraProvider;
import game.world.camera.PerspectiveCameraProvider;
import lombok.Getter;

public abstract class WorldScreen<G extends AbstractGame> extends AbstractScreen<G >
{
	GameboardModules gameSetup;

	protected Level level;

	@Getter protected GraphicOptions options;

	public WorldScreen( G game, GraphicOptions options )
	{
		super(game);

		this.options = options;
		if( options == null)
			throw new IllegalArgumentException("Options cannot be null");

	}

	@Override
	public LoadableModule getLoadable()
	{
		return new LoadableThread() {
			@Override
			public void load(LoadingProgress loadingProgress) {
				try {
					WorldScreen.this.load(loadingProgress);
				}
				catch(Exception e) {
					loadingProgress.setFailed(e);
				}

			}
		};
	}

	private void load(LoadingProgress progress)
	{
		progress.update(0, "Loading level...");

		// /////////////////////////////////////////////////////////////////////////
		// CREATING INTERACTIVE ENVIRONMENT
		//
		// this provides entity-entity and UI-entity interaction methods
		//
		IFabric environment = createFabric(progress);



		// /////////////////////////////////////////////////////////////////////////
		// GENERATING/LOADING LEVEL DEFINITIONS:
		//
		// this provides entity-entity and UI-entity interaction methods
		//
		Debug.startTiming("level creation");
		LevelDef def = createLevel(progress.subprogress(0.8f));
		Debug.stopTiming("level creation");


		LevelInitialSettings settings = def.getInitialSettings();
		if( settings == null )
			throw new IllegalArgumentException("Missing level initial settings");

		ICameraProvider worldCameraProvider = null;

		switch(settings.getCameraMode())
		{
		default:
		case ORTHOGONAL:
			worldCameraProvider = new OrthoCameraProvider(def.getWidth(), def.getHeight(),
					settings.getCameraPosition().x, settings.getCameraPosition().y, settings.getInitZoom());
			break;
		case PERSPECTIVE:
			worldCameraProvider = new PerspectiveCameraProvider(def.getWidth(), def.getHeight(),
					settings.getCameraPosition().x, settings.getCameraPosition().y, settings.getInitZoom());
			break;

		}

		ResourceFactory factory = super.game.getResourceFactory();

		gameSetup = new GameboardModules(factory, def, environment, worldCameraProvider);
		extendModules(gameSetup);


		progress.update(1, "Populating world...");
		progress.setFinished(true);


	}


	@Override
	public void show()
	{
		super.show();



		//progress.update(0.8f, "Populating world...");
		level = new Level( gameSetup, options );

		// TODO: remove
		Debug.init(level);

	}

	protected abstract LevelDef createLevel(LoadingProgress progress);

	protected abstract IFabric createFabric(LoadingProgress progress);

	protected void extendModules(GameboardModules modules)
	{
	}

	private void update( float delta )
	{

		level.engineUpdate(delta);

		assert Debug.debug.update(delta);
	}

	protected void draw( float delta )
	{


		level.draw(delta);
	}

	@Override
	public void render( float delta )
	{
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );

		super.render(delta);

		this.update(delta);

		this.draw(delta);
	}

	@Override
	public void resize( final int screenWidth, final int screenHeight )
	{
		// super.resize(screenWidth, screenHeight);
		EntityRenderingSystem renderer = level.getEngine().getSystem(EntityRenderingSystem.class);
		renderer.resize(screenWidth, screenHeight);

		GameInputProcessor processor = level.getEngine().getSystem(GameInputProcessor.class);
		processor.resize(screenWidth, screenHeight);

		Debug.debug.resize(screenWidth, screenHeight);
	}

	@Override
	public void dispose()
	{
		GameInputProcessor processor = level.getEngine().getSystem(GameInputProcessor.class);
		processor.dispose();
	}
}
