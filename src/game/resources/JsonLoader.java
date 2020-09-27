package game.resources;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
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
import com.google.gson.JsonParseException;

import game.util.colors.ColormapConf;
import game.util.colors.Colormaps;

public class JsonLoader extends
		AsynchronousAssetLoader<Configuration, Configuration.Parameter>
{
	Configuration cfg;
	
	Gson dependenciesLoader;
	Gson resourcesJson;
	
	Array<AssetDescriptor> dependencies = new Array <> ();
	
	Map <Class, JsonDeserializer> customDeserializers;

	public JsonLoader( ResourceFactory factory, FileHandleResolver resolver, Map <Class, JsonDeserializer> customDeserializers )
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
		
		preJsonBuilder.registerTypeAdapter(Texture.class, new JsonDeserializer <Texture>() {
					@Override
					public Texture deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
					{
						String textureName = json.getAsString();
						TextureLoader.TextureParameter params = new TextureLoader.TextureParameter();
						params.genMipMaps = true;
						AssetDescriptor<Texture> desc = new AssetDescriptor<Texture>(Gdx.files.internal(textureName), Texture.class, params);
						dependencies.add(desc);
						//factory.loadTexture(textureName, true, 1);
						return null;
					}});
		preJsonBuilder.registerTypeAdapter(TextureAtlas.class, new JsonDeserializer <TextureAtlas>() {
			@Override
			public TextureAtlas deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
			{
				String atlasName = json.getAsString();
				AssetDescriptor<TextureAtlas> desc = new AssetDescriptor<TextureAtlas>(Gdx.files.internal(atlasName), TextureAtlas.class);
				dependencies.add(desc);
				//factory.loadAtlas(atlasName, 1);
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
				AssetDescriptor<Configuration> desc = new AssetDescriptor<Configuration>(Gdx.files.internal(cfgName), Configuration.class, param);
				dependencies.add(desc);
				return null;
			}

		});			
		
		if( customDeserializers != null)
			for(Class type : customDeserializers.keySet() )
				preJsonBuilder.registerTypeAdapter(type, customDeserializers.get(type));
		
		this.dependenciesLoader = preJsonBuilder.create();
		
		
		
		postJsonBuilder.registerTypeAdapter(TextureAtlas.class, new JsonDeserializer <TextureAtlas>() {
			@Override
			public TextureAtlas deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
			{
				String atlasName = json.getAsString();
				TextureAtlas texture = factory.getTextureAtlas(atlasName);
				return texture;
			}

		});
		postJsonBuilder.registerTypeAdapter(Texture.class, new JsonDeserializer <Texture>()
		{
			@Override
			public Texture deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
			{
				String textureName = json.getAsString();
				Texture texture = factory.getTexture(textureName);
				return texture;
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
					return factory.getConfiguration(json.getAsString());
			}

		});		
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
		if( !file.isDirectory() )
		{
			String jsonText = file.readString();
		
			Object cfg = resourcesJson.fromJson(jsonText, parameter.getType());

			return new Configuration(cfg);
		}
		else
		{
			List <Object> cfgs = new ArrayList <> ();
			for (FileHandle entry: file.list()) 
			{
				
				String jsonText = entry.readString();
				
				Object o = resourcesJson.fromJson(jsonText, parameter.getType());
				
				cfgs.add(o);
			}
			return new Configuration(cfg);
		}
		
	}

	@Override
	public Array<AssetDescriptor> getDependencies( String fileName,
			FileHandle file, Configuration.Parameter parameter )
	{
		dependencies.clear();
		if( file.list().length == 0)
		{
			String jsonText = file.readString();
		
			Object o = dependenciesLoader.fromJson(jsonText, parameter.getType());
			return new Array <AssetDescriptor>(dependencies);
		}
		else
		{
			for (FileHandle entry: file.list()) 
			{
				String jsonText = entry.readString();
				
				Object o = dependenciesLoader.fromJson(jsonText, parameter.getType());
				
			}
			return new Array <AssetDescriptor>(dependencies);
		}
	}

	



}
