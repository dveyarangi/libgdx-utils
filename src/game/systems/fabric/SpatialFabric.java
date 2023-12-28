package game.systems.fabric;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.ai.msg.PriorityQueue;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;

import game.systems.control.IEntityFilter;
import game.systems.control.PickComponent;
import game.systems.sensor.SensorCategory;
import game.systems.sensor.SensorComponent;
import game.systems.spatial.ISpatialComponent;
import game.world.IFabric;
import game.world.IScoopula;
import lombok.Getter;
import lombok.Setter;
import yarangi.spatial.AABB;
import yarangi.spatial.ISpatialSensor;
import yarangi.spatial.SpatialHashMap;

/**
 * Entity system that provides means for spatial interactions.
 * - Monitors updates of entities with {@link SpatialIndexComponent}
 * - Manages entity sensor components, feeding them with surroundings data
 * - Manages {@link IScoopula} that provide entity picking for InputProcessor
 */
public class SpatialFabric extends EntitySystem implements IFabric, EntityListener
{
	// spatial 
	public SpatialHashMap <SpatialIndexComponent> space;

	// entities with varying spatial props
	private ObjectSet<Entity> dynamicEntities = new ObjectSet <>();

	// entities with sensors
	private ImmutableArray<Entity> sensorEntities;


	private SensorFilter sensorFilter = new SensorFilter();

		


	public SpatialFabric(int width, int height)
	{
		space = new SpatialHashMap<>("spatial-index", width*height, 1, width, height);

	}

	@Override
	public void entityAdded(Entity e)
	{
		var sic = SpatialIndexComponent.get(e);
		sic.getArea().translate(-space.getWidth()/2, -space.getHeight()/2);
		space.add( sic );
		if( !sic.isStatic )
			dynamicEntities.add(sic.getEntity());
	}

	@Override
	public void entityRemoved(Entity e)
	{
		var sic = SpatialIndexComponent.get(e);
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
			var spatial = ISpatialComponent.get(entity);
			var index = SpatialIndexComponent.get(entity);
			if( spatial.isChanged() )
			{
				index.update();
				// TODO: 
				index.getArea().translate(-space.getWidth()/2, -space.getHeight()/2);
				space.update( index, null );
			}
		}

		// update sensors
		for( int idx = 0; idx < sensorEntities.size(); idx++ )
		{
			Entity entity = sensorEntities.get(idx);
			var spatial = ISpatialComponent.get(entity);
			var sensor = SensorComponent.get(entity);

			if( sensor.shouldSense() )
			{
				sensorFilter.setSensor(sensor);
				sensor.clear();
				space.queryRadius(sensorFilter, spatial.x()-space.getWidth()/2, spatial.y()-space.getHeight()/2, sensor.radius);
				sensor.timeSinceSensing = 0;
			}
			else
				sensor.timeSinceSensing += delta;
			


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


	class PickProvider implements IScoopula, ISpatialSensor<SpatialIndexComponent>
	{
		private PriorityQueue<PickComponent> sensed = new PriorityQueue<>();

		private float pickRadius = 1;
		private AABB cursor = AABB.createSquare(0, 0, 1, 0);
		@Getter private CategorySet categories = new CategorySet(new int[]{0});

		@Setter private IEntityFilter entityFilter;


		@Override
		public Entity pick(float x, float y, float pickRadius)
		{
			assert pickRadius > 0 : "Invalid pick radius";
			//if( x >= 118 && x < 119 && y >= 105&& y < 106)
			//	System.out.println("test");
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

			return sensed.peek().getEntity();
		}

		@Override
		public boolean objectFound(SpatialIndexComponent object)
		{
			if( entityFilter == null || entityFilter.accept(object.getEntity()))
			{
				sensed.add(object.getEntity().getComponent(PickComponent.class));
				return false;
			}
			return false;
		}

		@Override
		public void clear() { }
	}

	@Override
	public IScoopula createPickProvider()
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
		Family sensorfamily = Family.one(SensorComponent.class).get();

		// listening to:
		// entities with spatial component (taking space)
		// entities with sensors
		engine.addEntityListener(Family.one(SpatialIndexComponent.class, SensorComponent.class).get(), this);

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
		}
		@Override
		public CategorySet getCategories() { return sensor.getCategories(); }
	}

	ObjectMap<SensorCategory[], CategorySet> categorySets = new ObjectMap <> ();
 	
	public CategorySet getCategorySet(SensorCategory ... categories)
	{
		CategorySet set = categorySets.get(categories);
		if(set == null)
			categorySets.put(categories, set = new CategorySet(categories));
		return set;
	};

}
