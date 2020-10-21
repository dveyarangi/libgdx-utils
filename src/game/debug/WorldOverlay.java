package game.debug;

import game.world.Level;
import game.world.camera.ICameraProvider;

public abstract class WorldOverlay implements IOverlay<Level> 
{

	protected ICameraProvider cameraProvider;

	
	public void setCameraProvider(ICameraProvider cameraProvider)
	{
		this.cameraProvider = cameraProvider;
	}

	@Override public boolean useWorldCoordinates() { return true; }

}
