package game.systems.rendering;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import game.world.Level;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class TileSpritesRendererDef
{
	public int width;
	public int height;

	public MeshDef [] meshes;
	
	public TileSpritesRendererDef()
	{
		super();
	}
	
	public void initSystem( Level level, TileSpritesRenderer system )
	{
		system.cam = level.getModules().getCameraProvider().getCamera();
		system.rendererDef = this;
		
	}

	
	@AllArgsConstructor
	public static class MeshDef 
	{
		@Getter String shaderName;
		@Getter String textureName;
		@Getter int unitsPerTile = 1;
		@Getter boolean isBlended;
		@Getter ShaderContext context;
		
	}
	
	public static class ShaderContext {
		public void updateShader(ShaderProgram shader) {}
	}

}
