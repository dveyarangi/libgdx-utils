package game.systems.lifecycle;

import com.badlogic.ashley.core.Entity;

import game.systems.IComponentDef;
import game.world.Level;
import game.world.saves.EntityProps;

/**
 * Holds definitions for entity type and life length.
 * @author Fima
 *
 */
public class LifecycleDef implements IComponentDef <LifecycleComponent>
{

	public static final String PROP_ID = "id";
	public static final String PROP_TYPE = "type";
	public static final String PROP_LIFELEN = "lifelen";
	public static final String PROP_LIFETIME = "lifetime";
	public static final float DEFAULT_LIFELEN = Float.POSITIVE_INFINITY;

	public EntityProps initProps(EntityProps props, String type, long lifelen)
	{
		return LifecycleComponent.save(props, createId(type), type, lifelen, 0);
	}

	public int id;
	public String type;
	/**
	 * Total life duration.
	 */
	public float lifeDuration = DEFAULT_LIFELEN;

	public LifecycleDef(String type)
	{
		this(createId(type), DEFAULT_LIFELEN, type);
	}

	public LifecycleDef( String type, float lifeDuration )
	{
		this(createId(type), lifeDuration, type);
	}

	public LifecycleDef( int id, float lifeDuration, String type  )
	{
		this.id = id;
		this.lifeDuration = lifeDuration;
		this.type = type;
	}

	public String getType() { return type; }

	public float getLiveLength()
	{
		return lifeDuration;
	}

	@Override
	public Class<LifecycleComponent> getComponentClass() { return LifecycleComponent.class; }

	@Override
	public void initComponent( LifecycleComponent component, Entity entity, Level level )
	{
		component.id = id;
		component.lifelen = lifeDuration;
		component.type = type;
	}

	@Override
	public void initComponent( LifecycleComponent component, EntityProps props, Entity entity, Level level )
	{
		component.id = id;
		component.lifelen = lifeDuration;
		component.type = type;
	}
	//////////////////////
	// ID generation

	static int IDGEN = 1;
	public static int createId(String type)
	{
		return IDGEN ++;
	}

	/** When loading entities, update IDGEN so that new entities won't get repeating ids */
	static void adjustIdGen(int existingId)
	{
		if(existingId > IDGEN)
			IDGEN = existingId + 1;
	}


}
