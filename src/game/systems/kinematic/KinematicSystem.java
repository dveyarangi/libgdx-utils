package game.systems.kinematic;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import game.systems.spatial.ISpatialComponent;
import game.util.Angles;

/**
 * System that controls the simple unit movement. This system update the spatial
 * component with entity position
 *
 * @author Fima
 */
public class KinematicSystem extends IteratingSystem implements EntityListener
{

	@SuppressWarnings( "unchecked" )
	public KinematicSystem()
	{
		super(Family.all(KinematicComponent.class).get());
	}

	@Override
	public void addedToEngine( Engine engine )
	{
		super.addedToEngine(engine);
		engine.addEntityListener(this.getFamily(), this);
	}

	@Override
	public void entityAdded( Entity entity )
	{
		KinematicComponent kinematics = KinematicComponent.get(entity);
		// if kinematics are undefined, we use max speed and current orientation
		// to set a linear movement:
		if( kinematics.isUndefined() )
		{
			ISpatialComponent spatial = ISpatialComponent.get(entity);
			float vx = kinematics.maxSpeed * (float) Math.cos(spatial.a() * Angles.TO_RAD);
			float vy = kinematics.maxSpeed * (float) Math.sin(spatial.a() * Angles.TO_RAD);

			kinematics.vx = vx;
			kinematics.vy = vy;
			kinematics.va = 0;
		}
	}

	@Override
	protected void processEntity( Entity entity, float deltaTime )
	{
		ISpatialComponent spatial = ISpatialComponent.get(entity);
		KinematicComponent kinematics = KinematicComponent.get(entity);

		spatial.transpose(kinematics.vx * deltaTime, kinematics.vy * deltaTime);
		spatial.rotate(kinematics.va * Angles.TAU * deltaTime);

		spatial.resize(spatial.r() + kinematics.vr * deltaTime);
	}

	@Override
	public void entityRemoved( Entity entity )
	{
	}

	@Override
	public void removedFromEngine( Engine engine )
	{
		engine.removeEntityListener(this);
		super.removedFromEngine(engine);
	}

}
