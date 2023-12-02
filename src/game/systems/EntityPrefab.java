package game.systems;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;

import game.world.saves.Props;
import lombok.Getter;
import lombok.Setter;

/**
 * This class provides definitions common for entity components, and allows to
 * define addition components, by adding them to defs collection.
 *
 * @author Fima
 */
public class EntityPrefab implements/*Component,*/ Poolable
{
	/*public static ComponentMapper<EntityDef> MAPPER = ComponentMapper.getFor(EntityDef.class);

	public static EntityDef get( final Entity entity )
	{
		return MAPPER.get(entity);
	}*/

	/**
	 * If true, this unit is allowed to have children
	 */
	public boolean descendants = false;

	@Getter @Setter private Props props;

	/**
	 * Used to initialize entity with additional components
	 */
	transient public Array <IComponentDef<?>> genericAspects = new Array <> ();

	public EntityPrefab()
	{
		this(Props.get());
	}

	public EntityPrefab(Props props)
	{
		this.props = props;
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
		for( Object cdef : getDefs() )
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


	//	public int id() { return getDef(LifecycleDef.class).id; }

}
