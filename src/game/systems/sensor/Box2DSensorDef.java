package game.systems.sensor;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import game.systems.IComponentDef;
import game.util.RandomUtil;
import game.world.Level;
import lombok.Getter;

public class Box2DSensorDef implements IComponentDef<Box2DSensorComponent>, ISensorDef
{
	float sensingInterval;
	int factionId;
	@Getter float radius;

	public BodyDef bodyDef = new BodyDef();
	public FixtureDef fixtureDef = new FixtureDef();

	public Box2DSensorDef()
	{

	}

	public Box2DSensorDef( float radius,float sensingInterval, int factionId )
	{
		this.radius = radius;
		this.sensingInterval = sensingInterval;
		this.factionId = factionId;
	}

	@Override
	public Class<Box2DSensorComponent> getComponentClass()
	{
		return Box2DSensorComponent.class;
	}

	@Override
	public void initComponent( Box2DSensorComponent component, Entity entity, Level level )
	{
		component.radius = radius;

		// component.radius = radius;
		component.sensingInterval = sensingInterval;

		// desynchronizing sensor phase:
		component.timeSinceSensing = RandomUtil.R(sensingInterval);

		component.factionId = factionId;

		component.def = this;
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
