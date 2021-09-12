package game.systems.lifecycle;

import static game.systems.lifecycle.LifecycleDef.PROP_ID;
import static game.systems.lifecycle.LifecycleDef.PROP_LIFELEN;
import static game.systems.lifecycle.LifecycleDef.PROP_LIFETIME;
import static game.systems.lifecycle.LifecycleDef.PROP_PATH;
import static game.systems.lifecycle.LifecycleDef.PROP_TYPE;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

import game.util.Equals;
import game.world.saves.EntityProps;
import game.world.saves.Savable;
/**
 * This entity component provides unit's birth and death effects.
 *
 * @author Fima
 */

public class LifecycleComponent implements Component, Poolable, Savable<LifecycleDef>
{
	public int id;

	public String type;
	/**
	 * Entity group
	 */
	public String path;

	/**
	 * Entity elapsed life time
	 */
	public float lifetime;
	/**
	 * Entity total life duration
	 */
	public float lifelen = Float.POSITIVE_INFINITY;

	/**
	 * When set to false, entity will be destroyed by the LifecycleSystem
	 */
	public boolean isAlive = false;

	public boolean isAlive()
	{
		return isAlive;
	}

	public void update( float delta )
	{
		this.lifetime += delta;
	}

	/**
	 * Time elapsed since this entity's creation
	 *
	 * @return
	 */
	public float lifetime()
	{
		return lifetime;
	}

	/**
	 * Maximal life duration of this entity
	 *
	 * @return
	 */
	public float lifelen()
	{
		return lifelen;
	}

	public boolean isImmortal() { return Float.isInfinite(lifelen); }

	public void setDead()
	{
		this.isAlive = false;
	}


	@Override
	public void reset()
	{
		// generate new entity id:
		//id = COMPONENT_ID++;
		lifetime = 0;
		isAlive = false;
		lifelen = Float.POSITIVE_INFINITY;
	}

	// component retrieving nicety
	public static ComponentMapper<LifecycleComponent> MAPPER = ComponentMapper.getFor(LifecycleComponent.class);

	public static LifecycleComponent get( Entity entity )
	{
		return MAPPER.get(entity);
	}

	@Override
	public void save(LifecycleDef def, EntityProps props)
	{
		props.put(PROP_ID, id);
		props.put(PROP_TYPE, type);
		if(path != null) props.put(PROP_PATH, path);
		if(!Equals.eq(lifelen, def.getLiveLength()))     props.put(PROP_LIFELEN, lifelen);
		if(!isImmortal())	props.put(PROP_LIFETIME, lifetime);

	}


	@Override
	public void load(LifecycleDef def, EntityProps props)
	{
		// init/load static component properties
		type = props.get(PROP_TYPE, def.type);
		if(props.hasProp(PROP_ID))
		{
			id = props.getInt(PROP_ID);
			adjustIdGen(id);
		}
		else
			id = createId(type);

		path = props.get(PROP_PATH, def.path);
		lifelen = props.get(PROP_LIFELEN, def.lifelen);
		this.lifetime = props.get(PROP_LIFETIME, 0);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(type).append(" ").append(path).append(" (").append(id).append(")");
		if(isImmortal())
			sb.append(", age:immortal, ");
		else
			sb.append(", age:").append(lifetime).append("/").append(lifelen).append(", ");
		sb.append("alive:").append(isAlive);
		return sb.toString();
	}


	//////////////////////
	// ID generation

	static int IDGEN = 1;
	public static int createId(String type)
	{
		return IDGEN ++;
	}

	/** When loading entities, update IDGEN so that new entities won't get repeating ids */
	static void adjustIdGen(int existingId)
	{
		if(existingId > IDGEN)
			IDGEN = existingId + 1;
	}

	@Override
	public Class<LifecycleDef> getDefClass() { return LifecycleDef.class; }


}
