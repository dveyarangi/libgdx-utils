package game.systems.lifecycle;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

import game.systems.EntityPrefab;
import game.systems.kinematic.KinematicDef;
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
	private EntityPrefab createDef( Entity entity, Aspect aspect )
	{
		if( aspect == null )
			return null;

		EntityPrefab def = aspect.def;

		ISpatialComponent spatial = ISpatialComponent.get(entity);

		ISpatialDef spatialDef = def.getDef(ISpatialDef.class);
		spatialDef.x(spatial.x());
		spatialDef.y(spatial.y());
		spatialDef.a(aspect.angleDist.eval());
		spatialDef.resize(aspect.sizeDist.eval());

		IMovementComponent movement = entity.getComponent( IMovementComponent.class );
		if(movement != null)
		{
			KinematicDef moveDef = def.getDef(KinematicDef.class);
			moveDef.vx = -movement.getMaxSpeed() * spatial.u();
			moveDef.vy = -movement.getMaxSpeed() * spatial.v();
			// moveDef.va = 0;
		}
		return def;

	}

	public EntityPrefab createBirthDef( Entity entity )
	{
		return this.createDef(entity, def.birth);
	}

	public EntityPrefab createDeathDef( Entity entity )
	{
		return this.createDef(entity, def.death);
	}

	public EntityPrefab createTrailDef( Entity entity )
	{
		return this.createDef(entity, def.trail);
	}
}
