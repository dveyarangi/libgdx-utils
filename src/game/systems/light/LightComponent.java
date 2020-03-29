package game.systems.light;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.utils.Pool.Poolable;

import box2dLight.ConeLight;
import box2dLight.DirectionalLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import game.config.GraphicOptions;
import game.systems.light.LightDef.Type;

public class LightComponent implements Component, Poolable
{
	Light light;
	
	
	float coneAngle;
	Type type;
	Color color = new Color();
	float distance;
	Filter filter;
	boolean isStatic = true;
	
	@Override
	public void reset()
	{
		light = null;
	}

	public void init( RayHandler rayHandler, GraphicOptions options )
	{
		switch(type)
		{
		case CONE_LIGHT:
			light = new ConeLight(rayHandler, options.lightsFullRay, color, distance, 0, 0, 0, coneAngle);
			break;
		case DIRECTIONAL_LIGHT:
			light = new DirectionalLight(rayHandler, options.lightsFullRay, color, 0);
			break;
		case POINT_LIGHT:
			light = new PointLight(rayHandler, options.lightsFullRay, color, distance, 0, 0);
			break;
		default:
			break;
		
		}
		
		light.setStaticLight(isStatic);
		light.setSoft(false);
		
		light.setSoftnessLength(3);
		light.setContactFilter( filter );
		light.setXray(false);
	}

}
