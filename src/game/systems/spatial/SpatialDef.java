package game.systems.spatial;

import com.badlogic.ashley.core.Entity;

import game.world.Level;
import game.world.saves.EntityProps;


public class SpatialDef implements ISpatialDef<SpatialComponent>
{
	public float x, y, z=DEFAULT_Z, a=DEFAULT_A, r=DEFAULT_R;

	@Deprecated
	public SpatialDef( float x, float y, float z, float a, float r )
	{
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.a = a;
		this.r = r;
	}

	public EntityProps initProps( EntityProps prop)
	{
		return initProps(prop, x, y, z, a, r);
	}

	/**
	 * Configure initial properties for this component type
	 */
	public static EntityProps initProps( EntityProps prop, float x, float y, float z, float a, float r )
	{
		return SpatialComponent.save(prop, x, y, z, a, r);
	}



	public SpatialDef() { /* for serialization */ }

	@Override
	public void initComponent( SpatialComponent component, Entity entity, Level level )
	{
		//component.x(x);
		//component.y(y);
		//component.z(z);
		//component.a(a);
		//component.r(r);
		//component.load(props);
	}

	@Override public float x() { return x; }
	@Override public float y() { return y; }
	@Override public float z() { return z; }
	@Override public float a() { return a; }
	@Override public float r() { return r; }

	@Override public void x( float x ) { this.x = x; }
	@Override public void y( float y ) { this.y = y; }
	@Override public void z( float z ) { this.z = z; }
	@Override public void a( float a ) { this.a = a; }
	@Override public void r( float r ) { this.r = r; }

	@Override
	public Class <SpatialComponent> getComponentClass() { return SpatialComponent.class; }

	public static final String PROP_X = "x";
	public static final String PROP_Y = "y";
	public static final String PROP_Z = "z";
	public static final String PROP_A = "a";
	public static final String PROP_R = "r";
	public static final float DEFAULT_X = 0;
	public static final float DEFAULT_Y = 0;
	public static final float DEFAULT_Z = 0;
	public static final float DEFAULT_A = 0;
	public static final float DEFAULT_R = 0.5f;
}
