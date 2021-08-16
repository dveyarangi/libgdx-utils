package game.resources;

import java.lang.reflect.Type;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import game.systems.EntityDef;
import game.systems.IComponentDef;
import game.util.colors.ColormapConf;
import game.util.colors.Colormaps;

public class JsonLoader extends
AsynchronousAssetLoader<Configuration, Configuration.Parameter>
{
	Configuration cfg;

	/**
	 * The purpose of this loader is to mark all dependency resources, specified in a json config,
	 * and add them for loading. The parsing result is abandoned.
	 */
	Gson dependenciesLoader;
	Gson resourcesJson;

	Array<AssetDescriptor<?>> dependencies = new Array <> ();

	Map <Class<?>, JsonDeserializer<?>> customDeserializers;

	public JsonLoader( ResourceFactory factory, FileHandleResolver resolver, Map <Class<?>, JsonDeserializer<?>> customDeserializers )
	{
		super(resolver);

		GsonBuilder preJsonBuilder = new GsonBuilder();
		GsonBuilder postJsonBuilder = new GsonBuilder();

		/*		for(Class <?> cfgType : configurationTypes)
		{
			JsonDeserializer deserializer = new JsonDeserializer() {
				@Override
				public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
						throws JsonParseException
				{
					try {
						String filename = json.getAsString();

						return getByReference(filename, typeOfT);
					}
					catch(Exception e)
					{
						return context.deserialize(json, typeOfT);
					}
				}
			};

			preJsonBuilder.registerTypeAdapter(cfgType, deserializer);
			postJsonBuilder.registerTypeAdapter(cfgType, deserializer);
		}*/

		preJsonBuilder.registerTypeAdapter(TextureName.class, new JsonDeserializer <TextureName>() {
			@Override
			public TextureName deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
			{
				String textureName = json.getAsString();
				TextureLoader.TextureParameter params = new TextureLoader.TextureParameter();
				params.genMipMaps = true;
				AssetDescriptor<Texture> desc = new AssetDescriptor<>(Gdx.files.internal(textureName), Texture.class, params);
				dependencies.add(desc);
				factory.loadTexture(textureName, true, 1);
				return null;
			}});
		preJsonBuilder.registerTypeAdapter(TextureRegionName.class, new JsonDeserializer <TextureRegionName>() {
			@Override
			public TextureRegionName deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
			{
				JsonObject object = json.getAsJsonObject();
				String atlasName = object.get("atlasName").getAsString();
				//String regionName = object.get("regionName").getAsString();
				AssetDescriptor<TextureAtlas> desc = new AssetDescriptor<>(Gdx.files.internal(atlasName), TextureAtlas.class);
				dependencies.add(desc);
				factory.loadAtlas(atlasName, 1);
				return null;
			}});
		preJsonBuilder.registerTypeAdapter(TextureAtlasName.class, new JsonDeserializer <TextureAtlasName>() {
			@Override
			public TextureAtlasName deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
			{
				String atlasName = json.getAsString();
				AssetDescriptor<TextureAtlas> desc = new AssetDescriptor<>(Gdx.files.internal(atlasName), TextureAtlas.class);
				dependencies.add(desc);
				factory.loadAtlas(atlasName, 1);
				return null;
			}});
		preJsonBuilder.registerTypeAdapter(ColormapConf.class, new JsonDeserializer <ColormapConf>()
		{
			@Override
			public ColormapConf deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
			{
				if(json.isJsonObject())
					return null;
				Configuration.Parameter param = new Configuration.Parameter(typeOfT);
				String cfgName = json.getAsString();
				AssetDescriptor<Configuration> desc = new AssetDescriptor<>(Gdx.files.internal(cfgName), Configuration.class, param);
				dependencies.add(desc);
				return null;
			}

		});


		// allows to load lists of component defs
		preJsonBuilder.registerTypeAdapter(IComponentDef.class, new JsonInterfaceAdapter<IComponentDef<?>>());
		preJsonBuilder.registerTypeAdapter(EntityDef.class, new JsonInterfaceAdapter<IComponentDef<?>>());

		if( customDeserializers != null)
			for(Class type : customDeserializers.keySet() )
				preJsonBuilder.registerTypeAdapter(type, customDeserializers.get(type));

		this.dependenciesLoader = preJsonBuilder.create();



		postJsonBuilder.registerTypeAdapter(TextureAtlasName.class, new JsonDeserializer <TextureAtlasName>() {
			@Override
			public TextureAtlasName deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
			{
				String atlasName = json.getAsString();
				return new TextureAtlasName(atlasName);
			}
		});

		postJsonBuilder.registerTypeAdapter(TextureName.class, new JsonDeserializer <TextureName>()
		{
			@Override
			public TextureName deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
			{
				String textureName = json.getAsString();
				return new TextureName(textureName);
			}
		});

		postJsonBuilder.registerTypeAdapter(ColormapConf.class, new JsonDeserializer <ColormapConf>()
		{
			@Override
			public ColormapConf deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
			{
				if(json.isJsonObject())
					return Colormaps.buildGson(new GsonBuilder()).create().fromJson(json, ColormapConf.class);
				else
					return ResourceFactory.getConfiguration(json.getAsString());
			}
		});

		postJsonBuilder.registerTypeAdapter(IComponentDef.class, new JsonInterfaceAdapter<IComponentDef<?>>());
		postJsonBuilder.registerTypeAdapter(EntityDef.class, new JsonInterfaceAdapter<EntityDef>());

		if( customDeserializers != null)
			for(Class type : customDeserializers.keySet() )
				postJsonBuilder.registerTypeAdapter(type, customDeserializers.get(type));

		this.resourcesJson = postJsonBuilder.create();

	}

	@Override
	public void loadAsync( AssetManager manager, String fileName,
			FileHandle file, Configuration.Parameter parameter )
	{


	}

	@Override
	public Configuration loadSync( AssetManager manager, String fileName,
			FileHandle file, Configuration.Parameter parameter )
	{
		String jsonText = file.readString();
		if( jsonText.contains("{")) // TODO: this is a bit awkward
		{
			Object cfg = resourcesJson.fromJson(jsonText, parameter.getType());

			return new Configuration(cfg);
		}
		else
		{
			String [] files = jsonText.split("\\n");
			Object cfgArr = java.lang.reflect.Array.newInstance((Class)parameter.getType(), files.length);
			for (int idx = 0; idx < files.length; idx ++)
			{
				String filename = files[idx];
				FileHandle entry =  Gdx.files.internal(fileName + "/" + filename);
				jsonText = entry.readString();
				Object o = resourcesJson.fromJson(jsonText, parameter.getType());

				java.lang.reflect.Array.set(cfgArr, idx, o);
			}
			return new Configuration(cfgArr);
		}

	}

	@Override
	public Array<AssetDescriptor> getDependencies( String fileName,
			FileHandle file, Configuration.Parameter parameter )
	{
		dependencies.clear();

		String jsonText = file.readString();
		if( jsonText.contains("{")) // TODO: this is a bit awkward
		{
			try {
				dependenciesLoader.fromJson(jsonText, parameter.getType());
				return new Array <>(dependencies);
			}
			catch(Exception e ) { throw new RuntimeException("Failed to load json file " + fileName, e); }

		}
		else
		{
			String [] files = jsonText.split("\\n");
			for (String filename : files)
			{
				FileHandle entry =  Gdx.files.internal(fileName + "/" + filename);
				try {
					jsonText = entry.readString();

					dependenciesLoader.fromJson(jsonText, parameter.getType());
				}
				catch(Exception e ) { throw new RuntimeException("Failed to load json file " + entry, e); }

			}
			return new Array <>(dependencies);
		}

	}

}
