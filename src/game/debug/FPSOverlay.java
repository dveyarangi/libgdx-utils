package game.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;

import game.systems.rendering.IRenderer;

public class FPSOverlay implements IOverlay
{
	long prevTime = TimeUtils.millis();
	long sinceSampling = 0;
	float averageFPS;
	
	private static float alpha = 0.95f;
	
	public FPSOverlay()
	{
		averageFPS = Gdx.graphics.getFramesPerSecond();
	}
	
	@Override
	public void draw(IRenderer renderer)
	{
		long delta = TimeUtils.timeSinceMillis(prevTime);
		
		averageFPS = averageFPS * alpha +  Gdx.graphics.getFramesPerSecond() * (1 - alpha);  
		
		sinceSampling += delta;
		if( sinceSampling >= 1000)
		{
			
			sinceSampling = 0;
			
			
			renderer.batch().begin();
			renderer.batch().setColor(1, 1, 1, 1);
	        Debug.FONT.draw(renderer.batch(), (int)averageFPS + " fps", 3, Gdx.graphics.getHeight() - 3);
	        renderer.batch().end();
		}
		
	}

	@Override
	public boolean isProjected() { return false; }

}
