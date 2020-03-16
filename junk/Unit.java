package game.world.units;

import game.world.defs.IUnitDef;

import com.badlogic.ashley.core.Entity;

public class Unit extends Entity implements IUnit
{



	@Override
	public <E extends IUnitDef> E getDef() {
		return null;
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
