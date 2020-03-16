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
public abstract class ISpatialComponent implements Component, Poolable
{

	private static ComponentMapper<ISpatialComponent> MAPPER = ComponentMapper.getFor(ISpatialComponent.class);

	public static ISpatialComponent get( Entity entity )
	{
		return MAPPER.get(entity);
	}

	/** gets x coordinate */
	public abstract float x();

	/** gets y coordinate */
	public abstract float y();

	/** gets angle */
	public abstract float a();

	/** gets entity radius */
	public abstract float r();

	/** gets x component of orientation vector */
	public abstract float u();

	/** gets y component of orientation vector */
	public abstract float v();

	/** gets orientation vector */
	public abstract Vector2 uv();

	/** sets x coordinate */
	public abstract void x( float x );
	/** sets y coordinate */
	public abstract void y( float y );
	/** moves the entity to specified position */
	public abstract void transpose( float x, float y );

	/** sets angle */
	public abstract void a( float a );
	/** sets the specified angle */
	public abstract void rotate( float a );

	/** set orientation vector */
	public abstract void uv( float u, float v );
	/** sets entity radius */
	public abstract void resize( float f );

}
