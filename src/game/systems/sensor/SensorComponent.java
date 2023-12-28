package game.systems.sensor;

import java.util.Iterator;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;

import game.systems.EntityCapsule;
import game.systems.fabric.CategorySet;
import lombok.Getter;

/**
 * Defines box2d sensor body.
 *
 * TODO: sensor filters
 *
 * @author Fima
 */
public class SensorComponent implements Component, Poolable, Iterable<Entity>
{
	public static ComponentMapper<SensorComponent> MAPPER = ComponentMapper.getFor(SensorComponent.class);

	public static SensorComponent get( Entity entity )
	{
		return MAPPER.get(entity);
	}

	public float radius;

	/**
	 * If true, sensed entities will be propagated to children that have TargetComponent
	 */
	public boolean propagateToDescendants = true;

	public float sensingInterval;
	public float timeSinceSensing;


	private Array<EntityCapsule> sensed = new Array<>();
	
	@Getter public CategorySet categories;
	
	private SensorIterator iterator = new SensorIterator();

	@Override
	public void reset()
	{

		timeSinceSensing = 0;
		clear();
	}

	public boolean propagateToDescendants() { return propagateToDescendants; }

	/**
	 * Called by Fabric when an entity overlaps the sensing body.
	 * @param sensedEntity
	 */
	public void sense( Entity sensedEntity )
	{
		if(shouldSense())
		{
			sensed.add(EntityCapsule.get(sensedEntity));
		}
	}
	/**
	 * Called by Fabric when an entity no longer overlaps the sensing body.
	 * @param sensedEntity
	 */
	public void unsense( Entity sensedEntity )
	{
		//sensed.removeValue(sensedEntity, true);
	}
	public boolean shouldSense()
	{
		return timeSinceSensing >= sensingInterval;
	}

	public void clear()
	{
		
		for(var capsule : sensed)
			capsule.free();
		sensed.clear();
	}
	
	/**
	 * This iterator helps to clean up sensed entities cache,
	 * dropping invalid entities
	 */
	private class SensorIterator implements Iterator <Entity>
	{

		Iterator <EntityCapsule> iterator;
		EntityCapsule nextEntity = null;
		protected SensorIterator()
		{
			iterator = SensorComponent.this.sensed.iterator();
			
		}
		
		@Override
		public boolean hasNext()
		{
			while(iterator.hasNext())
			{
				nextEntity = iterator.next();
				if(nextEntity.isValid())
					return true;
				iterator.remove();
				nextEntity.free();
			}
			return false;
		}

		@Override
		public Entity next()
		{
			return nextEntity.entity();
		}
		public void reset()
		{
			iterator = SensorComponent.this.sensed.iterator();
		}
	}

	@Override
	public Iterator<Entity> iterator()
	{
		iterator.reset();
		return iterator;
	}


}
