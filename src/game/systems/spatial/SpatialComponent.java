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

import game.util.Equals;
import game.world.saves.EntityProps;
import game.world.saves.Savable;

/**
 * This component defines unit's positioning, orientation and bounding radius.
 */
public class SpatialComponent extends GenericSpatialComponent implements Savable<SpatialDef>
{
	static
	{
		ComponentType.registerFor(ISpatialComponent.class, SpatialComponent.class);
	}
	// public static ComponentMapper<SpatialComponent> MAPPER =
	// ComponentMapper.getFor(SpatialComponent.class);
	// public static SpatialComponent get(Entity entity) { return MAPPER.get(
	// entity ); }


	@Override
	public void load(SpatialDef def, EntityProps props)
	{
		x(props.getFloat(PROP_X));
		y(props.getFloat(PROP_Y));
		z(props.get(PROP_Z, def.z));
		a(props.get(PROP_A, def.a));
		r(props.get(PROP_R, def.r));
		setChanged(true);
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
	public Class getDefClass() { return SpatialDef.class; }
}
