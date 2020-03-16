package game.systems.sensor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import game.systems.DescendantsComponent;
import game.systems.targeting.TargetComponent;
import game.world.IFabric;

public class SensorTargetingSystem extends IteratingSystem
{

	public IFabric fabric;

	public SensorTargetingSystem()
	{
		super(Family.one(SensorComponent.class).get());
	}

	@Override
	protected void processEntity( Entity entity, float deltaTime )
	{
		SensorComponent sensor = SensorComponent.get(entity);
		// e if( sensor.shouldSense())
		{
			// System.out.println(sensor.getSensedEntities().size());
			// sensor.getSensedEntities().iter();
			if( sensor.propagateToDescendants() )
			{
				DescendantsComponent descendants = DescendantsComponent.get(entity);

				for( int idx = 0; idx < descendants.size(); idx ++ )
				{
					Entity child = descendants.get(idx);
					TargetComponent target = child.getComponent(TargetComponent.class);
					if( target == null )
						continue;

					if( sensor.getSensedEntities().size() != 0 )
					{
						//fabric.checkVisible( )
						target.acquireTarget( sensor.getSensedEntities(), entity, fabric );
					}
				}
			}

			// sensor.clear();
		}
		sensor.timeSinceSensing += deltaTime;

	}

}
