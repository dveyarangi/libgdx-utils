package game.resources;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AssetLoader;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import game.util.colors.ColormapConf;
import game.util.colors.Colormaps;

public class ColormapResourceTypeFactory implements ResourceTypeFactory <ColormapConf, AssetLoaderParameters<ColormapConf>>
{
	@Target( ElementType.FIELD ) @Retention( RetentionPolicy.RUNTIME ) public static @interface Colormap {
		int priority() default 0;
	}
	
	@Override
	public Class<ColormapConf> getResourceType() { return ColormapConf.class; }

	@Override
	public Class<?> getAnnotation() { return Colormap.class; }

	@Override
	public AssetLoader<ColormapConf, AssetLoaderParameters<ColormapConf>> createLoader(FileHandleResolver resolver)
	{
		return new ColormapLoader(resolver);
	}

	public class ColormapLoader extends AsynchronousAssetLoader<ColormapConf, AssetLoaderParameters<ColormapConf>>
	{
		ColormapConf conf;
		
		
		public ColormapLoader( FileHandleResolver resolver )
		{
			super(resolver);
		}
		
		@Override
		public void loadAsync( AssetManager manager, String fileName,
				FileHandle file, AssetLoaderParameters<ColormapConf> parameter )
		{
			FileHandle colormapConfFile = Gdx.files.internal(fileName);
			if( !colormapConfFile.exists() )
				throw new IllegalStateException("Cannot find colormap configuration file " + fileName);
			
			String cmJson = colormapConfFile.readString();
			
			try
			{
				conf = Colormaps.readJson(cmJson);
			} 
			catch (IOException e) { throw new IllegalStateException("Failed to parse configuration file " + fileName); }
		}
		
		@Override
		public ColormapConf loadSync( AssetManager manager, String fileName,
				FileHandle file, AssetLoaderParameters<ColormapConf> parameter )
		{
			return conf;
		}
		
		@Override
		public Array<AssetDescriptor> getDependencies( String fileName,
				FileHandle file, AssetLoaderParameters<ColormapConf> parameter )
		{
			return null;
		}
	
	}

	
}
