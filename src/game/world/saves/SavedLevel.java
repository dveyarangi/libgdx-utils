package game.world.saves;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SavedLevel {
	//List <EntitySystem> systems = new ArrayList <> ();
	List <SavedEntity> entities = new ArrayList <> ();

	public void addEntity(String id, HashMap<String, String> properties)
	{
		entities.add(new SavedEntity(id, properties));
	}

}
