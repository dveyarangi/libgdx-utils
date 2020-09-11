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

public class JsonLoader extends
		AsynchronousAssetLoader<Configuration, Configuration.Parameter>
{
	Configuration cfg;
	
	Gson preJson;
	Gson postJson;
	
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
						AssetDescriptor<Texture> desc = new AssetDescriptor<Texture>(Gdx.files.internal(textureName), Texture.class);
						dependencies.add(desc);
						System.out.println(textureName);
						factory.loadTexture(textureName, true, 1);
						return null;
					}});
		preJsonBuilder.registerTypeAdapter(TextureAtlas.class, new JsonDeserializer <TextureAtlas>() {
			@Override
			public TextureAtlas deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
			{
				String atlasName = json.getAsString();
				AssetDescriptor<TextureAtlas> desc = new AssetDescriptor<TextureAtlas>(Gdx.files.internal(atlasName), TextureAtlas.class);
				dependencies.add(desc);
				factory.loadAtlas(atlasName, 1);
				return null;
			}});		
		
		
		if( customDeserializers != null)
			for(Class type : customDeserializers.keySet() )
				preJsonBuilder.registerTypeAdapter(type, customDeserializers.get(type));
		
		this.preJson = preJsonBuilder.create();
		
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
		
		if( customDeserializers != null)
			for(Class type : customDeserializers.keySet() )
				postJsonBuilder.registerTypeAdapter(type, customDeserializers.get(type));
		
		this.postJson = postJsonBuilder.create();

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
		
			Object cfg = postJson.fromJson(jsonText, parameter.getType());

			return new Configuration(cfg);
		}
		else
		{
			List <Object> cfgs = new ArrayList <> ();
			for (FileHandle entry: file.list()) 
			{
				
				String jsonText = entry.readString();
				
				Object o = preJson.fromJson(jsonText, parameter.getType());
				
				
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
		
			Object o = preJson.fromJson(jsonText, parameter.getType());
			return new Array <AssetDescriptor>(dependencies);
		}
		else
		{
			for (FileHandle entry: file.list()) 
			{
				String jsonText = entry.readString();
				
				Object o = preJson.fromJson(jsonText, parameter.getType());
				
			}
			return new Array <AssetDescriptor>(dependencies);
		}
	}

	



}
