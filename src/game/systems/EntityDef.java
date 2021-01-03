package game.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * This class provides definitions common for entity components, and allows to
 * define addition components, by adding them to defs collection.
 *
 * The definitions object is itself a component, which is always attached to
 * unit by {@link EntityFactory}. Note, however, if the definition object is
 * shared between several entities, make sure all modified defs properties are
 * copied to corresponding components, and not referred.
 *
 * @author Fima
 */
public class EntityDef implements Component, Poolable
{
	public static ComponentMapper<EntityDef> MAPPER = ComponentMapper.getFor(EntityDef.class);

	public static EntityDef get( final Entity entity )
	{
		return MAPPER.get(entity);
	}
	
	public String id;

	/**
	 * If true, this unit is allowed to have children
	 */
	public boolean descendants = false;

	/**
	 * Used to initialize entity with additional components
	 */
	transient public Array <IComponentDef<?>> genericAspects = new Array <IComponentDef<?>> ();

	public EntityDef(String id)
	{
		this.id = id;
	}

	//	@Override public int getFactionId() { return faction;}


	/*	@Override
	public <E extends IMovementComponent> IMovementDef<E> getMovementDef()
	{
		return movementDef;
	}*/


	public Array <IComponentDef<?>> getDefs()
	{
		return genericAspects;
	}

	public boolean hasDescendants()
	{
		return descendants;
	}

	public <E extends IComponentDef<?>> E addDef( final E def )
	{
		genericAspects.add(def);
		return def;
	}

	/**
	 * @param class1
	 * @return Def of specified type
	 */
	@SuppressWarnings("unchecked")
	public <E> E getDef( final Class<E> class1 )
	{
		for( Object cdef : this.getDefs() )
		{
			if( class1.isInstance(cdef) )
			{
				return (E) cdef;
			}
		}
		return null;
	}

	@Override
	public void reset()
	{
		genericAspects.clear();
		descendants = false;
	}


}
