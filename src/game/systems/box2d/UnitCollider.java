/**
 *
 */
package game.systems.box2d;

import game.systems.damage.DamageComponent;
import game.systems.damage.IHullComponent;
import game.systems.faction.FactionComponent;
import game.systems.lifecycle.LifecycleComponent;
import game.systems.sensor.SensorComponent;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * @author dveyarangi
 *
 */
public class UnitCollider implements ContactListener
{

	private List<DamageComponent> aoeDamages = new ArrayList<DamageComponent>();

	@Override
	public void beginContact( Contact contact )
	{
		Entity unitA = (Entity) contact.getFixtureA().getUserData();
		Entity unitB = (Entity) contact.getFixtureB().getUserData();
		/*if( unitA == null || unitB == null)
		{
			contact.setEnabled( false);
			return;
		}*/

		if( contact.getFixtureA().isSensor() || contact.getFixtureB().isSensor() )
		{
			if( contact.getFixtureA().isSensor() && contact.getFixtureB().isSensor() )
			{
				contact.setEnabled(false);
				return;
			}
			SensorComponent sensor;
			Entity sensedEntity;
			if( contact.getFixtureA().isSensor() )
			{
				sensor = SensorComponent.get(unitA);
				sensedEntity = unitB;
			}
			else
			{
				sensor = SensorComponent.get(unitB);
				sensedEntity = unitA;
			}

			sensor.sense(sensedEntity);

			// contact.setEnabled( false );
			return;
		}

		this.damage(unitA, unitB);
		this.damage(unitB, unitA);
	}

	private boolean damage( Entity source, Entity target )
	{

		DamageComponent damager = DamageComponent.get(source);
		if( damager == null ) // ignore hurtless beings
			return false;

		IHullComponent hurter = IHullComponent.get(target);
		if( hurter == null ) // ignore transcendental beings
			return false;

		boolean sameFaction = FactionComponent.get(source).id() == FactionComponent.get(target).id();
		boolean damageDealt = false;

		if( !sameFaction || damager.dealsFriendlyDamage() )
		{
			if( damager.getDamage().getAOE() != null )
			{
				aoeDamages.add(damager);
			}
			else
			{
				boolean isDead = hurter.hit(damager.getDamage(), source, 1);

				if( damager.dieOnHit() )
				{
					LifecycleComponent life = LifecycleComponent.get(source);
					life.setDead();

				}
				if( isDead )
				{
					LifecycleComponent life = LifecycleComponent.get(target);
					life.setDead();
				}
				damageDealt = true;
			}
		}

		return damageDealt;
	}

	@Override
	public void endContact( Contact contact )
	{
		Entity unitA = (Entity) contact.getFixtureA().getUserData();
		Entity unitB = (Entity) contact.getFixtureB().getUserData();
		/*if( unitA == null || unitB == null)
		{
			contact.setEnabled( false);
			return;
		}*/

		if( contact.getFixtureA().isSensor() || contact.getFixtureB().isSensor() )
		{

			SensorComponent sensor;
			Entity sensedEntity;
			if( contact.getFixtureA().isSensor() )
			{
				sensor = SensorComponent.get(unitA);
				sensedEntity = unitB;
			}
			else
			{
				sensor = SensorComponent.get(unitB);
				sensedEntity = unitA;
			}

			sensor.unsense(sensedEntity);

			// contact.setEnabled( false );
			return;
		}
	}

	@Override
	public void preSolve( Contact contact, Manifold oldManifold )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void postSolve( Contact contact, ContactImpulse impulse )
	{
		// TODO Auto-generated method stub

	}

	public void clear()
	{
		aoeDamages.clear();
	}

	public List<DamageComponent> getAOEDamage()
	{
		return aoeDamages;
	}

}
