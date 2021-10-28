package game.world.saves;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

import game.debug.Debug;
import game.systems.EntityPrefab;
import game.systems.IComponentDef;
import game.systems.lifecycle.LifecycleComponent;
import game.world.BlueprintFactory;
import game.world.Level;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * Provides methods to save and load game levels
 * @author fimar
 */
@AllArgsConstructor
public class LevelStore
{
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Settings
	{
		@Builder.Default
		public int maxFractionDigits = 2;
	}
	private BlueprintFactory blueprints;
	private Settings settings;

	// TODO: move saves to USER_HOME or similar folder:
	public static final String SAVE_DIR = "saves";

	public LevelStore(BlueprintFactory factory)
	{
		this(factory, new Settings());
	}


	public void save(Level level, String savename)
	{
		FileHandle savefile = toSaveFile(savename);
		String processId = "Writing save " + savefile;

		Debug.startTiming(processId);
		Json json = new Json(OutputType.minimal);

		SavedLevel savedata = new SavedLevel( level.getDef().getSettings() );

		////////////////////////////////////////////////////////////
		// TODO: save systems
		/*for(EntitySystem system : level.getEngine().getSystems())
		{
			if(system.getClass().isAnnotationPresent(Transient.class))
				continue;

			savedata.systems.add(system);
		}*/

		////////////////////////////////////////////////////////////
		// save entities
		Set <String> nonSavedComponents = new HashSet <> (); // TODO: remove

		for(Entity entity : level.getEngine().getEntities())
		{
			// this props objects will receive values to save from entity components
			EntityProps savedProps = new EntityProps();

			// get object blueprint to analyze components
			LifecycleComponent lifecycle = entity.getComponent(LifecycleComponent.class);

			EntityPrefab prefab = blueprints.getPrefab(lifecycle.type, lifecycle.path, savedProps);
			if( prefab == null)
			{
				Debug.warn("Entity of type " + lifecycle.type + " is missing prefab, not saving");
				continue;
			}

			//List <String> componentTypes = new ArrayList <>();
			// extract saved values from components into savedProps
			for(Component component : entity.getComponents())
			{
				if(!Savable.class.isAssignableFrom(component.getClass()))
				{
					nonSavedComponents.add(component.getClass().getCanonicalName());
					continue;
				}
				//componentTypes.add(component.getClass().getCanonicalName());

				Savable savableComponent = (Savable) component;
				if(savableComponent.getDefClass() == null)
					throw new RuntimeException("Def class is null for component type " + savableComponent.getClass());
				IComponentDef <?> def = (IComponentDef) prefab.getDef(savableComponent.getDefClass());
				savableComponent.save(def, savedProps);
			}

			savedata.addEntity(savedProps);
		}

		////////////////////////////////////////////////////////////
		// write save file
		// internalize loaded strings
		json.setSerializer( Float.class, new Serializer<Float>()
		{
			String FMT_STR = String.format("0.%" + settings.maxFractionDigits + "d", 0);
			DecimalFormat format = new DecimalFormat(FMT_STR);

			@Override public Float read(Json json, JsonValue value, Class type) { return value.asFloat(); }

			@Override public void write(Json json, Float value, Class type) {
				json.writeValue( format.format(value));
			}
		});
		String jsonStr = json.prettyPrint(savedata);

		try (Writer writer = savefile.writer(false, "UTF-8"))
		{
			writer.write(jsonStr);
		}
		catch (IOException e) { e.printStackTrace(); }

		//json.toJson(savedata, savefile);
		Debug.stopTiming(processId);
	}

	public SavedLevel load(String savename)
	{
		FileHandle savefile =  toSaveFile(savename);
		String processId = "Reading save " + savefile;
		Debug.startTiming(processId);
		Json json = new Json(OutputType.minimal);

		// internalize loaded strings
		json.setSerializer( String.class, new Serializer<String>()
		{
			@Override
			public String read(Json json, JsonValue value, Class type)
			{
				return value.asString().intern();
			}

			@Override public void write(Json json, String value, Class type) { json.writeValue(value); }
		});


		// load
		SavedLevel savedLevel = json.fromJson(SavedLevel.class, savefile);



		Debug.stopTiming(processId);

		return savedLevel;
	}


	private FileHandle toSaveFile(String savename)
	{
		FileHandle savefile = Gdx.files.local(SAVE_DIR + "/" + savename);
		return savefile;
	}

	public static String encodeGrid(float [][] grid)
	{
		ByteBuffer buff = ByteBuffer.allocate(4*grid[0].length*grid.length);
		for (int i = 0; i < grid[0].length; i++)
			for (int j = 0; j < grid.length; j++){
				float value = grid[i][j];
				buff.putFloat(value);
			}
		String data = Base64.getEncoder().encodeToString(buff.array());
		return data;
	}

	public static float [][] decodeGrid(String base64, int w, int h)
	{
		float [][] grid = new float [w][h];
		byte [] bytes = Base64.getDecoder().decode(base64);
		ByteBuffer bb = ByteBuffer.wrap(bytes);

		for (int i = 0; i < w; i++)
			for (int j = 0; j < h; j++){
				float value = bb.getFloat();
				grid[i][j] = value;
			}

		return grid;
	}
}
