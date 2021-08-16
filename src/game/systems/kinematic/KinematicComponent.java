package game.systems.kinematic;

import static game.systems.kinematic.KinematicDef.DEFAULT_VA;
import static game.systems.kinematic.KinematicDef.DEFAULT_VR;
import static game.systems.kinematic.KinematicDef.DEFAULT_VX;
import static game.systems.kinematic.KinematicDef.DEFAULT_VY;
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

public class KinematicComponent implements IMovementComponent, Savable
{
	static
	{
		// ComponentType.registerFor(IMovementComponent.class,
		// KinematicComponent.class);
	}
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
	public void save(EntityProps props)
	{
		save(props, vx, vy, va, vr);
	}

	public static EntityProps save(EntityProps props, float vx, float vy, float va, float vr)
	{
		if( Equals.eq(vx, DEFAULT_VX ) ) props.put(PROP_VX, vx);
		if( Equals.eq(vy, DEFAULT_VY ) ) props.put(PROP_VY, vy);
		if( Equals.eq(va, DEFAULT_VA ) ) props.put(PROP_VA, va);
		if( Equals.eq(vr, DEFAULT_VR ) ) props.put(PROP_VR, vr);

		return props;
	}

	@Override
	public void load(EntityProps props)
	{
		setLinearVelocity(props.get(PROP_VX, DEFAULT_VX), props.get(PROP_VY, DEFAULT_VY));
		setAngularVelocity(props.get(PROP_VA, DEFAULT_VA));
		setExpansion(props.get(PROP_VR, DEFAULT_VR));
	}

}
