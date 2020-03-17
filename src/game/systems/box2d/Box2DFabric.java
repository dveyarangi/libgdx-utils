package game.systems.box2d;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

import game.systems.box2d.PhysicalDef.PartDef;
import game.systems.sensor.SensorComponent;
import game.systems.sensor.SensorDef;
import game.systems.spatial.ISpatialComponent;
import game.util.Angles;
import game.world.IFabric;
import game.world.IPickProvider;
import game.world.environment.nav.NavMesh;
import game.world.environment.nav.NavNode;

/**
 * Manages entity interaction.
 *
 * Applies to entities with {@link PhysicalComponent} and {@link SensorComponent}.
 *
 * @author Fima
 *
 */
public class Box2DFabric extends EntitySystem implements IFabric, EntityListener
{

	public static final float NAV_NODE_SENSOR_RADIUS = 1;

	private static final short MESH_NODES_CATEGORY = 1;

	private static final short MESH_CLIENTS_CATEGORY = 1;

	/**
	 * Physical world, should be moved into Environment
	 */
	private final World world;

	private float accumulator;
	private float step = 1.0f / 60.0f;

	static
	{
		World.setVelocityThreshold(1000.0f);
	}


	/**
	 * Unit collision and sensor processer
	 */
	private UnitCollider collider;

	private ImmutableArray<Entity> physicalEntities;
	private ImmutableArray<Entity> sensorEntities;

	private Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();


	public Box2DFabric()
	{
		this.world = new World(new Vector2(0, 0), true);

		world.setAutoClearForces(true);

		this.collider = new UnitCollider();

		world.setContactListener(collider);

		debugRenderer.setDrawBodies(true);
		debugRenderer.setDrawAABBs(false);
		debugRenderer.setDrawJoints(true);
	}

	/**
	 * Initializes
	 */
	@Override
	public void addedToEngine( Engine engine )
	{
		Family physicalfamily = Family.one(PhysicalComponent.class).get();
		Family sensorfamily = Family.one(SensorComponent.class).get();

		// engine.addEntityListener(physicalfamily, this);
		// engine.addEntityListener(sensorfamily , this);
		engine.addEntityListener(Family.one(PhysicalComponent.class, SensorComponent.class).get(), this);

		physicalEntities = engine.getEntitiesFor(physicalfamily);
		sensorEntities = engine.getEntitiesFor(sensorfamily);
	}

	/**
	 * This method creates box2d sensors tied to every nav node. The
	 * accompanying ContactListener logic passes the nav node in proximity
	 * information to passing units.
	 *
	 * TODO: this may be redundant, maybe a query at movement start is
	 * sufficiently
	 *
	 * @param navMesh
	 * @param categoryBits
	 * @param maskBits
	 */
	private void indexNavMesh( NavMesh<NavNode> navMesh, short categoryBits, short maskBits )
	{
		// static body:
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;

		// creating sensor circle,
		// units in this radius will receive notification
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = new CircleShape();
		fixtureDef.shape.setRadius(NAV_NODE_SENSOR_RADIUS);
		fixtureDef.isSensor = true;

		fixtureDef.filter.categoryBits = categoryBits;
		fixtureDef.filter.maskBits = maskBits;

		// adding nodes to physics engine:
		for( int idx = 0; idx < navMesh.getNodesNum(); idx++ )
		{
			NavNode node = navMesh.getNode(idx);

			bodyDef.position.set(node.getPoint().x, node.getPoint().y);

			Body body = world.createBody(bodyDef);
			body.setUserData(node);

			Fixture fixture = body.createFixture(fixtureDef);
			fixture.setUserData(node);
		}
	}

	BodyDef sensorBodyDef = new BodyDef();
	FixtureDef sensorFixtureDef = new FixtureDef();
	Shape sensorShape = new CircleShape();

	@Override
	public void entityAdded( Entity entity )
	{
		this.initBodyComponent(entity);

		this.initSensorComponent(entity);

	}

	private void initBodyComponent( Entity entity )
	{
		PhysicalComponent physical = PhysicalComponent.get(entity);
		if( physical != null && physical.body == null )
		{

			PhysicalDef phydef = physical.def;
			for(int pi = 0; pi < phydef.parts.size; pi ++)
			{
				PartDef part = phydef.parts.get(pi);
				BodyDef bodyDef = part.getBodyDef();

				Body body = world.createBody(bodyDef);

				FixtureDef fixtureDef = part.getFixtureDef();
				Fixture fixture = body.createFixture(fixtureDef);
				// interweave entity and its body:
				body.setUserData(entity);
				physical.body = body;
				fixture.setUserData(entity);
			}
		}
	}

