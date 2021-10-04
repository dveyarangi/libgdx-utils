package game.systems.sensor;

import com.badlogic.ashley.core.Entity;

import game.systems.IComponentDef;
import game.util.RandomUtil;
import game.world.Level;
import lombok.Getter;

public class SensorDef implements IComponentDef<SensorComponent>, ISensorDef
{
	float sensingInterval;
	int factionId;
	@Getter float radius;

	public SensorDef()
	{

	}

	public SensorDef( float radius,float sensingInterval, int factionId )
	{
		this.radius = radius;
		this.sensingInterval = sensingInterval;
		this.factionId = factionId;
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
		component.timeSinceSensing = RandomUtil.R(sensingInterval);

		component.factionId = factionId;

		component.def = this;
	}


}
