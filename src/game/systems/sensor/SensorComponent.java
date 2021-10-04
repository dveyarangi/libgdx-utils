package game.systems.sensor;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;

import game.systems.faction.FactionComponent;

/**
 * Defines box2d sensor body.
 *
 * TODO: sensor filters
 *
 * @author Fima
 */
public class SensorComponent implements Component, Poolable
{
	public static ComponentMapper<SensorComponent> MAPPER = ComponentMapper.getFor(SensorComponent.class);

	public static SensorComponent get( Entity entity )
	{
		return MAPPER.get(entity);
	}

	public SensorDef def;

	public float radius;

	/**
	 * If true, sensed entities will be propagated to children that have TargetComponent
	 */
	public boolean propagateToDescendants = true;

	public float sensingInterval;
	public float timeSinceSensing;

	public int factionId;

	public Array<Entity> sensed = new Array<>();

	@Override
	public void reset()
	{

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
			//if( faction == null)
			//	return;
			if( factionId == 0 || faction.id() == factionId )
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
		//if( faction == null)
		//	return;
		if( factionId == 0 || faction.id() == factionId )
		{
			sensed.removeValue(sensedEntity, true);
		}
	}
	public boolean shouldSense()
	{
		return timeSinceSensing >= sensingInterval;
	}

	public Array<Entity> getSensedEntities()
	{
		return sensed;
	}

	public void clear()
	{
		sensed.clear();
	}


}
