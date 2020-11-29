package game.world;

import java.util.HashMap;
import java.util.Map;

import game.resources.ResourceFactory;
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

	@Getter private IFabric environment;

	@Getter private ICameraProvider cameraProvider; 

	private Map <Class<?>, Object> customModules = new HashMap <> ();
	

	public GameboardModules( ResourceFactory resourceFactory, LevelDef def, IFabric environment, ICameraProvider cameraProvider)
	{
		// game resources registry and loader
		this.resourceFactory = resourceFactory;

		this.levelDef = def;

		this.environment = environment;

		this.cameraProvider = cameraProvider;

	}

	public void addModule(Object object)
	{
		customModules.put(object.getClass(), object);
	}

	@SuppressWarnings("unchecked")
	public <M> M getModule(Class <M> clazz)
	{
		return (M) customModules.get(clazz);
	}



}
