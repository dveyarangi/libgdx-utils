package game.systems.order;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

/**
 * Order system executes a generic orders given to entities.
 * Order may be viewed as a generic behavior method.
 *
 * @author Fima
 */
public class OrderSystem extends IteratingSystem
{

	public OrderSystem()
	{
		super( Family.one( ControlComponent.class ).get());
	}

	@Override
	protected void processEntity( Entity entity, float deltaTime )
	{
		ControlComponent orderComponent = entity.getComponent( ControlComponent.class );
		IOrder order = orderComponent.order;
		if( order != null )
			order.update( entity, deltaTime );
	}

}
