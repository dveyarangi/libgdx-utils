package game.systems.lifecycle;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.PooledLinkedList;

import game.systems.DescendantsComponent;
import game.systems.EntityDef;
import game.systems.EntityFactory;
import game.systems.EntityId;
import game.systems.spatial.AnchorComponent;
import game.systems.spatial.ISpatialComponent;
import game.world.Constants;

/**
 * Manages entity's Great Cycle.
 *
 * Bores and kills entities; handles @{link LifeAuraComponent} to create birth, trail and death effects.
 *
 * @author Fima
 */
public class LifecycleSystem extends EntitySystem implements EntityListener
{
	/**
	 * The Configurer, One Who Manages The Great Pool
	 */
	private EntityFactory configurer;

	/**
	 * Entities with lifecycle.
	 */
	private ImmutableArray<Entity> entities;

	/**
	 * Temporal pool of removed entities
	 */
	PooledLinkedList<Entity> graveyard = new PooledLinkedList<Entity>( Constants.GRAVEYARD_POOL_SIZE );

	/**
	 * Provides the system with entity factory.
	 * @param factory
	 */
	public LifecycleSystem( EntityFactory factory )
	{
		// all that live are subject to the Cycle
		super();
		this.configurer = factory;
	}

	/**
	 * Registers the system to listen to lifecyclish entities appearance.
	 */
	@Override
	public void addedToEngine( Engine engine )
	{
		Family family = Family.one(LifecycleComponent.class).get();

		entities = engine.getEntitiesFor(family);
		engine.addEntityListener(family, 0, this);
	}

	@Override
	public void entityAdded( Entity entity )
	{
		LifecycleComponent lifecycle = LifecycleComponent.get(entity);
		lifecycle.isAlive = true; // no officially alive
		entity.flags = EntityId.UID();
	}

	@Override
	public void update( float deltaTime )
	{

		/////////////////////////////////////////////////////
		// advance life time for all entities;
		// determine if life is elapsed;
		// create lifecycle effects, if applicable
		for( Entity entity : entities )
		{
			LifecycleComponent lifecycle = LifecycleComponent.get(entity);
			// //////////////////////////////////////////////////////////////////////
			// being born, living a little and then dying:

			boolean isNewborn = false;

			// marking newborn:
			if( lifecycle.lifetime == 0 )
			{
				isNewborn = true;
			}
			// advancing life time
			lifecycle.lifetime += deltaTime;

			// killing of age:
			if( lifecycle.lifetime > lifecycle.lifelen() )
			{
				lifecycle.setDead();
			}

			if( !lifecycle.isAlive() )
			{
				graveyard.add(entity);
			}

			// //////////////////////////////////////////////////////////////////////
			// manifesting those steps:

			LifeAuraComponent aura = LifeAuraComponent.get(entity);
			if( aura != null )
			{
				// ISpatialComponent spatial = ISpatialComponent.get( entity );
				if( isNewborn )
				{
					configurer.addEntity(aura.createBirthDef( entity ));
				}
				else if( !lifecycle.isAlive() )
				{
					configurer.addEntity(aura.createDeathDef( entity ));
				}
				else
				{
					if( aura.def.trailTotalDuration > lifecycle.lifetime )
					{
						aura.timeSinceSpawn += deltaTime;
						if( aura.timeSinceSpawn >= aura.def.trailInterval )
						{
							EntityDef def = aura.createTrailDef( entity );
							configurer.addEntity(def);
							aura.timeSinceSpawn = 0;
						}
					}
				}
			}
		}

		// //////////////////////////////////////////////////////////////////////
		// sending to The Great Pool, may be dead from other reasons also
		this.sweepGraveyard();
	}

	/**
	 * Dismantle entities that marked as dead.
	 */
	private void sweepGraveyard()
	{
		if(graveyard.size() == 0)
			return;
		
		graveyard.iter();
		Entity corpse;
		while( ( corpse = graveyard.next() ) != null )
		{
			ISpatialComponent spatial = ISpatialComponent.get(corpse);

			// removing this part of entity tree
			DescendantsComponent descendants = DescendantsComponent.get(corpse);
			if( descendants != null )
			{
				// kill children:
				for( int idx = 0; idx < descendants.size(); idx ++ )
				{
					Entity child = descendants.get(idx);
					LifecycleComponent childsLife = LifecycleComponent.get(child);
					// TODO: no lifecycle aura for children:
					childsLife.setDead();
					configurer.removeEntity(child);
				}			}

			// informing parent:
			if( spatial != null && spatial instanceof AnchorComponent )
			{
				AnchorComponent anchor = (AnchorComponent) spatial;
				DescendantsComponent family = DescendantsComponent.get(anchor.p());
				if( family != null ) // kind of Jesus otherwise
					family.remove(corpse);
			}

			// thats it:
			configurer.removeEntity(corpse);
		}

		graveyard.clear();
	}


	@Override
	public void entityRemoved( Entity entity )
	{
	}

	@Override
	public void removedFromEngine( Engine engine )
	{
		engine.removeEntityListener(this);
		super.removedFromEngine(engine);
	}
}
