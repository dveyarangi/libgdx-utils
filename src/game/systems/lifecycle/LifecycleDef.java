package game.systems.lifecycle;

import com.badlogic.ashley.core.Entity;

import game.systems.IComponentDef;
import game.world.Level;

/**
 * Holds definitions for entity type and life length.
 * @author Fima
 *
 */
public class LifecycleDef implements IComponentDef <LifecycleComponent>
{

	public static final String PROP_ID = "id";
	public static final String PROP_TYPE = "type";
	public static final String PROP_PATH = "path";
	public static final String PROP_LIFELEN = "lifelen";
	public static final String PROP_LIFETIME = "lifetime";
	public static final float DEFAULT_LIFELEN = Float.POSITIVE_INFINITY;

	//public int id;

	public String type;

	public String path;
	/**
	 * Total life duration.
	 */
	public float lifelen = DEFAULT_LIFELEN;

	public LifecycleDef( String type )
	{
		this( type, null, DEFAULT_LIFELEN);
	}

	public LifecycleDef( String type, String path )
	{
		this( type, path, DEFAULT_LIFELEN);
	}

	public LifecycleDef( String type, String path, float lifelen )
	{
		this.type = type;
		this.path = path;
		this.lifelen = lifelen;
	}

	public String getType() { return type; }

	public float getLiveLength()
	{
		return lifelen;
	}

	@Override
	public Class<LifecycleComponent> getComponentClass() { return LifecycleComponent.class; }

	@Deprecated
	@Override
	public void initComponent( LifecycleComponent component, Entity entity, Level level )
	{
	}

}
