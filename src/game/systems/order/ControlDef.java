package game.systems.order;

import com.badlogic.ashley.core.Entity;

import game.systems.IComponentDef;
import game.systems.control.IControl;
import game.world.Level;

public class ControlDef implements IComponentDef <ControlComponent>
{

	private IControl control;

	public ControlDef(IControl control)
	{
		this.control = control;
	}

	@Override
	public Class getComponentClass() { return ControlComponent.class; }

	@Override
	public void initComponent( ControlComponent component, Entity entity, Level level )
	{
		component.entity = entity;
		component.control = this.control;
	}

}
