package game.systems.light;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;

import box2dLight.RayHandler;
import game.config.GraphicOptions;
import game.systems.spatial.ISpatialComponent;
import game.systems.spatial.SpatialComponent;

public class LightSystem extends EntitySystem implements EntityListener
{

	private OrthographicCamera camera;
	/**
	 * Entities with light.
	 */
	private ImmutableArray<Entity> entities;

	RayHandler rayHandler;
	
	public void init( World world, OrthographicCamera camera, LightSystemDef lightSystemDef  )
	{
		RayHandler.setGammaCorrection(true);
		//RayHandler.useDiffuseLight(true);
		
		

		this.rayHandler = new RayHandler(world, GraphicOptions.LIGHTS_FBO_WIDTH, GraphicOptions.LIGHTS_FBO_HEIGHT);
		this.camera = camera;
		rayHandler.setShadows( true/*lightSystemDef.makeShadows*/ );
		rayHandler.setCulling(true);
		rayHandler.setLightMapRendering(true);
		rayHandler.setBlur(true);
		rayHandler.setAmbientLight(0.2f, 0.2f, 0.2f, 0.7f);
		rayHandler.setBlurNum(GraphicOptions.LIGHTS_BLUR_SIZE);


	}
	@Override
	public void addedToEngine( Engine engine )
	{
		Family family = Family.one(LightComponent.class).get();

		entities = engine.getEntitiesFor(family);
		engine.addEntityListener(family, 0, this);
	}

	@Override
	public void entityAdded( Entity entity )
	{
		LightComponent light = entity.getComponent(LightComponent.class);
		light.init( rayHandler );
	}

	@Override
	public void update( float deltaTime )
	{
		for(int eidx = 0; eidx < entities.size(); eidx ++)
		{
			Entity entity = entities.get( eidx );

			ISpatialComponent spatial = SpatialComponent.get( entity );
			LightComponent light = entity.getComponent( LightComponent.class );
			light.light.setDirection(spatial.a());
			light.light.setPosition(spatial.x(), spatial.y());
		}

		rayHandler.setCombinedMatrix( camera );
		rayHandler.update();
	}



	@Override
	public void entityRemoved( Entity entity )
	{
		LightComponent light = entity.getComponent(LightComponent.class);
		light.light.dispose();
	}

	@Override
	public void removedFromEngine( Engine engine )
	{
		rayHandler.dispose();
		engine.removeEntityListener( this);
	}


}
