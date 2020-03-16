package game.systems;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Array;

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
public class EntityDef implements Component
{
	public static ComponentMapper<EntityDef> MAPPER = ComponentMapper.getFor(EntityDef.class);

	public static EntityDef get( final Entity entity )
	{
		return MAPPER.get(entity);
	}

	public boolean isPickable = false;

	/**
	 * If true, this unit is allowed to have children
	 */
	public boolean descendants = false;

	public Array <IComponentDef<?>> genericAspects = new Array <IComponentDef<?>> ();

	public EntityDef()
	{
	}

	//	@Override public int getFactionId() { return faction;}

	public boolean isPickable()
	{
		return isPickable;
	}


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
	 * TODO: optimize!
	 * @param class1
	 * @return
	 */
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


}
