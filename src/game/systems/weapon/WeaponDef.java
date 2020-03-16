package game.systems.weapon;

import com.badlogic.ashley.core.Entity;

import game.systems.IComponentDef;
import game.systems.EntityDef;
import game.world.Level;

public abstract class WeaponDef implements IComponentDef<WeaponComponent>
{
	private float projectileInitialSpeed;

	public EntityDef bulletDef;

	@Override
	public Class<WeaponComponent> getComponentClass() { return WeaponComponent.class; }

	@Override
	public void initComponent( WeaponComponent component, Entity entity, Level level )
	{
		component.def = this;
	}

	/**
	 * @return the burstSize
	 */
	public abstract int getBulletsInMagazineNum();

	/**
	 * Number of magazines
	 *
	 * @return
	 */
	public abstract float getMagazinesNum();

	/**
	 *
	 * @return
	 */
	public abstract float getBulletEnergyConsumption();

	/**
	 * @return the magazineReloadTime
	 */
	public abstract float getMagazineReloadTime();

	/**
	 * @return the reloadingTime
	 */
	public abstract float getReloadingTime();

	/**
	 * @return the accuracy
	 */
	public abstract float getDispersion();

	/**
	 * Canon rotation
	 *
	 * @return
	 */
	public abstract float getAngularSpeed();

	/**
	 * Firing angles distribution
	 *
	 * @param a
	 * @return
	 */
	public abstract float getAngleDistribution( float a );

	/**
	 * Max angular offset from target orientation when weapon is allowed to fire
	 *
	 * @return
	 */
	public abstract float getMaxFireAngle();

	public EntityDef getBulletDef()
	{
		return bulletDef;
	}

	public abstract boolean shouldDieOnCollision();
}
