package game.systems.spatial;

import static game.systems.spatial.SpatialDef.DEFAULT_A;
import static game.systems.spatial.SpatialDef.DEFAULT_R;
import static game.systems.spatial.SpatialDef.DEFAULT_X;
import static game.systems.spatial.SpatialDef.DEFAULT_Y;
import static game.systems.spatial.SpatialDef.DEFAULT_Z;
import static game.systems.spatial.SpatialDef.PROP_A;
import static game.systems.spatial.SpatialDef.PROP_R;
import static game.systems.spatial.SpatialDef.PROP_X;
import static game.systems.spatial.SpatialDef.PROP_Y;
import static game.systems.spatial.SpatialDef.PROP_Z;

import com.badlogic.ashley.core.ComponentType;
import com.badlogic.gdx.math.Vector2;

import game.util.Angles;
import game.util.Equals;
import game.world.saves.EntityProps;
import game.world.saves.Savable;
import lombok.Getter;
import lombok.Setter;

/**
 * This component defines unit's positioning, orientation and bounding radius.
 */
public class SpatialComponent implements ISpatialComponent, Savable<SpatialDef>
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
	private float x;

	private float y;

	private float z;

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
	private float r;

	@Getter @Setter private boolean isChanged = true;

	@Override public float x() { return x; }
	@Override public float y() { return y; }
	@Override public float z() { return z; }
	@Override public float a() { return a; }
	@Override public float u() { return uv.x; }
	@Override public float v() { return uv.y; }
	@Override public Vector2 uv() { return uv; }
	@Override public float r() { return r; }

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

	@Override
	public void load(SpatialDef def, EntityProps props)
	{
		load(this, props);
	}

	public static void load(SpatialComponent s, EntityProps props)
	{
		s.x(props.get(PROP_X, DEFAULT_X));
		s.y(props.get(PROP_Y, DEFAULT_Y));
		s.z(props.get(PROP_Z, DEFAULT_Z));
		s.a(props.get(PROP_A, DEFAULT_A));
		s.r(props.get(PROP_R, DEFAULT_R));
		s.setChanged(true);
	}

	@Override
	public void save(SpatialDef def, EntityProps props)
	{
		save( props, x, y, z, a, r);
	}

	public static EntityProps save(EntityProps props, float x, float y, float z, float a, float r )
	{
		if(!Equals.eq(x, DEFAULT_X)) props.put(PROP_X, x);
		if(!Equals.eq(y, DEFAULT_Y)) props.put(PROP_Y, y);
		if(!Equals.eq(z, DEFAULT_Z)) props.put(PROP_Z, z);
		if(!Equals.eq(a, DEFAULT_A)) props.put(PROP_A, a);
		if(!Equals.eq(r, DEFAULT_R)) props.put(PROP_R, r);
		return props;
	}

	@Override
	public String toString()
	{
		return new StringBuilder()
				.append("x:").append(x()).append(",")
				.append("y:").append(y()).append(",")
				.append("z:").append(z()).append(",")
				.append("a:").append(a()).append(",")
				.append("r:").append(r())
				.toString();
	}

	@Override
	public Class getDefClass() { return SpatialDef.class; }
}
