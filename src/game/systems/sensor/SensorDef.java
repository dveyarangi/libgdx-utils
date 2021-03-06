package game.systems.sensor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import game.systems.IComponentDef;
import game.util.RandomUtil;
import game.world.Level;
import lombok.Getter;

public class SensorDef implements IComponentDef<SensorComponent>
{
	float sensingInterval;
	int factionId;
	@Getter float radius;

	public BodyDef bodyDef = new BodyDef();
	public FixtureDef fixtureDef = new FixtureDef();

	public SensorDef()
	{

	}

	public SensorDef( float sensingInterval, int factionId )
	{
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
		component.def = this;

		// component.radius = radius;
		component.sensingInterval = sensingInterval;

		// desynchronizing sensor phase:
		component.timeSinceSensing = RandomUtil.R(sensingInterval);

		component.factionId = factionId;


	}

	public FixtureDef getFixtureDef()
	{
		return fixtureDef;
	}

	public BodyDef getBodyDef()
	{
		return bodyDef;
	}

}
