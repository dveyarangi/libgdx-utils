package game.systems.spatial;

import static game.systems.spatial.SpatialDef.DEFAULT_A;
import static game.systems.spatial.SpatialDef.DEFAULT_INV;
import static game.systems.spatial.SpatialDef.DEFAULT_S;
import static game.systems.spatial.SpatialDef.DEFAULT_Z;
import static game.systems.spatial.SpatialDef.PROP_A;
import static game.systems.spatial.SpatialDef.PROP_INV;
import static game.systems.spatial.SpatialDef.PROP_S;
import static game.systems.spatial.SpatialDef.PROP_X;
import static game.systems.spatial.SpatialDef.PROP_Y;
import static game.systems.spatial.SpatialDef.PROP_Z;

import com.badlogic.ashley.core.ComponentType;

import game.util.Equals;
import game.world.saves.Props;
import game.world.saves.Savable;

/**
 * This component defines unit's positioning, orientation and bounding radius.
 */
public class SpatialComponent extends GenericSpatialComponent implements Savable<SpatialDef>
{
	static { ComponentType.registerFor(ISpatialComponent.class, SpatialComponent.class); }

	@Override
	public void load(SpatialDef def, Props props)
	{
		x(props.getFloat(PROP_X, def.x));
		y(props.getFloat(PROP_Y, def.y));
		z(props.get(PROP_Z, def.z));
		a(props.get(PROP_A, def.a));
		resize(props.get(PROP_S, def.s));
		mirror(props.get(PROP_INV, def.inv));
		setChanged(true); // event if all props are default, we still want this entity to be updated
	}

	@Override
	public void save(SpatialDef def, Props props)
	{
		props.put(PROP_X, x);
		props.put(PROP_Y, y);
		if(!Equals.eq(z, def.z)) props.put(PROP_Z, z);
		if(!Equals.eq(a, def.a)) props.put(PROP_A, a);
		if(!Equals.eq(s, def.s)) props.put(PROP_S, s);
		if(inv != def.inv) props.put(PROP_INV, inv);
	}

	public static Props save(Props props, float x, float y, float z, float a, float s, boolean inv )
	{
		props.put(PROP_X, x);
		props.put(PROP_Y, y);
		if(!Equals.eq(z, DEFAULT_Z)) props.put(PROP_Z, z);
		if(!Equals.eq(a, DEFAULT_A)) props.put(PROP_A, a);
		if(!Equals.eq(s, DEFAULT_S)) props.put(PROP_S, s);
		if(inv != DEFAULT_INV) props.put(PROP_INV, inv);
		return props;
	}

	@Override
	public Class getDefClass() { return SpatialDef.class; }
}
