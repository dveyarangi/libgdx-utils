package game.systems.order;

import com.badlogic.ashley.core.Entity;

import game.systems.IComponentDef;
import game.world.Level;

public class ControlDef implements IComponentDef <ControlComponent>
{

	public ControlDef()
	{
	}

	@Override
	public Class <ControlComponent> getComponentClass() { return ControlComponent.class; }

	@Override
	public void initComponent( ControlComponent component, Entity entity, Level level )
	{
	}

}
