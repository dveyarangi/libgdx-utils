package game.systems.weapon;

import game.systems.SystemDef;
import game.world.Level;

public class WeaponSystemDef extends SystemDef<WeaponSystem>
{

	public WeaponSystemDef()
	{
		super(WeaponSystem.class);
	}

	@Override
	public void initSystem( Level level, WeaponSystem system )
	{
		WeaponSystem weaponSystem = (WeaponSystem) system;

		weaponSystem.init( level.getEntityFactory() );
	}

}
