package game.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import game.debug.Debug;
import game.resources.ResourceFactory;
import game.screen.AbstractGame;
import game.screen.AbstractScreen;
import game.systems.control.GameInputProcessor;
import game.systems.rendering.AnimationRenderingComponent;
import game.systems.rendering.EntityRenderingSystem;
import game.systems.rendering.MeshRenderingComponent;
import game.systems.rendering.ShapeRenderingComponent;
import game.systems.rendering.SpriteRenderingComponent;
import game.world.camera.BestviewCameraProvider;
import game.world.camera.ICameraProvider;

public abstract class WorldScreen<G extends AbstractGame> extends AbstractScreen<G >
{
	GameboardModules gameSetup;

	private Level level;

	public WorldScreen( G game )
	{
		super(game);
	}

	@Override
	public void show()
	{
		super.show();

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
		IFabric environment = createFabric();



		// /////////////////////////////////////////////////////////////////////////
		// GENERATING/LOADING LEVEL DEFINITIONS:
		//
		// this provides entity-entity and UI-entity interaction methods
		//
		Debug.startTiming("level creation");
		
		LevelDef def = createLevel();
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

		ICameraProvider worldCameraProvider = new BestviewCameraProvider(def.getWidth(), def.getHeight(),
				settings.getCameraPosition().x, settings.getCameraPosition().y, settings.getInitZoom());

		gameSetup = new GameboardModules(factory, def, environment, worldCameraProvider);
		extendModules(gameSetup);

		level = new Level( gameSetup );

		// TODO: remove
		Debug.init(level);

		// TODO: maybe this helps:
		Runtime.getRuntime().gc();

		// TODO: this is not the place
		Gdx.gl.glEnable(GL20.GL_BLEND);
	}
	
	protected abstract LevelDef createLevel();
	
	protected abstract IFabric createFabric();
	
	protected void extendModules(GameboardModules modules)
	{
		modules.addRendererType(AnimationRenderingComponent.class);
		modules.addRendererType(ShapeRenderingComponent.class);
		modules.addRendererType(MeshRenderingComponent.class);
		modules.addRendererType(SpriteRenderingComponent.class);
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
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
	}

}
