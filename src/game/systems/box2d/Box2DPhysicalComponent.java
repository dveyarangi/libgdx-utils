package game.systems.box2d;

import game.systems.movement.IMovementComponent;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;

public class Box2DPhysicalComponent implements IMovementComponent
{
	static
	{
		// ComponentType.registerFor(IMovementComponent.class,
		// PhysicalComponent.class);
	}
	public static ComponentMapper<Box2DPhysicalComponent> MAPPER = ComponentMapper.getFor(Box2DPhysicalComponent.class);

	public static Box2DPhysicalComponent get( Entity entity )
	{
		return MAPPER.get(entity);
	}

	public Body body;
	public Box2DPhysicalDef def;

	public float maxSpeed;

	public Body getBody()
	{
		return body;
	}

	@Override
	public void reset()
	{
		body = null;
	}

	@Override
	public float getMaxSpeed() { return maxSpeed; }

}
