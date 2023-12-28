package game.systems.sensor;

import com.badlogic.ashley.core.Entity;

import game.systems.IComponentDef;
import game.systems.fabric.SpatialFabric;
import game.world.Level;
import lombok.Getter;

public class SensorDef implements IComponentDef<SensorComponent>, ISensorDef
{
	float sensingInterval;

	@Getter float radius;
	
	@Getter SensorCategory [] categories;

	public SensorDef()
	{

	}

	public SensorDef( float radius,float sensingInterval, SensorCategory [] categories )
	{
		this.radius = radius;
		this.sensingInterval = sensingInterval;
		this.categories = categories;
	}

	@Override
	public Class<SensorComponent> getComponentClass()
	{
		return SensorComponent.class;
	}

	@Override
	public void initComponent( SensorComponent component, Entity entity, Level level )
	{
		component.radius = radius;

		// component.radius = radius;
		component.sensingInterval = sensingInterval;

		// desynchronizing sensor phase:
		component.timeSinceSensing = level.getDef().getInitialSettings().getSeed().R(sensingInterval);

		var fabric = level.getEngine().getSystem(SpatialFabric.class);
		component.categories = fabric.getCategorySet(categories);

	}


}
