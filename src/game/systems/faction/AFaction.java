/**
 *
 */
package game.systems.faction;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;

import game.world.Level;

/**
 * Player faction
 *
 */
public abstract class AFaction extends EntitySystem implements EntityListener
{

	/**
	 * Faction for factionless units
	 */
	public static final int WORLD_FACTION_ID = -1;

	// ///////////////////////////////////////////////////
	// loaded by LevelLoader
	//

	/**
	 * unit owner for hits resolving
	 */
	private FactionControllerDef def;

	/**
	 * List of units that belong to this faction
	 */
	public List<Entity> units;

	private ComponentMapper<FactionComponent> factions = ComponentMapper.getFor(FactionComponent.class);

	protected Level level;

	public AFaction()
	{

		this.units = new ArrayList<Entity>();
	}

	public int id()
	{
		return def.getOwnerId();
	}

	public FactionControllerDef getDef()
	{
		return def;
	}

	public void init( final FactionControllerDef def, Level level )
	{
		//UnitsFactory factory
		this.def = def;
		this.level = level;
	}

	@Override
	public void addedToEngine( Engine engine )
	{
		engine.addEntityListener(Family.one(FactionComponent.class).get(), this);
	}

	@Override
	public void entityAdded( Entity entity )
	{
		FactionComponent faction = factions.get(entity);
		if( faction.id() == this.id() )
		{
			units.add(entity);
			faction.faction = this;
		}
	}

	@Override
	public abstract void update( final float delta );

	@Override
	public void entityRemoved( Entity entity )
	{
		units.remove(entity);
	}

	@Override
	public void removedFromEngine( Engine engine )
	{
		engine.removeEntityListener(this);
		units.clear();
		units = null;
	}

	public List<Entity> getUnits()
	{
		return units;
	}

	public boolean isEnemy( final Entity unit )
	{
		FactionComponent faction = factions.get(unit);

		return this.def.isEnemy(faction.id());
	}

}
