package game.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;

public class ShaderLoader extends
		AsynchronousAssetLoader<ShaderProgram, ShaderParameters>
{
	String fragShader, vertexShader;
	ShaderProgram shaderProgram;

	public ShaderLoader( FileHandleResolver resolver )
	{
		super(resolver);
	}

	@Override
	public void loadAsync( AssetManager manager, String fileName,
			FileHandle file, ShaderParameters parameter )
	{
		FileHandle vertexShaderFile = Gdx.files.internal(parameter
				.getVertexShaderFile());
		if( !vertexShaderFile.exists() )
			throw new IllegalStateException("cannot find default shader");
		vertexShader = vertexShaderFile.readString();
		FileHandle maskingShaderFile = Gdx.files.internal(parameter
				.getFragShaderFile());
		if( !maskingShaderFile.exists() )
			throw new IllegalStateException("cannot find masking shader");
		fragShader = maskingShaderFile.readString();
	}

	@Override
	public ShaderProgram loadSync( AssetManager manager, String fileName,
			FileHandle file, ShaderParameters parameter )
	{
		shaderProgram = new ShaderProgram(vertexShader, fragShader);
		if( !shaderProgram.isCompiled() )
		{
			Gdx.app.error("Shader", shaderProgram.getLog());
		}

		return shaderProgram;
	}

	@Override
	public Array<AssetDescriptor> getDependencies( String fileName,
			FileHandle file, ShaderParameters parameter )
	{
		return null;
	}

}
