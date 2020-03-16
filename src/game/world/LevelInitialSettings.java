package game.world;

import com.badlogic.gdx.math.Vector2;

/**
 * Defines initial settings for a level.
 * 
 * @author Fima
 */
public class LevelInitialSettings
{

	private Vector2 cameraPosition;

	private float initZoom;

	private float minZoom, maxZoom;

	public LevelInitialSettings( final Vector2 cameraPosition, float initZoom, float minZoom, float maxZoom )
	{
		this.cameraPosition = cameraPosition;
		this.initZoom = initZoom;
		this.minZoom = minZoom;
		this.maxZoom = maxZoom;
	}

	public Vector2 getCameraPosition()
	{
		return cameraPosition;
	}

	public float getInitZoom()
	{
		return initZoom;
	}

	public float getMinZoom()
	{
		return minZoom;
	}

	public float getMaxZoom()
	{
		return maxZoom;
	}

}
