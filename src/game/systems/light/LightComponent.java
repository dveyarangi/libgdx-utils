package game.systems.light;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool.Poolable;

import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.RayHandler;
import game.config.GraphicOptions;

public class LightComponent implements Component, Poolable
{
	LightDef def;
	Light light;
	@Override
	public void reset()
	{
		// TODO Auto-generated method stub

	}

	public void init( RayHandler rayHandler )
	{
		
		//light = new DirectionalLight(rayHandler, GraphicOptions.LIGHTS_FULL_RAY, def.color, 0);		
		//light = new PointLight(rayHandler, GraphicOptions.LIGHTS_FULL_RAY, def.color, def.distance, 0, 0);
		light = new ConeLight(rayHandler, GraphicOptions.LIGHTS_FULL_RAY, def.color, def.distance, 0, 0, 0, def.coneAngle);
		light.setStaticLight(false);
		light.setSoft(true);
		
		light.setSoftnessLength(3);
		light.setContactFilter( def.filter );
		light.setXray(false);
	}

}
