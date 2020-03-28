package game.systems.weapon;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

import game.systems.EntityFactory;
import game.systems.box2d.Box2DPhysicalComponent;
import game.systems.box2d.Box2DPhysicalDef;
import game.systems.spatial.ISpatialComponent;
import game.systems.spatial.ISpatialDef;
import game.systems.targeting.TargetComponent;
import game.util.Angles;
import game.util.RandomUtil;
//import game.world.resources.Resource;

/**
 * Weapon system manages the entity with weapon component. It acquires the
 * target from target component and rotates the weapon towards it
 *
 * @author Fima
 */
public class WeaponSystem extends IteratingSystem
{
	private EntityFactory factory;

	public WeaponSystem()
	{
		super(Family.one(WeaponComponent.class).get());
	}

	public void init( EntityFactory factory )
	{
		this.factory = factory;
	}

	@Override
	protected void processEntity( Entity entity, float deltaTime )
	{
		WeaponComponent weapon = entity.getComponent(WeaponComponent.class);
		TargetComponent target = entity.getComponent(TargetComponent.class);

		weapon.isOriented = false;

		boolean loaded = this.reload(weapon, deltaTime);

		if( target.getTarget() != null )
		{
			ISpatialComponent spatial = ISpatialComponent.get(entity);

			this.adjustOrientation(spatial, weapon, target, deltaTime);
			if( loaded && weapon.isOriented() )
				this.reloadAndFire(spatial, weapon, target, deltaTime);
		}
		else
			this.lookAround(weapon);

	}

	private boolean reload( WeaponComponent weapon, float deltaTime )
	{
		weapon.timeToReload -= deltaTime;

		if( weapon.getBulletsInMagazine() == 0 )
		{
			weapon.reload();
		}

		if( weapon.getTimeToReload() > 0 )
			return false;
		return true;
	}

	Vector2 tmp = new Vector2();

	/**
	 * Predicts position of interception of a target with specified linear velocity and position by a
	 * linearily moving bullet of specified speed
	 *
	 * @param targetDir relative position of target
	 * @param targetVelocity absolute velocity of the target
	 * @param bulletSpeed bullet relative velocity
	 * @return
	 */
	private Vector2 predictOrientation(Vector2 targetPosition, Vector2 targetVelocity, float bulletVelocity )
	{
		double A = targetPosition.x * targetPosition.x + targetPosition.y * targetPosition.y;

		double B = 2 * (targetVelocity.x * targetPosition.x +
				        targetVelocity.y * targetPosition.y);

		double C = targetVelocity.x * targetVelocity.x +
				   targetVelocity.y * targetVelocity.y -
				   bulletVelocity*bulletVelocity;

		double D = B*B - 4*A*C;
		if( D < 0 )
			return null;

		D = Math.sqrt( D );

		float flyTime = (float) ( ( -B + D ) / (2 * C) );
		if(flyTime < 0)
		{
			flyTime = (float) ( ( -B - D ) / (2 * C) );
		}

		if(flyTime < 0)
			return null; // bullet cannot catch the target

		Vector2 predictedDir = targetPosition.add( tmp.set( targetVelocity ).scl( flyTime ) );
		return predictedDir;
	}

	protected void adjustOrientation( ISpatialComponent spatial, WeaponComponent weapon, TargetComponent target, float deltaTime )
	{
		WeaponDef def = weapon.def;
		ISpatialComponent targetSpatial = target.getTarget().getComponent(ISpatialComponent.class);
		Box2DPhysicalComponent targetPhysical = target.getTarget().getComponent(Box2DPhysicalComponent.class);
		// TODO: dismantle this monstrosity:
		float bulletSpeed = weapon.def.getBulletDef().getDef(Box2DPhysicalDef.class).getMaxSpeed();

		weapon.targetOrientation.set(targetSpatial.x() - spatial.x(), targetSpatial.y() - spatial.y());

		this.predictOrientation(weapon.targetOrientation, targetPhysical.getBody().getLinearVelocity(), bulletSpeed );
		//		weapon.targetOrientation.set(targetSpatial.x() - spatial.x(), targetSpatial.y() - spatial.y()).nor();

		weapon.targetOrientation.nor();
		Vector2 weaponDir = spatial.uv();

		float diffAngle =
				(float) ( Math.acos(weapon.targetOrientation.dot(weaponDir)) * Angles.TO_DEG );
		if( Math.abs(diffAngle) < Math.abs(deltaTime * def.getAngularSpeed()) )
		{
			spatial.uv(weapon.targetOrientation.x, weapon.targetOrientation.y);
		}
		else
		{
			// TODO: create spinor utility?
			int dir = weaponDir.crs(weapon.targetOrientation) > 0 ? 1 : -1;

			float angleDelta = dir * deltaTime * def.getAngularSpeed();

			float dx = Angles.COS(angleDelta * Angles.TO_RAD);
			float dy = Angles.SIN(angleDelta * Angles.TO_RAD);

			float x = weaponDir.x * dx - weaponDir.y * dy;
			float y = weaponDir.y * dx + weaponDir.x * dy;

			float abs = (float) Math.sqrt(x * x + y * y);

			spatial.uv(x / abs, y / abs);
		}

		// weaponDir.lerp( targetOrientation, delta *
		// weaponDef.getAngularSpeed() );

		// weaponDir.nor();

		diffAngle = (float) ( Math.acos(weapon.targetOrientation.dot(weaponDir)) * Angles.TO_DEG );

		float absDistance = Math.abs(diffAngle);

		weapon.isOriented = absDistance < weapon.def.getMaxFireAngle();
	}

	public void reloadAndFire( ISpatialComponent source, WeaponComponent weapon, TargetComponent target, float deltaTime )
	{

		// Resource energyStock = weapon.port.get(Resource.Type.ENERGY);
		// if(energyStock.getAmount() < weapon.def.getBulletEnergyConsumption())
		// return ;

		float angle = weapon.def.getAngleDistribution(source.a());

		//	float vx = weapon.def.getBulletDef().maxSpeed * (float) Math.cos(angle * Angles.TO_RAD);
		//	float vy = weapon.def.getBulletDef().maxSpeed * (float) Math.sin(angle * Angles.TO_RAD);
		//	weapon.def.getBulletDef().getMovementDef().setInitialState(source.x(), source.y(), angle, vx, vy);

		ISpatialDef spatialDef = weapon.def.getBulletDef().getDef(ISpatialDef.class);
		spatialDef.x(source.x());
		spatialDef.y(source.y());
		spatialDef.a(source.a());
		factory.addEntity(weapon.def.getBulletDef());
		// weapon.port.use(Resource.Type.ENERGY,
		// weapon.def.getBulletEnergyConsumption());

		// update weapon reloading sequence:
		weapon.bulletsInMagazine--;
		if( weapon.bulletsInMagazine > 0 )
		{
			weapon.timeToReload = weapon.def.getReloadingTime();
		}
		else
		{
			weapon.timeToReload = weapon.def.getMagazineReloadTime();
		}

	}

	private void lookAround( WeaponComponent weapon )
	{
		// no target, some meaningless behavior:
		if( RandomUtil.oneOf(200) )
		{
			float wanderAngle = RandomUtil.getRandomFloat(360);
			weapon.targetOrientation.setAngle(wanderAngle);
		}
	}

}
