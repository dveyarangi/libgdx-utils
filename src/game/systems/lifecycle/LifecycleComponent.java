package game.systems.lifecycle;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * This entity component provides unit's birth and death effects.
 *
 * @author Fima
 */
public class LifecycleComponent implements Component, Poolable
{


	/**
	 * Entity group
	 */
	public int type;

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

}
