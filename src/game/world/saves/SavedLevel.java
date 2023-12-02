package game.world.saves;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import game.world.IGameSettings;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SavedLevel
{
	int width;
	int height;

	@Getter IGameSettings settings;


	@Getter ObjectMap<String, SavedSystem> systems = new ObjectMap <> ();
	@Getter Array <Props> entities = new Array <> ();


	public SavedLevel(IGameSettings settings)
	{
		this.settings = settings;
	}
	public void addSystem(SavedSystem system)
	{
		systems.put(system.getName(), system);
	}
	public void addEntity(Props properties)
	{
		entities.add(properties);
	}

}
