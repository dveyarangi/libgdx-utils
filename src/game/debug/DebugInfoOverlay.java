package game.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entry;
import com.badlogic.gdx.utils.TimeUtils;

import game.debug.Debug.OverlayBinding;
import game.systems.rendering.IRenderer;

public class DebugInfoOverlay implements IOverlay
{
	long prevTime = TimeUtils.millis();
	long sinceSampling = 0;
	float averageFPS;
	
	
	private IntMap<OverlayBinding> debugOverlays;
	
	private static float alpha = 0.95f;
	
	public DebugInfoOverlay(IntMap<OverlayBinding> debugOverlays)
	{
		averageFPS = Gdx.graphics.getFramesPerSecond();
		
		this.debugOverlays = new IntMap <> (debugOverlays);
	}
	
	@Override
	public void draw(IRenderer renderer)
	{
		long delta = TimeUtils.timeSinceMillis(prevTime);
		
		averageFPS = averageFPS * alpha +  Gdx.graphics.getFramesPerSecond() * (1 - alpha);  
		float ystep = Debug.FONT.getCapHeight() + 5;
		sinceSampling += delta;
		if( sinceSampling >= 1000)
		{
			
			sinceSampling = 0;
			
			float y = Gdx.graphics.getHeight() - 3;
			renderer.batch().begin();
	        Debug.FONT.draw(renderer.batch(), (int)averageFPS + " FPS", 3, y);
	        
	        y -= ystep;
	        Debug.FONT.draw(renderer.batch(), "Overlay hotkeys: ", 3, y);
	        
			for( Entry<OverlayBinding> entry : debugOverlays.entries() )
			{
				y -= ystep;
				String key = Keys.toString(entry.key);

				Debug.FONT.draw(renderer.batch(), " * " + key, 3, y);
				Debug.FONT.draw(renderer.batch(), " - " + entry.value.overlay.toDesc(), 25, y);
			}	        
	        renderer.batch().end();
		}
		
	}

	@Override
	public boolean useWorldCoordinates() { return false; }

	@Override
	public String toDesc()
	{
		return "debug info";
	}

}
