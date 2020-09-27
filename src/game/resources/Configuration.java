package game.resources;

import java.lang.reflect.Type;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.AssetLoader;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Configuration 
{
	/**
	 * {@link AssetLoader} paremeter class
	 */
	@AllArgsConstructor
	public static class Parameter extends AssetLoaderParameters<Configuration> {
		@Getter private Type type;
	}

	Object o;
	
	@SuppressWarnings("unchecked")
	public <O> O getObject() { return (O)o; }
}
