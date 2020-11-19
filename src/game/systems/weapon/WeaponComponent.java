/**
 *
 */
package game.systems.weapon;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

import game.world.environment.resources.Port;
import game.world.environment.resources.Resource;

/**
 * @author dveyarangi
 *
 */
public class WeaponComponent implements Component, Poolable
{

	public WeaponDef def;

	protected float timeToReload;
	protected int bulletsInMagazine;

	protected Vector2 targetOrientation;

	protected boolean isOriented;

	private Port port;

	private float requestedResource;
	private float pendingResourceSupply;

	/**
	 *
	 */
	public WeaponComponent()
	{
		super();

		this.targetOrientation = new Vector2();

		this.port = new Port();
	}

	@Override
	public void reset()
	{
		// TODO: energy port capacity should be externalized

		float maxEnergy = def.getMagazinesNum() *
				def.getBulletsInMagazineNum() *
				def.getBulletEnergyConsumption();

		port.setCapacity(Resource.Type.ENERGY, 0, maxEnergy);

		targetOrientation.set(0, 0);

		bulletsInMagazine = def.getBulletsInMagazineNum();

		timeToReload = 0;
	}

	public boolean isOriented()
	{
		return isOriented;
	}

	public void reload()
	{
		bulletsInMagazine = def.getBulletsInMagazineNum();
	}

	public Vector2 getTargetOrientation()
	{
		return targetOrientation;
	}

	public float getTimeToReload()
	{
		return timeToReload;
	}

	public int getBulletsInMagazine()
	{
		return bulletsInMagazine;
	}

	public Port getPort()
	{
		return port;
	}

	public float getRequiredResource( Resource.Type type )
	{
		float delta = requestedResource - pendingResourceSupply;
		if( delta < 0 )
			return 0;
		return delta;
	}

	public void pendResourceProvision( Resource.Type type, float amount )
	{
		pendingResourceSupply += amount;
	}

	public void provisionResource( Resource.Type type, float amount )
	{
		pendingResourceSupply -= amount;
		if( pendingResourceSupply < 0 )
			pendingResourceSupply = 0;
	}

}