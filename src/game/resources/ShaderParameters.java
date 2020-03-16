package game.resources;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class ShaderParameters extends AssetLoaderParameters<ShaderProgram>
{
	private String vertexShaderFile;
	private String fragShaderFile;

	public ShaderParameters( String vertexShaderFile, String fragShaderFile )
	{
		this.vertexShaderFile = vertexShaderFile;
		this.fragShaderFile = fragShaderFile;
	}

	public String getVertexShaderFile()
	{
		return vertexShaderFile;
	}

	public String getFragShaderFile()
	{
		return fragShaderFile;
	}
}
