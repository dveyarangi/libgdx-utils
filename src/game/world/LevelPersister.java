package game.world;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import game.debug.Debug;

public class LevelPersister 
{
	// TODO: move saves to USER_HOME or similar folder:
	
	public static final String SAVE_DIR = "saves";
	public void save(Level level, String savename)
	{
		FileHandle savefile =  toSaveFile(savename);
		String processId = "Writing save " + savefile;
		Debug.startTiming(processId);
		Json json = new Json();
		
		PersistedLevel savedata = new PersistedLevel();
		
		/*for(EntitySystem system : level.getEngine().getSystems())
		{
			if(system.getClass().isAnnotationPresent(Transient.class))
				continue;
			
			savedata.systems.add(system);
		}*/

		for(Entity entity : level.getEngine().getEntities())
		{
			List <Component> entityComponents = new ArrayList <>();
			for(Component component : entity.getComponents())
			{
				if(component.getClass().isAnnotationPresent(Transient.class))
					continue;
				entityComponents.add(component);
			}
			savedata.components.add(entityComponents);
		}
		
		
		json.toJson(savedata, savefile);
		Debug.stopTiming(processId);
	}
	
	public void load(String savename)
	{
		
	}

	
	public static final class PersistedLevel
	{
		//List <EntitySystem> systems = new ArrayList <> ();
		List <List <Component>> components = new ArrayList <> ();
	}	

	
	private FileHandle toSaveFile(String savename)
	{
		FileHandle savefile = Gdx.files.local(SAVE_DIR + "/" + savename);
		return savefile;
	}
}
