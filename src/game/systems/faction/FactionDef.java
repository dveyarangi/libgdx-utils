package game.systems.faction;

import com.badlogic.ashley.core.Entity;

import game.systems.IComponentDef;
import game.world.Level;

public class FactionDef implements IComponentDef <FactionComponent>
{

	public int factionId;

	public FactionDef( int factionId )
	{
		this.factionId = factionId;
	}

	@Override public Class <FactionComponent> getComponentClass() { return FactionComponent.class; }

	@Override
	public void initComponent( FactionComponent component, Entity entity, Level level )
	{
		component.factionId = factionId;
	}

}
