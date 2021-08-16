package game.systems.lifecycle;

import static game.systems.lifecycle.LifecycleDef.DEFAULT_LIFELEN;
import static game.systems.lifecycle.LifecycleDef.PROP_ID;
import static game.systems.lifecycle.LifecycleDef.PROP_LIFELEN;
import static game.systems.lifecycle.LifecycleDef.PROP_LIFETIME;
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

public class LifecycleComponent implements Component, Poolable, Savable
{
	public int id;
	/**
	 * Entity group
	 */
	public String type;

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
	public void save(EntityProps props)
	{
		props.put("id", id);
		props.put("type", type);
		if(!isImmortal())
		{
			props.put("lifelen", lifelen);
			props.put("lifetime", lifetime);

		}
	}

	public static EntityProps save(EntityProps props, int id, String type, long lifelen, long lifetime)
	{
		props.put(PROP_ID, id);
		props.put(PROP_TYPE, type);
		if(!Equals.eq(lifelen, DEFAULT_LIFELEN))
		{
			props.put(PROP_LIFELEN, lifelen);
			props.put(PROP_LIFETIME, lifetime);
		}
		return props;
	}


	@Override
	public void load(EntityProps props)
	{
		this.id = props.get(PROP_ID);
		LifecycleDef.adjustIdGen(this.id);
		this.type = props.get(PROP_TYPE, null);
		this.lifelen = props.get(PROP_LIFELEN, DEFAULT_LIFELEN);
		this.lifetime = props.get(PROP_LIFETIME, 0f);
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb.append(type).append(" (").append(id).append(")");
		if(isImmortal())
			sb.append(", age:immortal, ");
		else
			sb.append(", age:").append(lifetime).append("/").append(lifelen).append(", ");
		sb.append("alive:").append(isAlive);
		return sb.toString();
	}
}
