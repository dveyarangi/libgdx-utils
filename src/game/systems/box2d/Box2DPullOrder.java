package game.systems.box2d;

import com.badlogic.ashley.core.Entity;

import game.systems.control.IMoveOrder;
import game.systems.spatial.ISpatialComponent;
import game.systems.targeting.TargetComponent;

public class Box2DPullOrder implements IMoveOrder
{
	private boolean isActive;
	private Entity entity;
	private float force = 150;

	@Override
	public boolean update( Entity entity, float dt )
	{
		TargetComponent target = entity.getComponent( TargetComponent.class );
		assert target != null;

		PhysicalComponent body = PhysicalComponent.get( entity );
		ISpatialComponent sourceSpatial = ISpatialComponent.get( entity );
		ISpatialComponent targetSpatial = ISpatialComponent.get( target.getTarget() );

		float dx = targetSpatial.x() - sourceSpatial.x();
		float dy = targetSpatial.y() - sourceSpatial.y();
		float dist = (float)Math.sqrt(dx*dx + dy*dy);

		float ax, ay;
		if( dist == 0)
			ax = ay = 0;
		else
		{
			float mass = body.getBody().getMass();
			ax = mass * force * dx / dist;
			ay = mass * force * dy / dist;
		}

		body.getBody().applyForce(ax, ay, sourceSpatial.x(), sourceSpatial.y(), true);

		return false;
	}

	@Override
	public void setActive( boolean isActive )
	{
		this.isActive = isActive;
	}

	@Override
	public Entity getEntity()
	{
		return entity;
	}

	@Override
	public void reset()
	{
		isActive = false;
		entity = null;
	}

}
