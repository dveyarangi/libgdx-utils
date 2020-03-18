package game.systems.lifecycle;

import com.badlogic.ashley.core.Entity;

import game.systems.IComponentDef;
import game.world.Level;
import lombok.NoArgsConstructor;

/**
 * Holds definitions for entity type and life length.
 * @author Fima
 *
 */
@NoArgsConstructor
public class LifecycleDef implements IComponentDef <LifecycleComponent>
{

	public int type;
	/**
	 * Total life duration.
	 */
	public float lifeDuration = Float.POSITIVE_INFINITY;

	public LifecycleDef( float lifeDuration )
	{
		this.lifeDuration = lifeDuration;
	}
	public LifecycleDef( float lifeDuration, int type  )
	{
		this.lifeDuration = lifeDuration;
		this.type = type;
	}

	public int getType() { return type; }

	public float getLiveLength()
	{
		return lifeDuration;
	}

	@Override
	public Class<LifecycleComponent> getComponentClass() { return LifecycleComponent.class; }

	@Override
	public void initComponent( LifecycleComponent component, Entity entity, Level level )
	{
		component.lifelen = lifeDuration;
		component.type = type;
	}

}
