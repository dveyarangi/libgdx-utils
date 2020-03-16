package game.world.units;

import game.world.defs.IUnitDef;
import game.world.factions.Faction;


public interface IUnit
{

	public String getType();
	//
	public <E extends IUnitDef> E getDef();

//	public SpatialComponent getSpatial();
	
//	public LifecycleComponent getLifecycle();

//	public DamageComponent getDamage();
	
//	public HullComponent getHull();
	
	public Faction getFaction();

	public void dispose();


//	public void update(float delta);

}
