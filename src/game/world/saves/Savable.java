package game.world.saves;

import game.systems.IComponentDef;

public interface Savable <C extends IComponentDef>
{
	void save(C def, EntityProps props);
	void load(C def, EntityProps props);
	Class<C> getDefClass();
}
