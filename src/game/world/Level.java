package game.world;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectSet;

import game.config.GraphicOptions;
import game.debug.Debug;
import game.systems.EntityFactory;
import game.systems.PooledEngine;
import game.systems.SystemDef;
import game.systems.WarmedUpSystem;
import game.systems.control.GameInputProcessor;
import game.systems.lifecycle.LifecycleSystem;
import game.systems.rendering.EntityRenderingSystem;
import game.systems.rendering.IRenderingContext;
import game.systems.rendering.Renderer;
import lombok.Getter;

/**
 * Binds together game modules and initiates them.
 *
 * @author Fima
 */
@Transient
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


	@Getter private GraphicOptions graphicOptions;


	private ObjectSet<Entity> modifiedEntities = new ObjectSet<>();
	
	
	private List <WarmedUpSystem> warmedUpSystems = new ArrayList <> ();


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

		////////////////////////////////////////////////////
		// lifesystem works closely with units factory
		// to give birth and death to entities
		engine.addSystem( new LifecycleSystem( unitsFactory ) );

		////////////////////////////////////////////////////
		// this system controls the physical environment, manages cursor picking and collisions
		engine.addSystem( (EntitySystem) modules.getEnvironment() );


		List <IRenderingContext> systemRenderers = new ArrayList <> ();
		////////////////////////////////////////////////////
		// initialize and add EntitySystems from definitions:
		for(SystemDef systemDef : def.getSystemDefs())
		{
			EntitySystem system = systemDef.createSystem();
			systemDef.initSystem( this, system );
			engine.addSystem( system );
			
			if( system instanceof WarmedUpSystem )
				warmedUpSystems.add((WarmedUpSystem)system);

			IRenderingContext systemRenderer = systemDef.createRenderer();
			if( systemRenderer != null )
				systemRenderers.add(systemRenderer);
		}

		// ////////////////////////////////////////////////////
		Renderer rend = new Renderer( modules.getCameraProvider(), graphicOptions );
		// creating entity rendering system:
		this.renderer = new EntityRenderingSystem( rend, modules.getResourceFactory(), systemRenderers );

		engine.addSystem( this.renderer );

		// ////////////////////////////////////////////////////
		for(GameModule module : getModules().getCustomModules())
			module.init(this);

		// ////////////////////////////////////////////////////
		unitsFactory.createUnits( def );



		// ////////////////////////////////////////////////////
		//
		Runtime.getRuntime().gc();


		Debug.stopTiming("level initialization");


	}

	/**
	 * @param delta
	 */
	public void engineUpdate( final float delta )
	{
		// //////////////////////////////////////////////////////
		// some profiling
		profile( delta );

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

		Debug.debug.draw();

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

	public boolean debug( final String message )
	{
		Gdx.app.debug(TAG, message);
		return true;
	}

	public boolean log( final String message )
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

	public void entityModified(Entity entity)
	{
		modifiedEntities.add(entity);
	}

	public void warmUp() 
	{
		for(WarmedUpSystem system : warmedUpSystems)
			system.initWarmup();
		
		for(int tick = 0; tick < def.getSimulationTicks(); tick ++)
			for(WarmedUpSystem system : warmedUpSystems)
				system.warmup(100);

		for(WarmedUpSystem system : warmedUpSystems)
			system.finishWarmup();
		
		this.update(0);
	}
}
