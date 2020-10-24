package game.systems.spatial;

import com.badlogic.ashley.core.Entity;

import game.world.Level;


public class SpatialDef implements ISpatialDef<SpatialComponent>
{
	public float x, y, z, a, r;

	public SpatialDef( float x, float y, float z, float a, float r )
	{
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.a = a;
		this.r = r;
	}

	public SpatialDef() { /* for serialization */ }

	@Override
	public void initComponent( SpatialComponent component, Entity entity, Level level )
	{
		component.x(this.x());
		component.y(this.y());
		
		component.r(this.r());
		component.a(this.a());
		component.setChanged(true);
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



}
