package game.systems.kinematic;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import game.systems.movement.IMovementComponent;
import game.util.Equals;
import lombok.Getter;

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

	@Getter private float vx;
	@Getter private float vy;
	@Getter private float va;
	@Getter private float vr;
	@Getter private float maxSpeed;
	
	@Getter private boolean isStatic = true;

	@Override
	public void reset()
	{
		vx = vy = va = vr;
	}
	
	protected boolean areStatic()
	{
		return Equals.isZero(vx) && Equals.isZero(vy) && Equals.isZero(va) && Equals.isZero(vr);
	}
	
	public void setLinearVelocity(float vx, float vy)
	{
		this.vx = vx; 
		this.vy = vy;
		isStatic = areStatic();
	}
	
	public void setAngularVelocity(float va)
	{
		this.va = va;
		isStatic = areStatic();
	}

	public void setExpansion(float vr)
	{
		this.vr = vr;
		isStatic = areStatic();
	}


	public boolean isUndefined()
	{
		return Float.isNaN(vx) || Float.isNaN(vy);
	}

}
