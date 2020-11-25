package game.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

import game.world.Level;

/**
 * Used for self-defining components
 * @author Fima
 */
public interface IDefComponent extends IComponentDef, Component, Poolable
{
	@Override
	public default void initComponent(Component component, Entity entity, Level level)
	{
		
	}
}
