package game.systems.targeting;

import game.systems.IComponentDef;
import game.systems.control.IEntityFilter;
import game.world.Level;

import com.badlogic.ashley.core.Entity;

/**
 * Defines targeting module for an entity.
 *
 *
 * @author Fima
 *
 */
public class TargetDef implements IComponentDef<TargetComponent>
{

	public IEntityFilter filter;

	@Override
	public Class<TargetComponent> getComponentClass()
	{
		return TargetComponent.class;
	}

	@Override
	public void initComponent( TargetComponent component, Entity entity, Level level )
	{
		component.filter = filter;
	}

}
