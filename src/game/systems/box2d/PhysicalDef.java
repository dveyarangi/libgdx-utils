package game.systems.box2d;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

import game.systems.EntityDef;
import game.systems.movement.IMovementDef;
import game.systems.spatial.ISpatialComponent;
import game.systems.spatial.SpatialComponent;
import game.util.Angles;
import game.world.Level;
import lombok.NoArgsConstructor;

public class PhysicalDef implements IMovementDef<PhysicalComponent>
{

	// setting body shape to axis aligned square
	
	public static PhysicalDef createStatic(PartDef part, short categoryBits)
	{
		PhysicalDef physical = new PhysicalDef(0);
		part.bodyDef.bullet = false;
		part.bodyDef.type = BodyDef.BodyType.StaticBody;
		part.fixtureDef.filter.categoryBits = categoryBits;
		part.fixtureDef.density = 1;
		physical.parts.add( part );		

		return physical;
	}

	@NoArgsConstructor
	public static class PartDef
	{
		/**
		 * Body shape polygon
		 */
		Vector2[] aabbarr = { new Vector2(), new Vector2(), new Vector2(), new Vector2() };
		public BodyDef bodyDef = new BodyDef();

		public Polygon shape;

		public FixtureDef fixtureDef = new FixtureDef();
		
		
		public static PartDef createSquare(float d)
		{
			PartDef def = new PartDef( );
			def.aabbarr[0].set(-d/2, -d/2);
			def.aabbarr[1].set(-d/2, +d/2);
			def.aabbarr[2].set(+d/2, +d/2);
			def.aabbarr[3].set(+d/2, -d/2);
			float [] points = new float [8];
			points[0] = def.aabbarr[0].x;points[1] = def.aabbarr[0].y;
			points[2] = def.aabbarr[1].x;points[3] = def.aabbarr[1].y;
			points[4] = def.aabbarr[2].x;points[5] = def.aabbarr[2].y;
			points[6] = def.aabbarr[3].x;points[7] = def.aabbarr[3].y;

			def.shape = new Polygon( points );
			PolygonShape poly = new PolygonShape();
			poly.set( def.aabbarr );
			def.fixtureDef.shape = poly;
			
			return def;
		}

		public PartDef(float r)
		{
			aabbarr[0].set(-r, -r);
			aabbarr[1].set(-r, +r);
			aabbarr[2].set(+r, +r);
			aabbarr[3].set(+r, -r);
			float [] points = new float [8];
			points[0] = aabbarr[0].x;points[1] = aabbarr[0].y;
			points[2] = aabbarr[1].x;points[3] = aabbarr[1].y;
			points[4] = aabbarr[2].x;points[5] = aabbarr[2].y;
			points[6] = aabbarr[3].x;points[7] = aabbarr[3].y;

			shape = new Polygon( points );
			PolygonShape poly = new PolygonShape();
			poly.set( aabbarr );
			fixtureDef.shape = poly;

		}

		public PartDef( float[] vertices )
		{
			this.shape = new Polygon( vertices );
			PolygonShape poly = new PolygonShape();
			poly.set( vertices );
			fixtureDef.shape = poly;
		}

		public BodyDef getBodyDef() { return bodyDef; }

		public FixtureDef getFixtureDef() { return fixtureDef; }
	}

	public Array <PartDef> parts;

	private float maxSpeed;

	public PhysicalDef(float maxSpeed )
	{
		this.parts = new Array <PartDef> (1);
		this.maxSpeed = maxSpeed;
	}

	@Override
	public Class<PhysicalComponent> getComponentClass() { return PhysicalComponent.class; }

	@Override
	public void initComponent( PhysicalComponent component, Entity entity, Level level )
	{
		component.def = this;
		component.maxSpeed = maxSpeed;
		ISpatialComponent spatial = SpatialComponent.get( entity );
		EntityDef def = EntityDef.get( entity );
		float dx = (float) Math.cos(spatial.a() * Angles.TO_RAD), dy = (float) Math.sin(spatial.a() * Angles.TO_RAD);

		for(int didx = 0; didx < parts.size; didx ++)
		{
			PartDef part = parts.get(didx);
			part.bodyDef.position.set(spatial.x(), spatial.y());
			part.bodyDef.angle = spatial.a() * Angles.TO_RAD;
			part.bodyDef.linearVelocity.set(maxSpeed*dx, maxSpeed*dy);

		}
	}


	public Array <PartDef> getParts() { return parts; }

	@Override
	public float getMaxSpeed() { return maxSpeed; }

}
