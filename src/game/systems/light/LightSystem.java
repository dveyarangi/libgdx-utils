package game.systems.light;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.physics.box2d.World;

import box2dLight.RayHandler;
import game.config.GraphicOptions;
import game.screen.WorldScreen;
import game.systems.spatial.ISpatialComponent;

/**
 * Handles {@link LightComponent}s, backed by box2dlights.
 * It requires Box2DFabric used by WorldScreen ({@link WorldScreen#createFabric() })
 * 
 * @author Fima
 */
public class LightSystem extends EntitySystem implements EntityListener
{
	
	private GraphicOptions graphicOptions;
	
	protected Camera camera;
	
	/**
	 * Entities with light.
	 */
	private ImmutableArray<Entity> entities;

	protected RayHandler rayHandler;

	
	public void init( World world, Camera camera, LightSystemDef lightSystemDef, GraphicOptions options  )
	{
		//RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(lightSystemDef.useDiffuseLight);
		
		this.graphicOptions = options;

		this.rayHandler = new RayHandler(world, options.lightsFBOWidth, options.lightsFBOHeight);
		this.camera = camera;
		rayHandler.setShadows( true/*lightSystemDef.makeShadows*/ );
		rayHandler.setCulling(true);
		rayHandler.setLightMapRendering(true);
		rayHandler.setBlur(true);
		if( lightSystemDef.ambientLightColor != null )
			rayHandler.setAmbientLight(lightSystemDef.ambientLightColor);
		rayHandler.setBlurNum(options.lightsBlurSize);


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
		light.init( rayHandler, graphicOptions );
	}

	@Override
	public void update( float deltaTime )
	{
		for(int eidx = 0; eidx < entities.size(); eidx ++)
		{
			Entity entity = entities.get( eidx );

			ISpatialComponent spatial = ISpatialComponent.get( entity );
			LightComponent light = entity.getComponent( LightComponent.class );
			//if(spatial.isChanged())
			//{
				light.light.setDirection(spatial.a());
				light.light.setPosition(spatial.x(), spatial.y());
				//light.light.setStaticLight(staticLight);
			//}
		}

		//rayHandler.update();
	}



	@Override
	public void entityRemoved( Entity entity )
	{
		LightComponent light = entity.getComponent(LightComponent.class);
		light.light.remove(true); // TODO: cache!
	}

	@Override
	public void removedFromEngine( Engine engine )
	{
		rayHandler.dispose();
		engine.removeEntityListener( this);
	}


}
