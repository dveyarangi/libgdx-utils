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
import game.resources.ResourceFactory;
import game.systems.EntityDef;
import game.systems.EntityPrefab;
import game.systems.IComponentDef;
import game.systems.lifecycle.LifecycleComponent;
import game.systems.lifecycle.LifecycleDef;
import game.systems.spatial.SpatialComponent;
import game.systems.spatial.SpatialDef;
import game.world.BlueprintFactory;
import game.world.Level;
import game.world.LevelDef;
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
			LifecycleComponent lifecycle = entity.getComponent(LifecycleComponent.class);
			SpatialComponent spatial = entity.getComponent(SpatialComponent.class);
			if(spatial != null)
				spatial.save(new SpatialDef(), savedProps);
			EntityPrefab prefab = blueprints.getBlueprint(lifecycle.type, lifecycle.path, savedProps);
			if( prefab == null)
			{
				Debug.log("Entity of type " + lifecycle.type + " is missing prefab, not saving");
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
				IComponentDef <?> def = (IComponentDef) prefab.getDef(savableComponent.getDefClass());
				savableComponent.save(def, savedProps);
			}

			savedata.addEntity(String.valueOf(savedProps.get(LifecycleDef.PROP_ID)), savedProps.getProps());
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
		FileHandle savefile =  toSaveFile(savename);
		String processId = "Reading save " + savefile;
		Debug.startTiming(processId);
		Json json = new Json(OutputType.minimal);

		SavedLevel savedLevel = json.fromJson(SavedLevel.class, savefile);

		LevelDef level = new LevelDef();

		for(SavedEntity entity :savedLevel.entities)
		{
			String path = entity.getProps().get(LifecycleDef.PROP_PATH);
			EntityDef def = ResourceFactory.getEntityDef(path);

			blueprints.insertEntity(level, def, new EntityProps(entity.getProps()));
		}

		Debug.stopTiming(processId);
	}


	private FileHandle toSaveFile(String savename)
	{
		FileHandle savefile = Gdx.files.local(SAVE_DIR + "/" + savename);
		return savefile;
	}
}
