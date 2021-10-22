package game.systems.spatial;

import java.text.DecimalFormat;

import com.badlogic.gdx.math.Vector2;

import game.util.Angles;
import game.util.Equals;
import lombok.Getter;
import lombok.Setter;

public class GenericSpatialComponent implements ISpatialComponent
{
	/**
	 * Unit coordinates
	 */
	protected float x;

	protected float y;

	protected float z;

	/**
	 * Unit orientation
	 */
	protected float a = Float.NaN;

	/**
	 * Orientated space component
	 */
	protected Vector2 uv = new Vector2();

	/*
	 * Unit radius
	 */
	protected float r;

	/**
	 * Whether this entity is mirrored along y axis
	 */
	protected boolean inv;

	@Getter @Setter private boolean isChanged = true;

	@Override public float x() { return x; }
	@Override public float y() { return y; }
	@Override public float z() { return z; }
	@Override public float a() { return a; }
	@Override public float u() { return uv.x; }
	@Override public float v() { return uv.y; }
	@Override public Vector2 uv() { return uv; }
	@Override public float r() { return r; }
	@Override public boolean inv() { return inv; }

	@Override public void x( float x )
	{
		assert !Float.isNaN(x);
		if( !Equals.eq(this.x, x ))
		{
			isChanged = true;
			this.x = x;
		}
	}
	@Override public void y( float y )
	{
		assert !Float.isNaN(y);
		if( !Equals.eq(this.y, y ))
		{
			isChanged = true;
			this.y = y;
		}
	}
	@Override public void z( float z )
	{
		assert !Float.isNaN(z);
		if( !Equals.eq(this.z, z ))
		{
			isChanged = true;
			this.z = z;
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
	public void mirror( boolean inv )
	{
		if (this.inv != inv)
		{
			isChanged = true;
			this.inv = inv;
		}
	}


	@Override
	public void transpose( float x, float y )
	{
		x(this.x + x);
		y(this.y + y);
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
		x = y = a = r = 0;
		this.uv.set(0,0); // should it be (1,0)?
		isChanged = false;
	}

	static DecimalFormat fmt = new DecimalFormat("0.##");

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();

		sb		.append("(x y z)=(")
		.append(fmt.format(x())).append(" ")
		.append(fmt.format(y())).append(" ")
		.append(fmt.format(z())).append(")")
		.append("|a=").append(fmt.format(a()))
		.append("|r=").append(fmt.format(r()));
		if(inv())
			sb.append("|mirrored");

		return sb.toString();
	}

}
