package game.systems;

import com.badlogic.ashley.core.EntitySystem;

import game.systems.rendering.ISystemRenderer;
import game.world.Level;

public class SystemDef <S extends EntitySystem>
{
	private Class <S> systemClass;

	private S system;

	private ISystemRenderer renderer;

	public SystemDef()
	{
	}
	public SystemDef( Class<S> systemClass)
	{
		this.systemClass = systemClass;
	}

	public SystemDef( S system )
	{
		this.system = system;
	}
	public SystemDef( S system, ISystemRenderer renderer )
	{
		this.system = system;
		this.renderer = renderer;
	}

	public S createSystem()
	{
		if(system != null) return system;
		try
		{
			system = systemClass.newInstance();
		} catch( InstantiationException e )
		{
			throw new IllegalArgumentException("Entity system of type " + systemClass + " not found.");
		} catch( IllegalAccessException e )
		{
			throw new IllegalArgumentException("Entity system of type " + systemClass + " not found.");
		}

		return system;
	}

	public void initSystem( Level level, S system )
	{
		// TODO Auto-generated method stub

	}
	
	public ISystemRenderer createRenderer() { return renderer; }

}
