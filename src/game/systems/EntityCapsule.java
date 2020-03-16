package game.systems;

import com.badlogic.ashley.core.Entity;

/**
 * Used to store entity outside the entity engine;
 * uses entity id to validate that this entity instance is not reused since
 * it was provided.
 *
 * @author Fima
 */
public class EntityCapsule
{
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

		if(entity.flags != id)
		{
			entity = null;
			id = EntityId.INVALID_ID;
		}

		return entity;
	}

	public int id() { return id; }
}
