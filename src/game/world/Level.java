package game.world;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;

import game.config.GraphicOptions;
import game.debug.Debug;
import game.systems.EntityFactory;
import game.systems.SystemDef;
import game.systems.control.GameInputProcessor;
import game.systems.lifecycle.LifecycleSystem;
import game.systems.rendering.EntityRenderingSystem;
import game.systems.rendering.ISystemRenderer;
import game.systems.rendering.Renderer;
import lombok.Getter;

/**
 * Binds together game modules and initiates them.
 *
 * @author Fima
 */
public class Level extends EntitySystem
{
	// //////////////////////////////////////////////////////////////
	// level definitions:
	@Getter private LevelDef def;


	// //////////////////////////////////////////////////////////////
	// set of pre-loaded gameboard modules
	private GameboardModules modules;

	// //////////////////////////////////////////////////////////////
	// factories

	/**
	 * Units factory; encapsulates means of defining and creating a game unit.
	 * Relies on resource factory to load unit decorations;
	 */
	private EntityFactory unitsFactory;


	/**
	 * Some generic module to attach to level
	 */
	private ILevelListener data;


	// //////////////////////////////////////////////////////////////
	// units

	/**
	 * Set of units in game
	 */
	private PooledEngine engine;


	// //////////////////////////////////////////////////////////////
	// renderer
	private EntityRenderingSystem renderer;

	
	private List <ISystemRenderer> systemRenderers;
	
	
	@Getter private GraphicOptions graphicOptions;
	/**
	 *
	 */
	public Level( GameboardModules modules, GraphicOptions options )
	{
		
		this.modules = modules;

		this.def = modules.getLevelDef();
		
		this.graphicOptions = options;
		
		// game entities manager:
		this.engine = new PooledEngine(INITIAL_UNITS_NUM, Integer.MAX_VALUE, 10 * INITIAL_UNITS_NUM, Integer.MAX_VALUE);
		// flattening game modules:
		this.unitsFactory = new EntityFactory( this, engine );
		
		this.systemRenderers = new ArrayList <> ();

		engine.addSystem( this );
		
		
	}

	/**
	 * Initializes level properties and creates units and environment based on
	 * level definitions
	 *
	 * @param renderer
	 * @param context
	 * @param factory
	 */
	@Override
	public void addedToEngine(Engine engine)
	{
		Debug.startTiming("level initialization");

		// creating entity control systems:

		// ////////////////////////////////////////////////////
		Renderer rend = new Renderer( modules.getCameraProvider(), graphicOptions );
		// creating entity rendering system:
		this.renderer = new EntityRenderingSystem( rend,
				modules.getGameFactory(), modules.getRendererTypes() );

		////////////////////////////////////////////////////
		// lifecyctem works closely with units factory
		engine.addSystem( new LifecycleSystem( unitsFactory ) );


		////////////////////////////////////////////////////
		// initalize and add a generic entity system from definitions:
		for(SystemDef systemDef : def.getSystemDefs())
		{
			EntitySystem system = systemDef.createSystem();
			systemDef.initSystem( this, system );
			engine.addSystem( system );
			ISystemRenderer systemRenderer = systemDef.createRenderer();
			if( systemRenderer != null )
				systemRenderers.add(systemRenderer);
		}

		////////////////////////////////////////////////////
		// this system controls the physical environment and dispatches damage
		// messages:
		engine.addSystem( (EntitySystem) modules.getEnvironment() );

		engine.addSystem( this.renderer );

		unitsFactory.createUnits( def );

		Debug.stopTiming("level initialization");

	}

	/**
	 * @param delta
	 */
	public void engineUpdate( final float delta )
	{
		// //////////////////////////////////////////////////////
		// some profiling
		this.profile( delta );

		// //////////////////////////////////////////////////////
		if( data != null )
			data.beforeUpdate(delta);


		// //////////////////////////////////////////////////////
		// animate units:
		engine.update(delta);

		// //////////////////////////////////////////////////////
		if( data != null )
			data.afterUpdate(delta);

	}


	public void draw( float delta )
	{
		renderer.draw(delta);
		
		for(int idx = 0; idx < systemRenderers.size(); idx ++)
		{
			systemRenderers.get(idx).render(renderer);
		}
		
		GameInputProcessor processor = getEngine().getSystem(GameInputProcessor.class);
		processor.render(renderer);
	}

	public GameboardModules getModules() { return modules; }

	public EntityFactory getEntityFactory() { return unitsFactory; }

	/*
	 * some generic module to append at level creation
	 */
	@SuppressWarnings( "unchecked" ) public <E> E getListener() { return (E) data; }
	public void setListener( ILevelListener data ) { this.data = data; }

	public LevelDef def() { return def; }
	// ////////////////////////////////////////////////////////////////////
	// world dimension utils
	/*
	 * public boolean inWorldBounds(float x, float y) { return x <
	 * def.getHalfWidth() && x > -def.getHalfWidth() && y < def.getHalfHeight()
	 * && y > -def.getHalfHeight(); } public boolean inWorldBounds(Vector2
	 * position) { return inWorldBounds(position.x, position.y); }
	 *
	 * public float getHeight() { return def.getHeight(); } public float
	 * getWidth() { return def.getWidth(); } public float getHalfHeight() {
	 * return def.getHalfHeight(); } public float getHalfWidth() { return
	 * def.getHalfWidth(); }
	 */

	// ////////////////////////////////////////////////////////////////////
	// TODO: debug

	private static final String TAG = "level";

	private boolean debug( final String message )
	{
		Gdx.app.debug(TAG, message);
		return true;
	}

	private boolean log( final String message )
	{
		Gdx.app.log(TAG, message);
		return true;
	}
	// //////////////////////////////////////////////////////////////
	// TODO: profiling

	int INITIAL_UNITS_NUM = 100;
	private int PROFILE_INTERVAL = 2;
	private float timeSinceProfiling = 0;

	private void profile( float delta )
	{
		if( timeSinceProfiling > PROFILE_INTERVAL )
		{
			//assert this.log("Level update: active units: " + engine.getEntities().size());

			timeSinceProfiling = 0;
		}

		timeSinceProfiling += delta;
	}

	@Override
	public PooledEngine getEngine() { return engine; }


}
