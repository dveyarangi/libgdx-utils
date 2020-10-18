package game.systems.spatial;

import game.systems.IComponentDef;

/**
 * Position, orientation and size definitions of a object.
 * This def is meant to be modifiable, providing means to set unit's initial position
 * without recreating entire unit definition.
 *
 * @author Fima
 *
 * @param <C>
 */
public interface ISpatialDef<C extends SpatialComponent> extends IComponentDef<C>
{

	public float x();

	public float y();
	public float z();

	public float a();

	public float r();

	public void x( float x );

	public void y( float y );

	public void z( float z );

	public void a( float a );

	public void r( float r );

}