	private void initSensorComponent( Entity entity )
	{
		SensorComponent sensor = SensorComponent.get(entity);
		if( sensor != null && sensor.body == null )
		{
			SensorDef sensorDef = sensor.def;
			BodyDef bodyDef = sensorDef.getBodyDef();

			Body body = world.createBody(bodyDef);
			body.setUserData(entity);
			sensor.body = body;

			FixtureDef fixtureDef = sensorDef.getFixtureDef();
			Fixture fixture = body.createFixture(fixtureDef);
			fixture.setUserData(entity);
		}
	}

	@Override
	public void update( final float delta )
	{
		// make physics steps:
		this.processPhysicsWorld(delta);

		// copy bodies positions into entities:
		for( int idx = 0; idx < physicalEntities.size(); idx++ )
		{
			Entity entity = physicalEntities.get(idx);
			ISpatialComponent spatial = ISpatialComponent.get(entity);
			PhysicalComponent physical = PhysicalComponent.get(entity);
			Vector2 pos = physical.getBody().getPosition();
			if(!Float.isNaN(pos.x)) // TODO: Box2D investigate this, caused jni failures in past
			{
				spatial.x(pos.x);
				spatial.y(pos.y);
				spatial.a(physical.getBody().getAngle() * Angles.TO_DEG);
			}
			else
			{
				System.out.println("Box2DFabric: Invalid body position");
			}
		}

		// adjust sensor positions from carrying entities
		for( int idx = 0; idx < sensorEntities.size(); idx++ )
		{
			Entity entity = sensorEntities.get(idx);
			ISpatialComponent spatial = ISpatialComponent.get(entity);
			SensorComponent sensor = SensorComponent.get(entity);

			sensor.body.setTransform(spatial.x(), spatial.y(), spatial.a() * Angles.TO_RAD);
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

	private static class FilteredRayCastCallback implements RayCastCallback
	{
		IEntityFilter filter;
		boolean hit;

		public void reset(IEntityFilter filter)
		{
			this.hit = false;
			this.filter = filter;
		}
		@Override
		public float reportRayFixture( Fixture fixture, Vector2 point, Vector2 normal, float fraction )
		{
			hit = filter.accept( (Entity) fixture.getUserData());
			return hit ? 0 : -1;
		}

	};

	FilteredRayCastCallback callback = new FilteredRayCastCallback();
	Vector2 point1 = new Vector2();
	Vector2 point2 = new Vector2();
	@Override
	public boolean hasLineOfSight(float x1, float y1, float x2, float y2, IEntityFilter filter)
	{
		assert !Float.isNaN(x1) && (x1 != x2 || y1 != y2);
		callback.reset( filter );

		world.rayCast(callback, point1.set(x1,y1), point2.set(x2, y2));

		return !callback.hit;
	}

	private void processPhysicsWorld( float delta )
	{
		collider.clear();
		accumulator += delta;// * (slomo ? 0.1f : 1f);

		// modifying time for physics step:
		float modifiedStep = step;
		// System.out.println(modifiedStep);
		int stepsTaken = 0;

		// float physicsTime = 0;
		do
		{
			world.step(modifiedStep, 3, 3);
			accumulator -= modifiedStep;
			// stepsTaken ++;
			// physicsTime += modifiedStep;
		} while( accumulator >= modifiedStep );
		// if(stepsTaken > 1) System.out.println(stepsTaken);
	}

	@Override
	public void entityRemoved( Entity entity )
	{
		PhysicalComponent body = PhysicalComponent.get(entity);
		if( body != null )
			world.destroyBody(body.getBody());

		SensorComponent sensor = SensorComponent.get(entity);
		if( sensor != null )
			world.destroyBody(sensor.body);
	}

	@Override
	public void removedFromEngine( Engine engine )
	{
		engine.removeEntityListener(this);
	}

	@Override
	public IPickProvider createPickProvider()
	{
		return new Box2DPicker(world);
	}

	@Override
	public void debugDraw(Matrix4 proj)
	{
		debugRenderer.render(world, proj);
	}

	public World getWorld() { return world; }



}
