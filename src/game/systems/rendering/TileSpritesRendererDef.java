package game.systems.rendering;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;

import game.systems.IComponentDef;
import game.systems.SystemDef;
import game.world.Level;

public class TileSpritesRendererDef extends SystemDef <TileSpritesRenderer>  implements IComponentDef <TileSpritesRenderer>
{
	public int width;
	public int height;

	public String shaderName;
	
	public Texture texture; 

	public TileSpritesRendererDef()
	{
		super(TileSpritesRenderer.class);
	}
	
	public void initSystem( Level level, TileSpritesRenderer system )
	{
		system.cam = level.getModules().getCameraProvider().getCamera();
		system.meshDef = this;

	}

	@Override
	public Class<TileSpritesRenderer> getComponentClass()
	{
		return TileSpritesRenderer.class;
	}

	@Override
	public void initComponent(TileSpritesRenderer component, Entity entity, Level level)
	{
		component.init( entity, this, level );
	}

}
