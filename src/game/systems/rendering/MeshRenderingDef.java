package game.systems.rendering;

import com.badlogic.ashley.core.Entity;

import game.util.MeshDef;
import game.world.Level;


public class MeshRenderingDef extends RendererDef<MeshRenderingComponent>
{
	public int width;
	public int height;

	public String textureName;
	public String shaderName;
	public MeshDef triangles;

	public MeshRenderingDef()
	{
		super(MeshRenderingComponent.class);
	}
	@Override
	public void initComponent( MeshRenderingComponent component, Entity entity, Level level )
	{
		super.initComponent(component, entity, level);


	}
}
