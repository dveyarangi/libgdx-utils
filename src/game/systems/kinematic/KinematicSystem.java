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
			float vx = kinematics.getMaxSpeed() * (float) Math.cos(spatial.a() * Angles.TO_RAD);
			float vy = kinematics.getMaxSpeed() * (float) Math.sin(spatial.a() * Angles.TO_RAD);

			kinematics.setLinearVelocity(vx, vy);
			kinematics.setAngularVelocity(0);;
		}
	}

	@Override
	protected void processEntity( Entity entity, float deltaTime )
	{
		ISpatialComponent spatial = ISpatialComponent.get(entity);
		KinematicComponent kinematics = KinematicComponent.get(entity);

		spatial.setChanged(false);
		if( !kinematics.isStatic() )
		{
			if( deltaTime > 1)
				deltaTime = 1;
			spatial.transpose(kinematics.getVx() * deltaTime, kinematics.getVy() * deltaTime);
			spatial.rotate(kinematics.getVa() * Angles.TAU * deltaTime);
			spatial.resize(spatial.r() + kinematics.getVr() * deltaTime);
		}
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
