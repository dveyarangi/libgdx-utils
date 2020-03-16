package game.systems.faction;

import game.systems.SystemDef;
import game.world.Level;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.graphics.Color;

public class FactionControllerDef extends SystemDef
{
	private String className;

	private int ownerId;

	// private List <IUnitDef> units;

	private Color color;
	private Color secondaryColor;

	int[] enemyFactions;

	public FactionControllerDef( Class <? extends AFaction> controllerClass, final int factionId, final String className, final Color color, final Color secondaryColor, int[] enemyFactions )
	{
		super(controllerClass);
		this.ownerId = factionId;
		this.color = color;
		this.secondaryColor = secondaryColor;
		// this.units = new ArrayList <IUnitDef> ();
		this.enemyFactions = enemyFactions;

		this.className = className;
	}

	@Override
	public void initSystem( Level level, EntitySystem system )
	{
		AFaction faction = (AFaction) system;

		faction.init( this, level );
	}

	public int getOwnerId()
	{
		return ownerId;
	}

	// public List <IUnitDef> getUnitDefs() { return units; }

	public Color getColor()
	{
		return color;
	}

	public Color getSecondaryColor()
	{
		return secondaryColor;
	}

	public boolean isEnemy( int factionId )
	{
		for( int idx = 0; idx < enemyFactions.length; idx++ )
			if( enemyFactions[idx] == factionId )
				return true;

		return false;
	}

	public String getClassName()
	{
		return className;
	}
}
