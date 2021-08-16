package game.systems.kinematic;

import com.badlogic.ashley.core.Entity;

import game.systems.movement.IMovementDef;
import game.world.Level;
import game.world.saves.EntityProps;

/**
 * Defines a simple movement behavior. TODO: non-linear movements
 * @author Fima
 */
public class KinematicDef implements IMovementDef<KinematicComponent>
{
	public static final String PROP_VX = "vx";
	public static final String PROP_VY = "vy";
	public static final String PROP_VA = "va";
	public static final String PROP_VR = "vr";

	public static final float DEFAULT_VX = 0;
	public static final float DEFAULT_VY = 0;
	public static final float DEFAULT_VA = 0;
	public static final float DEFAULT_VR = 0;

	public static EntityProps initProps(EntityProps props, float vx, float vy, float va, float vr)
	{
		return KinematicComponent.save(props, vx, vy, va, vr);
	}

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

	public KinematicDef()
	{
		this.vx = 0;
		this.vy = 0;
		this.va = 0;
	}
	public KinematicDef(float maxSpeed)
	{
		this();
		this.maxSpeed = maxSpeed;
	}

	@Deprecated
	public KinematicDef( float vx, float vy, float va, float vr, float maxSpeed )
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
	public void initComponent( KinematicComponent component, EntityProps props, Entity entity, Level level )
	{
		component.load(props);
	}

	@Override
	public float getMaxSpeed() { return maxSpeed; }

}
