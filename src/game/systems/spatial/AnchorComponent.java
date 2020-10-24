package game.systems.spatial;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.ComponentType;
import com.badlogic.ashley.core.Entity;

import game.util.Angles;

/**
 * Entity positional component, anchored relatively to some parent's spatial
 * component.
 *
 * The anchored coordinates are inside rotated and transposed parent coordinate system.
 *
 * @author Fima
 */
public class AnchorComponent extends SpatialComponent
{

	public Entity parent;
	public ISpatialComponent parentSpatial;

	public void setParent( float x, float y, float z, float a, float r, Entity parent )
	{
		this.parent = parent;
		this.parentSpatial = parent.getComponent(ISpatialComponent.class);
		this.x(x);
		this.y(y);
		this.z(z);
		this.a(a);
		this.r(r);
	}

	/**
	 * TODO: this might be redundant
	 *
	 * @return
	 */
	public Entity p()
	{
		return parent;
	}

	@Override
	public void reset()
	{
		super.reset();
		parent = null;
	}

	/**
	 * Retrieve absolute x
	 */
	@Override
	public float x()
	{
		return parentSpatial.x() + parentSpatial.u() * super.x() - parentSpatial.v() * super.y();
	}

	/**
	 * Retrieve absolute y
	 */
	@Override
	public float y()
	{
		return parentSpatial.y() + parentSpatial.v() * super.x() + parentSpatial.u() * super.y();
	}
	
	public float z()
	{
		return parentSpatial.z() + super.z();
	}

	/**
	 * Retrieve absolute angle
	 */
	@Override
	public float a()
	{
		return parentSpatial.a() + super.a();
	}

	/**
	 * Set parent-relative angle of this entity
	 */
	@Override
	public void a( float a )
	{
		this.a = a;
		this.uv.set(Angles.COS(this.a() * Angles.TO_RAD), Angles.SIN(this.a() * Angles.TO_RAD));
	}

	/**
	 * Set absolute orientation vector of this entity (not parent-relative)
	 */
	@Override
	public void uv( float u, float v )
	{
		this.uv.set(u, v);
		this.a = ( (float) Math.atan2(v, u) ) * Angles.TO_DEG - parentSpatial.a();
	}

	/**
	 * Retrieve real-world orientation vector of this entity (not parent-relative)
	 */
	@Override
	public float u()
	{
		return Angles.COS(this.a() * Angles.TO_RAD);//-parentSpatial.u() * super.u() + parentSpatial.v() * super.v();
	}

	/**
	 * Retrieve real-world orientation vector of this entity (not parent-relative)
	 */
	@Override
	public float v()
	{
		return Angles.SIN(this.a() * Angles.TO_RAD);//parentSpatial.u() * super.v() + parentSpatial.v() * super.u();
	}

	////////////////////////////////////////////////////
	// component mapping stuff
	static
	{
		ComponentType.registerFor(ISpatialComponent.class, AnchorComponent.class);
	}

	public static ComponentMapper<AnchorComponent> MAPPER = ComponentMapper.getFor(AnchorComponent.class);

	public static AnchorComponent get( Entity entity )
	{
		return MAPPER.get(entity);
	}

}
