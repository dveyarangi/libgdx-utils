/**
 *
 */
package game.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;

import game.systems.lifecycle.LifecycleComponent;
import game.systems.lifecycle.LifecycleSystem;
import game.systems.spatial.SpatialComponent;
import game.world.Level;
import game.world.LevelDef;
import game.world.saves.Savable;
import lombok.Getter;

/**
 * Manages entity instantiation and pooling.
 *
 * Unit is created taking properties from {@link EntityPrefab}. TODO: all the def properties are copied to component,
 * so it is safe to reuse the def with some modifications
 *
 * @author Fima
 */
public class EntityFactory
{


	/**
	 * Entity pooling and managing engine
	 */
	@Getter private PooledEngine engine;
	private LifecycleSystem lifecycle;

	private Level level;

	// PooledLinkedList<IUnitDef> birthpool = new PooledLinkedList <IUnitDef>
	// (30);

	public EntityFactory( Level level, PooledEngine engine  )
	{
		this.engine = engine;
		this.level = level;
	}

	public LifecycleSystem getLifecycle()
	{
		if( lifecycle == null)
			this.lifecycle = engine.getSystem(LifecycleSystem.class);

		return lifecycle;
	}

	public void createUnits( LevelDef def )
	{
		// ////////////////////////////////////////////////////
		// create starting units:
		for( int idx = 0; idx < def.getEntityDefs().size(); idx ++ )
		{
			EntityPrefab entityDef = def.getEntityDefs().get(idx);
			assert entityDef != null : "Unit def is null, looks like redundant comma in units list in config";
			Entity unit = createUnit(entityDef);

			engine.addEntity(unit);
		}
	}

	/*	public Entity createUnit( IUnitDef def, float x, float y, float a )
	{
		Entity entity = this.createUnit(def);
		ISpatialComponent spatial = ISpatialComponent.get(entity);
		spatial.transpose(x, y);
		spatial.rotate(a);

		return entity;
	}*/

	/**
	 * Creates entity from definitions.
	 *
	 * Every created entity has the {@link LifecycleComponent} and {@link SpatialComponent}, as well as
	 * other components, specified by definitions.
	 * @param prefab
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	public Entity createUnit( EntityPrefab prefab )
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

			Component component;
			if( componentDef instanceof SystemDef)
			{
				component = (Component)engine.getSystem(componentDef.getComponentClass());
			}
			else
				if( componentDef instanceof IDefComponent)
				{
					component = (IDefComponent)componentDef;
				}
				else
				{
					// create component for the speficied def:
					component = engine.createComponent(componentDef.getComponentClass());
				}
			// init the component:
			if( component instanceof Savable )
				((Savable)component).load(componentDef, prefab.getProps());
			else
				componentDef.initComponent( component, entity, level );
			// attach the component to the entity:
			entity.add(component);
		}
		/*		// /////////////////////////////////////////////////////////
		// defining behavior:
		IMovementDef movementDef = def.getMovementDef();
		if( movementDef != null )
		{
			IMovementComponent behavior = engine.createComponent(movementDef.getComponentClass());
			movementDef.initComponent( behavior, entity, engine );
			entity.add(behavior);
		}*/

		// /////////////////////////////////////////////////////////
		// providing renderer:
		/*		IRenderingDef renderingDef = def.getRenderingDef();
		if( renderingDef != null )
		{
			// mark as rendering component for ashley:
			ComponentType.registerFor(IRenderingComponent.class, renderingDef.getComponentClass() );

			// create and init the component:
			IRenderingComponent rendering = engine.createComponent(renderingDef.getComponentClass());
			renderingDef.initComponent( rendering, entity, gameFactory );

			entity.add(rendering);
		}*/
		// /////////////////////////////////////////////////////////
		// all done
		return entity;
	}

	public void removeUnit( Entity entity )
	{
		engine.removeEntity(entity);
	}

	public Entity addEntity( EntityPrefab def )
	{
		if( def == null ) return null;

		Entity entity = createUnit(def);

		engine.addEntity( entity );

		return entity;
	}
	/*	public Entity addEntity( IUnitDef def, float x, float y, float a )
	{
		if( def == null ) return null;

		Entity entity = this.createUnit( def, x, y, a );

		engine.addEntity( entity );

		return entity;
	}*/

	public void removeEntity( Entity entity )
	{
		getLifecycle().killEntity(entity);
	}

	public void bore()
	{
		/*
		 * birthpool.iter(); IUnitDef newborn; while((newborn =
		 * birthpool.next()) != null) { engine.addEntity( createUnit( newborn )
		 * ); } birthpool.clear();
		 */
	}

}