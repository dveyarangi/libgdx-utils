package game.world.saves;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
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
import game.systems.EntityPrefab;
import game.systems.IComponentDef;
import game.systems.lifecycle.LifecycleComponent;
import game.systems.lifecycle.LifecycleDef;
import game.systems.spatial.ISpatialComponent;
import game.world.BlueprintFactory;
import game.world.Level;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LevelStore
{

	BlueprintFactory blueprints;

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


		SavedLevel savedata = new SavedLevel( level.getDef().getSettings() );

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
			LifecycleComponent lifecycle = entity.getComponent(LifecycleComponent.class);

			ISpatialComponent spatial = entity.getComponent(ISpatialComponent.class);
			EntityPrefab prefab = blueprints.getPrefab(lifecycle.type, lifecycle.path, savedProps);
			if( prefab == null)
			{
				Debug.warn("Entity of type " + lifecycle.type + " is missing prefab, not saving");
				continue;
			}

			for(Component component : entity.getComponents())
			{
				if(!Savable.class.isAssignableFrom(component.getClass()))
				{
					nonSavedComponents.add(component.getClass().getCanonicalName());
					continue;
				}
				componentTypes.add(component.getClass().getCanonicalName());

				Savable savableComponent = (Savable) component;
				if(savableComponent.getDefClass() == null)
					throw new RuntimeException("Def class is null for component type " + savableComponent.getClass());
				IComponentDef <?> def = (IComponentDef) prefab.getDef(savableComponent.getDefClass());
				savableComponent.save(def, savedProps);
			}

			savedata.addEntity(String.valueOf(savedProps.get(LifecycleDef.PROP_ID)), savedProps);
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

	public SavedLevel load(String savename)
	{
		FileHandle savefile =  toSaveFile(savename);
		String processId = "Reading save " + savefile;
		Debug.startTiming(processId);
		Json json = new Json(OutputType.minimal);

		SavedLevel savedLevel = json.fromJson(SavedLevel.class, savefile);



		Debug.stopTiming(processId);

		return savedLevel;
	}


	private FileHandle toSaveFile(String savename)
	{
		FileHandle savefile = Gdx.files.local(SAVE_DIR + "/" + savename);
		return savefile;
	}

	public static String gridToBase64(float [][] grid)
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
	public static float [][] base64ToGrid(String base64, int w, int h)
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
