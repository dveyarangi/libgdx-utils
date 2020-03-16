package game.systems.weapon;

import game.systems.SystemDef;
import game.world.Level;

import com.badlogic.ashley.core.EntitySystem;

public class WeaponSystemDef extends SystemDef
{

	public WeaponSystemDef()
	{
		super(WeaponSystem.class);
	}

	@Override
	public void initSystem( Level level, EntitySystem system )
	{
		WeaponSystem weaponSystem = (WeaponSystem) system;

		weaponSystem.init( level.getEntityFactory() );
	}

}
