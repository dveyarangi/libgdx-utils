package game.systems.damage;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * This is the damageable aspect of a unit
 * 
 * @author Fima
 *
 */
public abstract class IHullComponent<D extends IDamage> implements Component, Poolable
{
	public static ComponentMapper<IHullComponent> MAPPER = ComponentMapper.getFor(IHullComponent.class);

	public static IHullComponent get( Entity entity )
	{
		return MAPPER.get(entity);
	}

	public abstract boolean hit( final D source, Entity damager, final float damageCoef );

	/*
	 * @Override public void reset() { // TODO Auto-generated method stub
	 * 
	 * }
	 */

}
