package game.world;

import game.systems.EntityDef;
import game.systems.EntityPrefab;
import game.world.saves.Props;

public interface BlueprintFactory
{
	EntityDef getBlueprint(String type, String path);

	/**
	 * Add entity to level that is being loaded
	 * @param level
	 * @param type
	 * @param path
	 * @param props
	 */
	//void prepareEntity(LevelDef level, String type, String path, Props props);

	/**
	 * Add entity to level that is already running
	 * @param level
	 * @param def
	 * @param props
	 */
	void insertEntity(LevelDef level, EntityDef def, Props props);


	EntityPrefab getPrefab(String type, String path, Props props, boolean instantiate);

}
