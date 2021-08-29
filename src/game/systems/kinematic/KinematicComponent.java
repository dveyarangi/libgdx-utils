package game.systems.kinematic;

import static game.systems.kinematic.KinematicDef.PROP_VA;
import static game.systems.kinematic.KinematicDef.PROP_VR;
import static game.systems.kinematic.KinematicDef.PROP_VX;
import static game.systems.kinematic.KinematicDef.PROP_VY;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

import game.systems.movement.IMovementComponent;
import game.util.Equals;
import game.world.saves.EntityProps;
import game.world.saves.Savable;
import lombok.Getter;

public class KinematicComponent implements IMovementComponent, Savable <KinematicDef>
{
	/*static
	{
		ComponentType.registerFor(IMovementComponent.class,
		 KinematicComponent.class);
	}*/
	private static ComponentMapper<KinematicComponent> MAPPER = ComponentMapper.getFor(KinematicComponent.class);

	public static KinematicComponent get( Entity entity )
	{
		return MAPPER.get(entity);
	}

	@Getter private float vx;
	@Getter private float vy;
	@Getter private float va;
	@Getter private float vr;
	@Getter private float maxSpeed;

	@Getter private boolean isStatic = true;

	@Override
	public void reset()
	{
		vx = vy = va = vr = 0;
	}

	protected boolean areStatic()
	{
		return Equals.isZero(vx) && Equals.isZero(vy) && Equals.isZero(va) && Equals.isZero(vr);
	}

	public void setLinearVelocity(float vx, float vy)
	{
		assert !Double.isNaN(vx);
		this.vx = vx;
		this.vy = vy;
		isStatic = areStatic();
	}

	public void setAngularVelocity(float va)
	{
		this.va = va;
		isStatic = areStatic();
	}

	public void setExpansion(float vr)
	{
		this.vr = vr;
		isStatic = areStatic();
	}


	public boolean isUndefined()
	{
		return Float.isNaN(vx) || Float.isNaN(vy);
	}

	@Override
	public void save(KinematicDef def, EntityProps props)
	{
		if( !Equals.eq(vx, def.vx ) ) props.put(PROP_VX, vx);
		if( !Equals.eq(vy, def.vy ) ) props.put(PROP_VY, vy);
		if( !Equals.eq(va, def.va ) ) props.put(PROP_VA, va);
		if( !Equals.eq(vr, def.vr ) ) props.put(PROP_VR, vr);
	}

	public static EntityProps save(EntityProps props, float vx, float vy, float va, float vr)
	{

		return props;
	}

	@Override
	public void load(KinematicDef def, EntityProps props)
	{
		setLinearVelocity(props.get(PROP_VX, def.vx), props.get(PROP_VY, def.vy));
		setAngularVelocity(props.get(PROP_VA, def.va));
		setExpansion(props.get(PROP_VR, def.vr));
	}

	@Override
	public Class<KinematicDef> getDefClass() { return KinematicDef.class; }
}
