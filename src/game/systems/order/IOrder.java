package game.systems.order;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Provides interface for generic entity behavior.
 * Orders attached to entity will be invoked by {@link OrderSystem}
 *
 * @author Fima
 */
public interface IOrder extends Component, Poolable
{
	/**
	 * Steps forward order execution.
	 * @param entity
	 * @param dt
	 * @return true if execution is completed.
	 */
	public boolean update(Entity entity, float dt);

	public void setActive( boolean b );

	public Entity getEntity();

}
