package game.systems.damage;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool.Poolable;

public class DamageComponent implements Component, Poolable
{
	public static ComponentMapper<DamageComponent> MAPPER = ComponentMapper.getFor(DamageComponent.class);

	public static DamageComponent get( Entity entity )
	{
		return MAPPER.get(entity);
	}

	public IDamage damage;
	public boolean friendlyFire;
	public boolean dieOnHit;

	public DamageComponent()
	{
	}

	public boolean dealsFriendlyDamage()
	{
		return friendlyFire;
	}

	public IDamage getDamage()
	{
		return damage;
	}

	@Override
	public void reset()
	{
		damage = null;
		friendlyFire = false;
	}

	public boolean dieOnHit()
	{
		return dieOnHit;
	};

}
