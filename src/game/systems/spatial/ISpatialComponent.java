package game.systems.spatial;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Defines positioning and kinematic properties of a game entity
 * @author Fima
 */
public interface ISpatialComponent extends Component, Poolable
{

	ComponentMapper<ISpatialComponent> MAPPER = ComponentMapper.getFor(ISpatialComponent.class);

	static ISpatialComponent get( Entity entity )
	{
		return MAPPER.get(entity);
	}

	/** gets x coordinate */
	float x();

	/** gets y coordinate */
	float y();

	/** gets z coordinate */
	float z();

	/** gets angle */
	float a();

	/** gets entity radius */
	float r();
	
	/** gets entity diameter/size */
	float s();

	/** whether entity is mirrored along y axis */
	boolean inv();

	/** gets x component of orientation vector */
	float u();

	/** gets y component of orientation vector */
	float v();

	/** gets orientation vector */
	Vector2 uv();

	/** sets x coordinate */
	void x( float x );
	/** sets y coordinate */
	void y( float y );
	/** sets y coordinate */
	void z( float z );

	/** moves the entity to specified position */
	void transpose( float x, float y );

	/** sets angle */
	void a( float a );
	/** sets the specified angle */
	void rotate( float a );

	void mirror(boolean inv);

	/** set orientation vector */
	void uv( float u, float v );
	/** sets entity radius */
	void resize( float f );

	/** True if the component position, orientation or size were changed during last heartbeat */
	boolean isChanged();
	void setChanged(boolean isChanged);

}
