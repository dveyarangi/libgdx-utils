package game.systems.lifecycle;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

import game.systems.EntityDef;
import game.systems.kinematic.KinematicMovementDef;
import game.systems.lifecycle.BirthTrailDeathDef.Aspect;
import game.systems.movement.IMovementComponent;
import game.systems.spatial.ISpatialComponent;
import game.systems.spatial.ISpatialDef;

/**
 * Entity lifecycle visual effects bundle.
 *
 * @author Fima
 */
public class LifeAuraComponent implements Component, Poolable
{
	private static final ComponentMapper<LifeAuraComponent> MAPPER = ComponentMapper.getFor(LifeAuraComponent.class);

	public static LifeAuraComponent get( Entity entity )
	{
		return MAPPER.get(entity);
	}

	/**
	 * Definitions of birth-trail-death effect entities.
	 */
	public BirthTrailDeathDef def;

	/**
	 * Time since last trail appearance
	 */
	public float timeSinceSpawn = 0;

	public LifeAuraComponent() { }

	@Override
	public void reset()
	{
		def = null;
	}

	/**
	 * Create definition for
	 * @param entity
	 * @param aspect
	 * @return
	 */
	private EntityDef createDef( Entity entity, Aspect aspect )
	{
		if( aspect == null )
			return null;

		EntityDef def = aspect.def;

		ISpatialComponent spatial = ISpatialComponent.get(entity);

		ISpatialDef spatialDef = def.getDef(ISpatialDef.class);
		spatialDef.x(spatial.x());
		spatialDef.y(spatial.y());
		spatialDef.a(aspect.angleDist.eval());
		spatialDef.r(aspect.sizeDist.eval());

		IMovementComponent movement = entity.getComponent( IMovementComponent.class );
		if(movement != null)
		{
			KinematicMovementDef moveDef = def.getDef(KinematicMovementDef.class);
			moveDef.vx = -movement.getMaxSpeed() * spatial.u();
			moveDef.vy = -movement.getMaxSpeed() * spatial.v();
			// moveDef.va = 0;
		}
		return def;

	}

	public EntityDef createBirthDef( Entity entity )
	{
		return this.createDef(entity, def.birth);
	}

	public EntityDef createDeathDef( Entity entity )
	{
		return this.createDef(entity, def.death);
	}

	public EntityDef createTrailDef( Entity entity )
	{
		return this.createDef(entity, def.trail);
	}
}
