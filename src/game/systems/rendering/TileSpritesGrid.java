package game.systems.rendering;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import game.util.rendering.TileMultiMesh;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TileSpritesGrid 
{
	TileMultiMesh mesh;
	
	Texture texture;

	ShaderProgram shader;
	
	boolean isOn;
	float opacity;
}
