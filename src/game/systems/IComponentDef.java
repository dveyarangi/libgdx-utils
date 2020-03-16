package game.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;

import game.world.Level;

public interface IComponentDef<C extends Component>
{
	/**
	 * @return Class of the component defined here
	 */
	public Class<C> getComponentClass();

	/**
	 * Initialize the component from definitions.
	 * TODO: remove all defs storage inside the component, copy values instead
	 * @param component
	 * @param entity
	 * @param level
	 */
	public void initComponent( C component, Entity entity, Level level );
}
