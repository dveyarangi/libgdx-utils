package game.systems.sensor;

import game.systems.SystemDef;
import game.world.Level;

public class SensorTargetingSystemDef extends SystemDef <SensorTargetingSystem>
{
	public SensorTargetingSystemDef()
	{
		super(SensorTargetingSystem.class);
	}
	@Override
	public void initSystem( Level level, SensorTargetingSystem sensors )
	{
		sensors.fabric = level.getModules().getEnvironment();
	}

}
