package game.systems.damage;

import com.badlogic.ashley.core.Entity;

import game.systems.IComponentDef;
import game.world.Level;

public class DamageDef implements IComponentDef<DamageComponent>
{
	public IDamage damage;
	public boolean friendlyFire = false;
	public boolean dieOnHit = false;

	@Override
	public Class <DamageComponent> getComponentClass() { return DamageComponent.class; }

	@Override
	public void initComponent( DamageComponent component, Entity entity, Level level )
	{
		component.damage = damage;
		component.friendlyFire = friendlyFire;
		component.dieOnHit = dieOnHit;
	}

}
