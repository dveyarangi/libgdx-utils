package game.resources;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;

public interface ResourceTypeFactory <T,P extends AssetLoaderParameters<T>>
{
	public Class <T> getResourceType();
	public Class <?> getAnnotation();
	
	public AssetLoader <T,P> createLoader(FileHandleResolver resolver);
}
