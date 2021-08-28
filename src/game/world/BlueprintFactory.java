package game.world;

import game.systems.EntityDef;
import game.systems.EntityPrefab;
import game.world.saves.EntityProps;

public interface BlueprintFactory
{
	EntityPrefab getBlueprint(String type, String path, EntityProps props);

	void insertEntity(LevelDef level, EntityDef def, EntityProps props);

}
