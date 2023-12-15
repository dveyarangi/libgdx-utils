package game.systems.lifecycle;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.utils.PooledLinkedList;

import game.systems.DescendantsComponent;
import game.systems.EntityId;
import game.systems.EntityPrefab;
import game.systems.IComponentDef;
import game.systems.IDefComponent;
import game.systems.PooledEngine;
import game.systems.spatial.AnchorComponent;
import game.systems.spatial.ISpatialComponent;
//import game.systems.spatial.ISpatialComponent;
import game.world.Constants;
import game.world.Level;
import game.world.LevelDef;
import game.world.Transient;
import game.world.saves.Savable;

/**
 * Manages entity's Great Cycle.
 *
 * Bores and kills entities; handles @{link LifeAuraComponent} to create birth, trail and death effects.
 *
 * @author Fima
 */
@Transient
public class LifecycleSystem extends EntitySystem implements EntityListener
{
	private PooledEngine engine;
	
	private Level level;

	/**
	 * Newborn entities arrive here
	 */
	private PooledLinkedList<Entity> nursery = new PooledLinkedList <Entity>(Integer.MAX_VALUE);
	
	/**
	 * Entities with lifecycle.
	 */
	private PooledLinkedList<Entity> entities = new PooledLinkedList <Entity>(Integer.MAX_VALUE);

	/**
	 * Temporal pool of removed entities
	 */
	PooledLinkedList<Entity> graveyard = new PooledLinkedList<Entity>( Constants.GRAVEYARD_POOL_SIZE );

	/**
	 * Provides the system with entity factory.
	 * @param factory
	 */
	public LifecycleSystem()
	{
		// all that live are subject to the Cycle
		super();
	}

	/**
	 * Registers the system to listen to lifecyclish entities appearance.
	 */
	@Override
	public void addedToEngine( Engine engine )
	{
		Family family = Family.one(LifecycleComponent.class).get();

		//entities = engine.getEntitiesFor(family);
		engine.addEntityListener(family, 0, this);
		
		this.engine = (PooledEngine) engine;
		
		this.level = engine.getSystem(Level.class);
	}

	@Override
	public void entityAdded( Entity entity )
	{
		LifecycleComponent lifecycle = LifecycleComponent.get(entity);
		nursery.add(entity);
		lifecycle.isAlive = true; // now officially alive
		entity.flags = EntityId.UID();
	}

	@Override
	public void update( float deltaTime )
	{

		
		processNursery();
		
		/////////////////////////////////////////////////////
		// advance life time for all entities;
		// determine if life is elapsed;
		// create lifecycle effects, if applicable
		entities.iter();
		Entity entity = null;
		while((entity = entities.next()) != null)
		{
			LifecycleComponent lifecycle = LifecycleComponent.get(entity);
			// //////////////////////////////////////////////////////////////////////
			// being born, living a little and then dying:

			// advancing life time
			lifecycle.lifetime += deltaTime;

			// killing of age:
			if( lifecycle.lifetime > lifecycle.lifelen() )
			{
				lifecycle.setDead();
			}

			// moving corpses to graveyard:
			if( !lifecycle.isAlive() )
			{
				graveyard.add(entity);
				entities.remove();
			}


			// checking if trail effect is required
			LifeAuraComponent aura = LifeAuraComponent.get(entity);
			if( aura != null )
			{
				if( aura.def.trailTotalDuration > lifecycle.lifetime )
				{
					aura.timeSinceSpawn += deltaTime;
					if( aura.timeSinceSpawn >= aura.def.trailInterval )
					{
						EntityPrefab def = aura.createTrailDef( entity );
						addEntity(def);
						aura.timeSinceSpawn = 0;
					}
				}
			}
		}

		// //////////////////////////////////////////////////////////////////////
		// sending to The Great Pool, may be dead from other reasons also
		this.sweepGraveyard();
	}

	private void processNursery()
	{
		nursery.iter();
		Entity baby = null;
		while((baby = nursery.next()) != null)
		{
			
			LifecycleComponent lifecycle = LifecycleComponent.get(baby);
			
			if(!lifecycle.isImmortal())
				entities.add(baby);
			
			LifeAuraComponent aura = LifeAuraComponent.get(baby);
			if( aura == null )
				continue;
			
			addEntity(aura.createBirthDef( baby ));
		}
		
		nursery.clear();
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
					getEngine().removeEntity(child);
				}			
			}
			
			ISpatialComponent spatial = ISpatialComponent.get(corpse);

			// informing parent:
			if( spatial != null && spatial instanceof AnchorComponent )
			{
				AnchorComponent anchor = (AnchorComponent) spatial;
				DescendantsComponent family = DescendantsComponent.get(anchor.p());
				if( family != null ) // kind of Jesus otherwise
					family.remove(corpse);
			}
			
			LifeAuraComponent aura = LifeAuraComponent.get(corpse);
			if( aura != null)
				addEntity(aura.createDeathDef( corpse ));

			// thats it:
			getEngine().removeEntity(corpse);
			
		}

		graveyard.clear();
	}


	@Override
	public void entityRemoved( Entity entity )
	{
		//graveyard.add(entity);
	}

	@Override
	public void removedFromEngine( Engine engine )
	{
		engine.removeEntityListener(this);
		super.removedFromEngine(engine);
	}

	public Entity addEntity( EntityPrefab def )
	{
		if( def == null ) return null;

		Entity entity = createEntity(def);

		engine.addEntity( entity );

		return entity;
	}
	
	public void killEntity(Entity entity)
	{
		LifecycleComponent lifecycle = LifecycleComponent.get(entity);
		lifecycle.isAlive = false;
		graveyard.add(entity);
	}
	
	public void createEntities( LevelDef def )
	{
		// ////////////////////////////////////////////////////
		// create starting units:
		for( int idx = 0; idx < def.getEntityDefs().size(); idx ++ )
		{
			EntityPrefab entityDef = def.getEntityDefs().get(idx);
			assert entityDef != null : "Unit def is null, looks like redundant comma in units list in config";
			Entity unit = createEntity(entityDef);

			engine.addEntity(unit);
		}
	}

	/**
	 * Creates entity from definitions.
	 *
	 * Every created entity has the {@link LifecycleComponent} as well as
	 * other components, specified by definitions.
	 * @param prefab
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	private Entity createEntity( EntityPrefab prefab )
	{
		Entity entity = engine.createEntity();

		if( prefab.hasDescendants() )
		{
			entity.add(engine.createComponent(DescendantsComponent.class));
		}

		// /////////////////////////////////////////////////////////
		// Generic components
		for( int idx = 0; idx < prefab.getDefs().size; idx ++ )
		{
			IComponentDef componentDef = prefab.getDefs().get(idx);

			if(componentDef.getComponentClass() == null) // TODO: validate component type is not null
				throw new IllegalArgumentException("No class defined for component def " + componentDef);

			Component component = switch(componentDef) 
			{
				case IDefComponent<?> c -> c;
				default -> engine.createComponent(componentDef.getComponentClass());
			};
			
			// init the component:
			if( component instanceof Savable savable )
				savable.load(componentDef, prefab.getProps());
			
			//prefab.getProps().free();

			componentDef.initComponent( component, entity, level );
			// attach the component to the entity:
			entity.add(component);
		}

		// /////////////////////////////////////////////////////////
		// all done
		return entity;
	}



}
