package game.systems.kinematic;

import game.systems.movement.IMovementComponent;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

public class KinematicComponent implements IMovementComponent
{
	static
	{
		// ComponentType.registerFor(IMovementComponent.class,
		// KinematicComponent.class);
	}
	private static ComponentMapper<KinematicComponent> MAPPER = ComponentMapper.getFor(KinematicComponent.class);

	public static KinematicComponent get( Entity entity )
	{
		return MAPPER.get(entity);
	}

	public float vx;
	public float vy;
	public float va;
	public float vr;
	public float maxSpeed;

	@Override
	public void reset()
	{
		vx = vy = va = vr;
	}

	public boolean isUndefined()
	{
		return Float.isNaN(vx) || Float.isNaN(vy);
	}

	@Override
	public float getMaxSpeed() { return maxSpeed; }

}
