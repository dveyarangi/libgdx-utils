package game.systems.spatial;

import com.badlogic.ashley.core.ComponentType;
import com.badlogic.gdx.math.Vector2;

import game.util.Angles;
import game.util.Equals;
import lombok.Getter;
import lombok.Setter;

/**
 * This component defines unit's positioning, orientation and bounding radius.
 */
public class SpatialComponent implements ISpatialComponent
{
	static
	{
		ComponentType.registerFor(ISpatialComponent.class, SpatialComponent.class);
	}
	// public static ComponentMapper<SpatialComponent> MAPPER =
	// ComponentMapper.getFor(SpatialComponent.class);
	// public static SpatialComponent get(Entity entity) { return MAPPER.get(
	// entity ); }

	/**
	 * Unit coordinates
	 */
	private Vector2 pos = new Vector2();

	/**
	 * Unit orientation
	 */
	protected float a;

	/**
	 * Orientated space component
	 */
	protected Vector2 uv = new Vector2();

	/*
	 * Unit radius
	 */
	private float r;
	
	@Getter @Setter private boolean isChanged = false;

	@Override public float x() { return pos.x; }
	@Override public float y() { return pos.y; }
	@Override public float a() { return a; }
	@Override public float u() { return uv.x; }
	@Override public float v() { return uv.y; }
	@Override public Vector2 uv() { return uv; }
	@Override public float r() { return r; }

	@Override public void x( float x ) 
	{ 
		assert !Float.isNaN(x);
		if( !Equals.eq(this.pos.x, x ))
		{
			isChanged = true;
			this.pos.x = x;
		}
	}
	@Override public void y( float y ) 
	{ 
		assert !Float.isNaN(y);
		if( !Equals.eq(this.pos.y, y ))
		{
			isChanged = true;
			this.pos.y = y;
		}
	}

	@Override
	public void a( float a )
	{
		assert !Float.isNaN(a);
		if( !Equals.eq(this.a, a ))
		{
			isChanged = true;
			this.a = a;
			this.uv.set(Angles.COS(this.a() * Angles.TO_RAD), Angles.SIN(this.a() * Angles.TO_RAD));
		}
	}


	@Override
	public void uv( float u, float v )
	{
		assert !Float.isNaN(u); assert !Float.isNaN(v);
		if( !Equals.eq(this.uv.x, u ) || !Equals.eq(this.uv.y, v ))
		{
			this.uv.set(u, v);
			this.a((float) Math.atan2(v, u) * Angles.TO_DEG); // causes isChanged update!
		}
	}

	public void r( float r ) 
	{ 
		if( !Equals.eq(this.r, r ))
		{
			isChanged = true;
			this.r = r;
		}
	}


	@Override
	public void transpose( float x, float y )
	{
		x(this.pos.x + x);
		y(this.pos.y + y);
	}

	@Override
	public void rotate( float a )
	{
		this.a(this.a + a);
	}

	@Override public void resize( float newR )
	{
		this.r(newR);
	}

	@Override
	public void reset()
	{
		pos.x = pos.y = a = r = 0;
		this.uv.set(0,0); // should it be (1,0)?
		isChanged = false;
	}

}
