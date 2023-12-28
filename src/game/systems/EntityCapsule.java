package game.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * Used to store entity outside the entity engine;
 * uses entity id to validate that this entity instance is not reused since
 * it was provided.
 *
 * @author Fima
 */
public class EntityCapsule implements Poolable
{
	static Pool<EntityCapsule> pool = new Pool<> () {
		@Override
		protected EntityCapsule newObject() { return new EntityCapsule(); }
	};
	
	public static EntityCapsule get(Entity entity)
	{
		var capsule = pool.obtain();
		capsule.id = entity.flags;
		capsule.entity = entity;
		return capsule;
	}
	public static EntityCapsule get()
	{
		return pool.obtain();
	}	
	
	private int id;

	private Entity entity;

	public void set(Entity entity)
	{
		if(entity != null)
			this.id = entity.flags;
		this.entity = entity;

	}

	public Entity entity()
	{
		if( entity == null)
			return null;

		if(!isValid())
			reset();

		return entity;
	}

	public int id() { return id; }

	@Override
	public void reset()
	{
		entity = null;
		id = EntityId.INVALID_ID;
	}
	
	public void free() { pool.free(this); }

	public boolean isValid()
	{
		return entity.flags == id;
	}
}
