package game.systems.light;

import game.systems.IComponentDef;
import game.world.Level;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Filter;

public class LightDef implements IComponentDef<LightComponent>
{
	public static final int POINT_LIGHT = 1;
	public float coneAngle ;
	public int type;
	public Color color;
	public float distance;
	public Filter filter;
	@Override
	public Class<LightComponent> getComponentClass() { return LightComponent.class; }
	@Override
	public void initComponent( LightComponent component, Entity entity, Level level )
	{
		component.def = this;
	}
}
