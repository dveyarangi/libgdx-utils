package game.systems.kinematic;

import com.badlogic.ashley.core.Entity;

import game.systems.movement.IMovementDef;
import game.world.Level;

/**
 * Defines a simple movement behavior. TODO: non-linear movements
 * @author Fima
 */
public class KinematicMovementDef implements IMovementDef<KinematicComponent>
{

	/** velocity components */
	public float vx, vy;
	/** angular velocity */
	public float va;
	/** radius change */
	public float vr;
	/**
	 *  maximal movement speed
	 *  TODO: should not be here?
	 */
	public float maxSpeed;

	public KinematicMovementDef()
	{
		this.vx = 0;
		this.vy = 0;
		this.va = 0;
	}

	public KinematicMovementDef( float vx, float vy, float va, float vr, float maxSpeed )
	{
		this.vx = vx;
		this.vy = vy;
		this.va = va;
		this.vr = vr;
		this.maxSpeed = maxSpeed;
	}

	@Override
	public Class<KinematicComponent> getComponentClass()
	{
		return KinematicComponent.class;
	}

	@Override
	public void initComponent( KinematicComponent component, Entity entity, Level level )
	{
		component.setLinearVelocity(vx, vy);
		component.setAngularVelocity(va);
		component.setExpansion(vr);
	}

	@Override
	public float getMaxSpeed() { return maxSpeed; }

}
