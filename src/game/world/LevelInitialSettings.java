package game.world;

import com.badlogic.gdx.math.Vector2;

import game.util.RandomSeed;
import lombok.Getter;

/**
 * Defines initial settings for a level.
 * 
 * @author Fima
 */
public class LevelInitialSettings
{

	public enum CameraMode {
		ORTHOGONAL,
		PERSPECTIVE
	}
	
	@Getter private CameraMode cameraMode;
	
	@Getter private Vector2 cameraPosition;

	@Getter private float initZoom;

	@Getter private float minZoom, maxZoom;
	
	@Getter private RandomSeed seed;

	public LevelInitialSettings( CameraMode cameraMode,final Vector2 cameraPosition, float initZoom, float minZoom, float maxZoom, RandomSeed seed )
	{
		this.cameraMode = cameraMode;
		this.cameraPosition = cameraPosition;
		this.initZoom = initZoom;
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
		this.seed = seed;
	}


}
