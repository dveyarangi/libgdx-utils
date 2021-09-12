package game.world.saves;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import game.world.IGameSettings;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SavedLevel
{
	int width;
	int height;

	@Getter IGameSettings settings;


	//List <EntitySystem> systems = new ArrayList <> ();
	List <SavedEntity> entities = new ArrayList <> ();


	public SavedLevel(IGameSettings settings)
	{
		this.settings = settings;
	}

	public void addEntity(String id, HashMap<String, String> properties)
	{
		entities.add(new SavedEntity(id, properties));
	}

}
