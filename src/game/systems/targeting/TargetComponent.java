package game.systems.targeting;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;

import game.systems.EntityCapsule;
import game.systems.control.IEntityFilter;
import game.systems.spatial.ISpatialComponent;
import game.systems.spatial.SpatialComponent;
import game.world.IFabric;

public class TargetComponent implements Component, Poolable
{
	private static ComponentMapper<TargetComponent> MAPPER = ComponentMapper.getFor(TargetComponent.class);
	public static TargetComponent get( Entity entity ) { return MAPPER.get( entity ); }
	/**
	 * Target entity
	 */
	EntityCapsule target = new EntityCapsule();

	/**
	 * States whether entity should die when it has no target. TODO: not used
	 */
	private boolean decayOnNoTarget = false;

	public IEntityFilter filter;

	public boolean decayOnNoTarget()
	{
		return decayOnNoTarget;
	}

	@Override
	public void reset()
	{
		target.set(null);
		filter = null;
	}

	public Entity getTarget()
	{
		return target.entity();
	}

	public void acquireTarget( Array<Entity> sensedEntities, Entity source, IFabric fabric )
	{
		ISpatialComponent s = source.getComponent(SpatialComponent.class);
		target.set( null );
		boolean isVisible = false;
		Entity entity = null;
		for(int idx = 0; idx < sensedEntities.size; idx ++)
		{
			entity = sensedEntities.get(idx);
			ISpatialComponent t = entity.getComponent(SpatialComponent.class);
			isVisible = filter == null ? true : fabric.hasLineOfSight( s.x(), s.y(), t.x(), t.y(), filter );
			if(isVisible)
				break;
		}

		if(isVisible)
			target.set( entity );

	}

	public void setTarget( Entity entity )
	{
		target.set( entity );
	}


}
