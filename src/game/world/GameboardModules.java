package game.world;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import game.resources.ResourceFactory;
import game.systems.control.GameInputProcessor;
import game.world.camera.ICameraProvider;
import lombok.Getter;

/**
 * Defines set of resources, controllers and ui that are used in a game level.
 *
 * @author Fima
 */
public class GameboardModules
{
	/**
	 * Resource factory, that loads and caches resources textures, animations,
	 * models, fonts etc) used in this level.
	 */
	@Getter protected ResourceFactory resourceFactory;

	/**
	 * Level definitions, such as initial unit layout, camera setup, background
	 */
	@Getter protected LevelDef levelDef;

	@Getter private GameInputProcessor input;

	@Getter private IFabric environment;
	
	@Getter private Chronometer chronometer;

	@Getter private ICameraProvider cameraProvider;

	private ObjectMap <Class<? extends GameModule>, GameModule> customModules = new ObjectMap <> ();


	public GameboardModules( ResourceFactory resourceFactory, 
			LevelDef def, 
			GameInputProcessor input,
			IFabric environment, 
			Chronometer chronometer,
			ICameraProvider cameraProvider)
	{
		// game resources registry and loader
		this.resourceFactory = resourceFactory;

		this.levelDef = def;
		
		this.input = input;
		
		this.chronometer = chronometer;

		this.environment = environment;

		this.cameraProvider = cameraProvider;

	}

	public void addModule(GameModule object)
	{
		customModules.put(object.getClass(), object);
	}


	public <M extends GameModule> M getModule(Class <M> clazz)
	{
		return (M) customModules.get(clazz);
	}

	public Array <GameModule> getCustomModules()
	{
		return customModules.values().toArray();
	}



}
