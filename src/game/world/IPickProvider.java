package game.world;

import com.badlogic.ashley.core.Entity;

import game.systems.box2d.IEntityFilter;

/**
 * Interface for classes that provide entity picking by mouse
 *
 */
public interface IPickProvider
{
	/**
	 * Allows setting a filter for entities picked
	 * @param filter
	 */
	public void setEntityFilter(IEntityFilter filter);
	
	/**
	 * Do pick
	 * @param x
	 * @param y
	 * @param pickRadius
	 * @return entity matching filter (if set) in the specified radius; or null if none found
	 */
	Entity pick( float x, float y, float pickRadius );

}
