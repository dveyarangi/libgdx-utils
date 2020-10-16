package game.systems.fabric;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.PooledLinkedList;

import game.systems.control.IEntityFilter;
import game.systems.sensor.SensorComponent;
import game.systems.spatial.ISpatialComponent;
import game.world.Constants;
import game.world.IFabric;
import game.world.IPickProvider;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import yarangi.spatial.AABB;
import yarangi.spatial.ISpatialSensor;
import yarangi.spatial.SpatialHashMap;

@Slf4j
public class SpatialFabric extends EntitySystem implements IFabric, EntityListener
{
	public SpatialHashMap <SpatialIndexComponent> space;
	
	private ObjectSet<Entity> dynamicEntities = new ObjectSet <Entity>();
	
	private ImmutableArray<Entity> sensorEntities;
	
	private ComponentMapper<SpatialIndexComponent> mapper;
	
	private SensorFilter sensorFilter = new SensorFilter();
	
	@Setter private IEntityFilter entityFilter;

	
	//Pool pool = 
	
	public SpatialFabric(int width, int height)
	{
		space = new SpatialHashMap<SpatialIndexComponent>("spatial-index", width*height, 1, width, height);
		mapper = ComponentMapper.getFor(SpatialIndexComponent.class);
	}

	@Override
	public void entityAdded(Entity e)
	{
		SpatialIndexComponent sic = mapper.get(e);
		sic.getArea().translate(-space.getWidth()/2, -space.getHeight()/2);
		space.add( sic );
		if( !sic.isStatic )
			dynamicEntities.add(sic.getEntity());
	}

	@Override
	public void entityRemoved(Entity e)
	{
		SpatialIndexComponent sic = mapper.get(e);
		space.remove(sic);
		if( !sic.isStatic )
			dynamicEntities.remove(sic.getEntity());
	}


	@Override
	public void update( final float delta )
	{

		// copy bodies positions into entities:
		for(Entity entity : dynamicEntities)
		{			
			ISpatialComponent spatial = ISpatialComponent.get(entity);
			SpatialIndexComponent index = mapper.get(entity);
			if( spatial.isChanged() )
				space.update( index );
		}

		// update sensors
		for( int idx = 0; idx < sensorEntities.size(); idx++ )
		{
			Entity entity = sensorEntities.get(idx);
			ISpatialComponent spatial = ISpatialComponent.get(entity);
			SensorComponent sensor = SensorComponent.get(entity);
			
			if(sensor.shouldSense() )
			{
				sensorFilter.setSensor(sensor);
				sensor.clear();
				space.queryRadius(sensorFilter, spatial.x(), spatial.y(), sensor.def.getRadius());
			}

		}

		/*
		 * for( int idx = 0; idx < collider.getAOEDamage().size(); idx ++ ) {
		 * DamageComponent damager = collider.getAOEDamage().get( idx );
		 * aoeCollider.setDamager( damager ); Unit damagingUnit = (Unit)
		 * damager; index.queryRadius( aoeCollider,
		 * damagingUnit.getArea().getAnchor().x,
		 * damagingUnit.getArea().getAnchor().y, damager.getDamage().getRadius()
		 * ); aoeCollider.clear(); }
		 */
	}

	
	class PickProvider implements IPickProvider, ISpatialSensor<SpatialIndexComponent>
	{
		private PooledLinkedList<Entity> sensed = new PooledLinkedList<Entity>( Constants.SENSOR_POOL_SIZE );
		
		private float pickRadius = 1;
		private AABB cursor = AABB.createSquare(0, 0, 1, 0);
		
		
		@Setter private IEntityFilter entityFilter;
		

		@Override
		public Entity pick(float x, float y, float pickRadius)
		{
			assert pickRadius > 0 : "Invalid pick radius";
			sensed.clear();
			// preparing querying cursor location and size
			cursor.move(x-space.getWidth()/2, y-space.getHeight()/2);
			if( this.pickRadius != pickRadius )
			{
				cursor.fitTo(pickRadius);
				this.pickRadius = pickRadius;
			}
			space.queryAABB(this, cursor);
			
			if( sensed.size() == 0)
				return null;

			sensed.iter();
			return sensed.next();
		}

		@Override
		public boolean objectFound(SpatialIndexComponent object)
		{
			if( entityFilter == null || entityFilter.accept(object.getEntity()))
			{
				sensed.add(object.getEntity());
				return true;
			}
			return false;
		}

		@Override
		public void clear() { }
		
	}
	
	@Override
	public IPickProvider createPickProvider()
	{
		return new PickProvider();
	}

	@Override
	public void debugDraw(Matrix4 proj)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean hasLineOfSight(float sx, float sy, float tx, float ty, IEntityFilter filter)
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void addedToEngine( Engine engine )
	{
//		KinematicSystem kinematicSystem = engine.getSystem(KinematicSystem.class);
//		if( kinematicSystem == null )
//			log.info("Spatial fabric could not locate kinematic system");
		
		Family spatialfamily = Family.one(SpatialIndexComponent.class).get();
		Family sensorfamily = Family.one(SensorComponent.class).get();

		// engine.addEntityListener(physicalfamily, this);
		// engine.addEntityListener(sensorfamily , this);
		engine.addEntityListener(Family.one(SpatialIndexComponent.class, SensorComponent.class).get(), this);

		//spatialEntities = engine.getEntitiesFor(spatialfamily);
		sensorEntities = engine.getEntitiesFor(sensorfamily);
		
		super.addedToEngine(engine);
	}
	
	@Override
	public void removedFromEngine( Engine engine )
	{
		engine.removeEntityListener(this);
		super.removedFromEngine(engine);
	}
	
	
	static class SensorFilter implements ISpatialSensor<SpatialIndexComponent>
	{
		SensorComponent sensor;
		
		public void setSensor(SensorComponent sensor)
		{
			this.sensor = sensor;
		}
		@Override
		public boolean objectFound(SpatialIndexComponent object)
		{
			sensor.sense(object.getEntity());
			
			return false;
		}

		@Override
		public void clear()
		{
			// TODO Auto-generated method stub
			
		}
	};
	
}
