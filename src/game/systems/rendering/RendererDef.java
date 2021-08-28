package game.systems.rendering;

import com.badlogic.ashley.core.Entity;

import game.systems.IComponentDef;
import game.world.Level;

public abstract class RendererDef <R extends IRenderingComponent> implements IComponentDef <R>
{

	@Override
	public void initComponent( R component, Entity entity, Level level )
	{
		//ComponentType.registerFor(IRenderingComponent.class, component.getClass());
		//ResourceFactory factory = level.getModules().getGameFactory();
		component.init( entity, this, level );
	}

}
