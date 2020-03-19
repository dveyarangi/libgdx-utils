package game.systems.light;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Filter;

import game.systems.IComponentDef;
import game.world.Level;

public class LightDef implements IComponentDef<LightComponent>
{
	
	public static enum Type {
		POINT_LIGHT,
		DIRECTIONAL_LIGHT,
		CONE_LIGHT;
	}
	

	public float coneAngle ;
	public Type type;
	public Color color;
	public float distance;
	public Filter filter;
	public boolean isStatic = true;
	@Override
	public Class<LightComponent> getComponentClass() { return LightComponent.class; }
	@Override
	public void initComponent( LightComponent component, Entity entity, Level level )
	{
		component.def = this;
	}
}
