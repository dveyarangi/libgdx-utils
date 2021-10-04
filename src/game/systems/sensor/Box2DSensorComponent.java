package game.systems.sensor;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.PooledLinkedList;

import game.systems.faction.FactionComponent;
import game.world.Constants;

/**
 * Defines box2d sensor body.
 *
 * TODO: sensor filters
 *
 * @author Fima
 */
public class Box2DSensorComponent implements Component, Poolable
{
	public static ComponentMapper<Box2DSensorComponent> MAPPER = ComponentMapper.getFor(Box2DSensorComponent.class);

	public static Box2DSensorComponent get( Entity entity )
	{
		return MAPPER.get(entity);
	}

	public Box2DSensorDef def;

	public float radius;

	/**
	 * Sensor body
	 */
	public Body body;

	/**
	 * If true, sensed entities will be propagated to children that have TargetComponent
	 */
	public boolean propagateToDescendants = true;

	public float sensingInterval;
	public float timeSinceSensing;

	public int factionId;

	public PooledLinkedList<Entity> sensed = new PooledLinkedList<>( Constants.SENSOR_POOL_SIZE );

	@Override
	public void reset()
	{
		body = null;
		timeSinceSensing = 0;
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
			FactionComponent faction = FactionComponent.get(sensedEntity);
			if( faction == null)
				return;
			if( faction.id() == factionId )
				sensed.add(sensedEntity);


		}
	}
	/**
	 * Called by Fabric when an entity no longer overlaps the sensing body.
	 * @param sensedEntity
	 */
	public void unsense( Entity sensedEntity )
	{
		FactionComponent faction = FactionComponent.get(sensedEntity);
		if( faction == null)
			return;
		if( faction.id() == factionId )
		{
			sensed.iter();
			Entity entity;
			while( ( entity = sensed.next() ) != null )
			{
				if( entity == sensedEntity )
				{
					sensed.remove();
					break;
				}
			}
		}
	}
	public boolean shouldSense()
	{
		return timeSinceSensing >= sensingInterval;
	}

	public PooledLinkedList<Entity> getSensedEntities()
	{
		return sensed;
	}

	public void clear()
	{
		sensed.clear();
	}


}
