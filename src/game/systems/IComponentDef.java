package game.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import game.world.Level;

public interface IComponentDef<C extends Component>
{

	/**
	 * Initialize the component from definitions.
	 * TODO: remove all defs storage inside the component, copy values instead
	 * @param component
	 * @param entity
	 * @param level
	 */
	@Deprecated
	void initComponent( C component,Entity entity, Level level );

	//	default void initComponent( C component, EntityProps props, Entity entity, Level level ) {}
	/**
	 * @return Class of the component defined here
	 */

	Class<C> getComponentClass();
	//public IComponentDef<C> copy();
}
