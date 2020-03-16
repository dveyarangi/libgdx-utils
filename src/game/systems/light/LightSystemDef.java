package game.systems.light;

import com.badlogic.gdx.graphics.OrthographicCamera;

import game.systems.SystemDef;
import game.systems.box2d.Box2DFabric;
import game.systems.rendering.ISystemRenderer;
import game.world.Level;

public class LightSystemDef extends SystemDef <LightSystem>
{
	boolean makeShadows;

	public LightSystemDef()
	{
		super(LightSystem.class);
	}

	@Override
	public void initSystem( Level level, LightSystem system )
	{
		Box2DFabric fabric = (Box2DFabric)level.getModules().getEnvironment();
		OrthographicCamera camera = level.getModules().getCameraProvider().getCamera();
		system.init( fabric.getWorld(), camera, this );
	}

	public ISystemRenderer createRenderer(LightSystem system)
	{
		return new LightSystemRenderer(system);
	}
}
