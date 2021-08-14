package game.world.saves;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

import game.debug.Debug;
import game.world.Level;

public class LevelStore
{
	// TODO: move saves to USER_HOME or similar folder:

	public static final String SAVE_DIR = "saves";
	public void save(Level level, String savename)
	{
		FileHandle savefile =  toSaveFile(savename);
		String processId = "Writing save " + savefile;
		Debug.startTiming(processId);
		Json json = new Json(OutputType.minimal);

		json.setSerializer(TextureRegion.class, new Serializer<TextureRegion>() {

			@Override
			public void write(Json json, TextureRegion object, Class knownType)
			{
				json.writeObjectStart();

				json.writeValue("atlasName", "TODODODOD");
				json.writeObjectEnd();
			}

			@Override
			public TextureRegion read(Json json, JsonValue jsonData, Class type)
			{
				// TODO Auto-generated method stub
				return null;
			}});
		json.setSerializer(Decal.class, new Serializer<Decal>() {

			@Override
			public void write(Json json, Decal object, Class knownType)
			{
				json.writeObjectStart();

				json.writeValue("decalName", "TODODODOD");
				json.writeObjectEnd();
			}

			@Override
			public Decal read(Json json, JsonValue jsonData, Class type)
			{
				// TODO Auto-generated method stub
				return null;
			}});

		SavedLevel savedata = new SavedLevel();

		/*for(EntitySystem system : level.getEngine().getSystems())
		{
			if(system.getClass().isAnnotationPresent(Transient.class))
				continue;

			savedata.systems.add(system);
		}*/

		Set <String> nonSavedComponents = new HashSet <> (); // TODO: remove
		for(Entity entity : level.getEngine().getEntities())
		{
			EntityProps savedProps = new EntityProps();
			List <String> componentTypes = new ArrayList <>();
			for(Component component : entity.getComponents())
			{
				if(!Savable.class.isAssignableFrom(component.getClass()))
				{
					nonSavedComponents.add(component.getClass().getCanonicalName());
					continue;
				}
				componentTypes.add(component.getClass().getCanonicalName());

				Savable savableComponent = (Savable) component;
				savableComponent.save(savedProps);

			}
			savedata.addEntity("prefab_TODO-name/sfsd", savedProps.getProps());
		}

		String jsonStr = json.prettyPrint(savedata);
		try (Writer writer = savefile.writer(false, "UTF-8")) {
			writer.write(jsonStr);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		//json.toJson(savedata, savefile);
		Debug.stopTiming(processId);
	}

	public void load(String savename)
	{

	}


	private FileHandle toSaveFile(String savename)
	{
		FileHandle savefile = Gdx.files.local(SAVE_DIR + "/" + savename);
		return savefile;
	}
}
