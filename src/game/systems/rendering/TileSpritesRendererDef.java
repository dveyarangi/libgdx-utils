package game.systems.rendering;

import com.badlogic.ashley.core.Entity;

import game.systems.IComponentDef;
import game.systems.SystemDef;
import game.world.Level;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class TileSpritesRendererDef extends SystemDef <TileSpritesRenderer>  implements IComponentDef <TileSpritesRenderer>
{
	public int width;
	public int height;

	public MeshDef [] meshes;
	
	public TileSpritesRendererDef()
	{
		super();
		
		this.system = new TileSpritesRenderer();
		
	}
	
	public void initSystem( Level level, TileSpritesRenderer system )
	{
		system.cam = level.getModules().getCameraProvider().getCamera();
		system.rendererDef = this;
		
	}

	@Override
	public Class<TileSpritesRenderer> getComponentClass() {
		return TileSpritesRenderer.class;
	}

	@Override
	public void initComponent(TileSpritesRenderer component, Entity entity, Level level)
	{
		component.init( entity, this, level );
	}
	
	@AllArgsConstructor
	public static class MeshDef 
	{
		@Getter String shaderName;
		@Getter String textureName;
		@Getter int unitsPerTile = 1;
		
	}

}
