package game.systems.box2d;

import game.systems.movement.IMovementComponent;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;

public class PhysicalComponent implements IMovementComponent
{
	static
	{
		// ComponentType.registerFor(IMovementComponent.class,
		// PhysicalComponent.class);
	}
	public static ComponentMapper<PhysicalComponent> MAPPER = ComponentMapper.getFor(PhysicalComponent.class);

	public static PhysicalComponent get( Entity entity )
	{
		return MAPPER.get(entity);
	}

	public Body body;
	public PhysicalDef def;

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
