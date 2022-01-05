package game.systems.spatial;

import java.text.DecimalFormat;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import game.util.Angles;
import game.util.Equals;
import lombok.Getter;

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
	 * Unit size
	 */
	protected float s;

	/**
	 * Whether this entity is mirrored along y axis
	 */
	protected boolean inv;

	@Getter private boolean isChanged = true;
	private Array <SpatialListener> listeners = new Array <> ();

	@Override public float x() { return x; }
	@Override public float y() { return y; }
	@Override public float z() { return z; }
	@Override public float a() { return a; }
	@Override public float u() { return uv.x; }
	@Override public float v() { return uv.y; }
	@Override public Vector2 uv() { return uv; }
	@Override public float r() { return s/2; }
	@Override public float s() { return s; }
	@Override public boolean inv() { return inv; }

	@Override public void x( float x )
	{
		assert !Float.isNaN(x);
		if( !Equals.eq(this.x, x ))
		{
			this.x = x;
			setChanged(true);
		}
	}

	@Override public void y( float y )
	{
		assert !Float.isNaN(y);
		if( !Equals.eq(this.y, y ))
		{
			this.y = y;
			setChanged(true);
		}
	}
	@Override public void z( float z )
	{
		assert !Float.isNaN(z);
		if( !Equals.eq(this.z, z ))
		{
			this.z = z;
			setChanged(true);
		}
	}

	@Override
	public void a( float a )
	{
		assert !Float.isNaN(a);
		if( !Equals.eq(this.a, a ))
		{
			this.a = a;
			this.uv.set(Angles.COS(this.a() * Angles.TO_RAD), Angles.SIN(this.a() * Angles.TO_RAD));
			setChanged(true);
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

	@Override
	public void resize( float s )
	{
		if( !Equals.eq(this.s, s ))
		{
			this.s = s;
			setChanged(true);
		}
	}	

	@Override
	public void mirror( boolean inv )
	{
		if (this.inv != inv)
		{
			this.inv = inv;
			setChanged(true);
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
	
	@Override
	public void reset()
	{
		x = y = a = s = 0;
		this.uv.set(0,0); // should it be (1,0)?
		isChanged = false;
		listeners.clear();
	}
	
	public void addListener(SpatialListener l ) { listeners.add(l); }
	public void removeistener(SpatialListener l ) { listeners.removeValue(l, true); }
	
	@Override
	public void setChanged(boolean isChanged)
	{
		this.isChanged = isChanged;
		if( isChanged )
			fireChanged();
	}
	
	private void fireChanged() 
	{
		for(SpatialListener l : listeners)
			l.spatialChanged(this);

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
