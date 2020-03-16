package game.resources;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

public class JsonLoader extends
		AsynchronousAssetLoader<Configuration, Configuration.Parameter>
{
	Configuration cfg;
	
	Json json;

	public JsonLoader( FileHandleResolver resolver )
	{
		super(resolver);
		
		this.json = new Json();
	}

	@Override
	public void loadAsync( AssetManager manager, String fileName,
			FileHandle file, Configuration.Parameter parameter )
	{
		
		String jsonText = file.readString();
		Object configObject = json.fromJson(parameter.getType(), jsonText);
		
		cfg = new Configuration(configObject);
	}

	@Override
	public Configuration loadSync( AssetManager manager, String fileName,
			FileHandle file, Configuration.Parameter parameter )
	{
		return cfg;
	}

	@Override
	public Array<AssetDescriptor> getDependencies( String fileName,
			FileHandle file, Configuration.Parameter parameter )
	{
		return null;
	}

}
