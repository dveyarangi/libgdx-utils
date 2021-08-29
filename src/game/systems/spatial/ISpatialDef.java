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
public interface ISpatialDef<C extends ISpatialComponent> extends IComponentDef<C>
{

	float x();

	float y();
	float z();

	float a();

	float r();

	void x( float x );

	void y( float y );

	void z( float z );

	void a( float a );

	void r( float r );

}
