package game.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntMap.Entry;
import com.badlogic.gdx.utils.TimeUtils;

import game.debug.Debug.OverlayBinding;
import game.systems.rendering.IRenderer;
import game.world.Level;

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
		
		Level level = Debug.debug.level;
		
		averageFPS = averageFPS * alpha +  Gdx.graphics.getFramesPerSecond() * (1 - alpha);  
		float ystep = Debug.FONT.getCapHeight() + 5;
		sinceSampling += delta;
		if( sinceSampling >= 1000)
		{
			
			sinceSampling = 0;
			
			SpriteBatch sprites = renderer.sprites();
			float y = Gdx.graphics.getHeight() - 3;
			sprites.begin();
	        Debug.FONT.draw(sprites, (int)averageFPS + " FPS", 3, y);
	        
	        if(Debug.debug.level != null)
	        {
		        y -= ystep;
		        Debug.FONT.draw(sprites, "Entities: " + level.getEngine().getEntities().size(), 3, y);
	        }
	        
	        
	        Vector3 cameraPos = level.getModules().getCameraProvider().position();
	        float cameraZoom = level.getModules().getCameraProvider().zoom();
	        String cameraInfo = "Camera: " + cameraPos.x + "," + cameraPos.y + " z" + cameraZoom;
	        y -= ystep;
	        Debug.FONT.draw(sprites, cameraInfo, 3, y);
	        
	        y -= ystep;
	        Debug.FONT.draw(sprites, "Overlay hotkeys: ", 3, y);
	        
			for( Entry<OverlayBinding> entry : debugOverlays.entries() )
			{
				y -= ystep;
				String key = Keys.toString(entry.key);

				Debug.FONT.draw(sprites, " * " + key, 3, y);
				Debug.FONT.draw(sprites, " - " + entry.value.overlay.toDesc(), 25, y);
			}	        
			sprites.end();
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
