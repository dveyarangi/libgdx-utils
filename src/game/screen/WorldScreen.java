package game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import game.config.GraphicOptions;
import game.debug.Debug;
import game.resources.ResourceFactory;
import game.systems.control.GameInputProcessor;
import game.systems.rendering.AnimationRenderingComponent;
import game.systems.rendering.EntityRenderingSystem;
import game.systems.rendering.MeshRenderingComponent;
import game.systems.rendering.ShapeRenderingComponent;
import game.systems.rendering.SpriteComponent;
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

public abstract class WorldScreen<G extends AbstractGame> extends AbstractScreen<G >
{
	GameboardModules gameSetup;

	private Level level;

	private GraphicOptions options;

	public WorldScreen( G game )
	{
		super(game);
	}
	
	public LoadableModule getLoadable()
	{
		return new LoadableThread() {
			@Override
			public void load(LoadingProgress loadingProgress) {
				WorldScreen.this.load(loadingProgress);
			}
		};
	}
	
	private void load(LoadingProgress progress)
	{
		progress.update(0, "Loading level...");
		
		this.options = getOptions();
		if( options == null)
			throw new IllegalArgumentException("Options cannot be null");


		// /////////////////////////////////////////////////////////////////////////
		// LOADING LEVEL RESOURCES
		//
		// this should be done before we get here, in a loading screen:
		//
		ResourceFactory factory = super.game.getResourceFactory();
		//		factory.finishLoading();


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
		LevelDef def = createLevel(progress);
		Debug.stopTiming("level creation");
		


		// /////////////////////////////////////////////////////////////////////////
		// CREATING USER INTERFACE OVERLAY
		//
		// HUD display and interface manager
		//
		//HUD ui = new BattleHUD();

		// /////////////////////////////////////////////////////////////////////////
		// GAME WORLD RENDERER
		//
		// initializing level rendering system
		//

		LevelInitialSettings settings = def.getInitialSettings();
		if( settings == null )
			throw new IllegalArgumentException("Missing level initial settings");

		ICameraProvider worldCameraProvider = null;
		
		switch(settings.getCameraMode())
		{
		default:
		case ORTHOGONAL:
			worldCameraProvider= new OrthoCameraProvider(def.getWidth(), def.getHeight(),
					settings.getCameraPosition().x, settings.getCameraPosition().y, settings.getInitZoom());
			break;
		case PERSPECTIVE:
			worldCameraProvider = new PerspectiveCameraProvider(def.getWidth(), def.getHeight(),
					settings.getCameraPosition().x, settings.getCameraPosition().y, settings.getInitZoom());
			break;
		
		}

		gameSetup = new GameboardModules(factory, def, environment, worldCameraProvider);
		extendModules(gameSetup);
		
		progress.update(1, "Populating world...");
		progress.setFinished(true);

	}
	

	@Override
	public void show()
	{
		super.show();


		level = new Level( gameSetup, options );

		// TODO: remove
		Debug.init(level);


		// TODO: this is not the place
		Gdx.gl.glEnable(GL20.GL_BLEND);

	}
	
	protected abstract LevelDef createLevel(LoadingProgress progress);
	
	protected abstract IFabric createFabric(LoadingProgress progress);
	
	protected void extendModules(GameboardModules modules)
	{
		modules.addRendererType(AnimationRenderingComponent.class);
		modules.addRendererType(ShapeRenderingComponent.class);
		modules.addRendererType(MeshRenderingComponent.class);
		modules.addRendererType(SpriteComponent.class);
	}

	private void update( float delta )
	{

		level.engineUpdate(delta);

		assert Debug.debug.update(delta);
	}

	protected void draw( float delta )
	{


		level.draw(delta);
		Debug.debug.draw();
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

	public void dispose()
	{
		GameInputProcessor processor = level.getEngine().getSystem(GameInputProcessor.class);
		processor.dispose();
	}
}
